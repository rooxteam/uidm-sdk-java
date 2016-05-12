package com.rooxteam.sso.aal;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import com.rooxteam.jwt.Checker;
import com.rooxteam.sso.aal.exception.AalException;
import org.apache.commons.codec.binary.Base64;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.rooxteam.sso.aal.AalLogger.LOG;

class JwtValidator {
    private static final String JWT_IS_MISSING_MESSAGE = "JWT argument is missing.";
    public static final String DEFAULT_ALGORITHM = "RSA";
    public static final String SHARED_KEY_ARGUMENT_IS_MISSING = "base64SharedKey argument is missing.";
    public static final String JWT_PARSER_ARGUMENT_IS_MISSING = "jwtParser argument is missing.";

    private JWSVerifier verifier;
    private JwtParser jwtParser;
    private AalException suppressionException;
    private final List<Checker> checkers = new ArrayList<>();

    JwtValidator(String base64SharedKey, Checker... checkers) {
        this(base64SharedKey, new JwtParser(), checkers);
    }

    JwtValidator(String base64PrivateKey, JwtParser jwtParser, Checker... checkers) {
        if (jwtParser == null) {
            throw new IllegalArgumentException(JWT_PARSER_ARGUMENT_IS_MISSING);
        }
        this.jwtParser = jwtParser;
        if (checkers != null) {
            this.checkers.addAll(Arrays.asList(checkers));
        }
        if (base64PrivateKey == null) {
            suppressionException = new AalException(new IllegalArgumentException(SHARED_KEY_ARGUMENT_IS_MISSING));
        } else {
            try {
                KeyFactory keyFactory = KeyFactory.getInstance(DEFAULT_ALGORITHM);
                byte[] decodedPrivateKey = Base64.decodeBase64(base64PrivateKey);
                PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(decodedPrivateKey);
                PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
                verifier = new MACVerifier(privateKey.getEncoded());
            } catch (JOSEException | NoSuchAlgorithmException | InvalidKeySpecException | RuntimeException e) {
                LOG.warnJwtValidatorGotSuppressedException(e);
                suppressionException = new AalException(e);
            }
        }
    }

    /**
     * Validate signature and expiration time of {@code jwt}
     *
     * @param jwt JWT
     * @return Status {@link com.rooxteam.sso.aal.ValidationStatus#VALID} if JWT has a valid signature.
     * If token is expired returns {@link com.rooxteam.sso.aal.ValidationStatus#EXPIRED};
     * otherwise {@link com.rooxteam.sso.aal.ValidationStatus#INVALID_SIGNATURE} or {@link com.rooxteam.sso.aal.ValidationStatus#INVALID_FORMAT}
     * @throws com.rooxteam.sso.aal.exception.AalException May contain the cause which is saved for later retrieval by the {@link java.lang.Exception#getCause()} method
     *                                                     in cases of absence of security algorithm or invalid shared key
     * @throws java.lang.IllegalArgumentException          If {@code jwt} is null
     */
    ValidationStatus validate(String jwt) {
        if (suppressionException != null) {
            throw suppressionException;
        }
        if (jwt == null) {
            throw new IllegalArgumentException(JWT_IS_MISSING_MESSAGE);
        }

        try {
            SignedJWT signedJwt = jwtParser.parse(jwt);
            Date expirationTime = signedJwt.getJWTClaimsSet().getExpirationTime();
            if (isExpired(expirationTime)) {
                return ValidationStatus.EXPIRED;
            }
            for (Checker checker : checkers) {
                checker.check(signedJwt);
            }
            boolean verify = signedJwt.verify(verifier);
            if (!verify) {
                return ValidationStatus.INVALID_SIGNATURE;
            }
            return ValidationStatus.VALID;
        } catch (JOSEException e) {
            LOG.errorSignatureVerifyingException(e);
            return ValidationStatus.INVALID_FORMAT;
        } catch (ParseException e) {
            LOG.errorSignatureParsingException(e);
            return ValidationStatus.INVALID_FORMAT;
        } catch (SignatureException e) {
            return ValidationStatus.INVALID_SIGNATURE;
        }
    }

    /**
     * Tests if {@code expirationTime} is expired or not
     *
     * @param expirationTime Expire time from token
     * @return true if {@code expirationTime} is null or {@code expirationTime} is strictly earlier than now (UTC time); false otherwise.
     */
    private boolean isExpired(Date expirationTime) {
        return expirationTime != null && expirationTime.before(new Date());
    }
}
