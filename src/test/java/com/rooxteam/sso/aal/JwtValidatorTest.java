package com.rooxteam.sso.aal;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.rooxteam.jwt.Checker;
import com.rooxteam.sso.aal.exception.AalException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;

import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.util.Calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public class JwtValidatorTest {

    private static final String TEST_PRIVATE_KEY = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAK0kHP1O+RgdgLSoWxk" +
            "uaYoi5Jic6hLKeuKw8WzCfsQ68ntBDf6tVOTn/kZA7Gjf4oJAL1dXLlxIEy+kZWnxT3FF+0MQ4WQYbGBfaW8LTM4uAOLLvYZ8SIVEXmxhJ" +
            "sSlvaiTWCbNFaOfiII8bhFp4551YB07NfpquUGEwOxOmci/AgMBAAECgYAwUJOGXDNGd3Ui9Jf3PuxUj8gaji8Db107RQUZxGx7dbeUjWL" +
            "KXrQB7HsYP6W43kDm9+I+DSXyumogMU/bcGsqSRo0JQzvRo+7l1rCSIryRHRs62m8VLWzu6YVwH6TVPK5c0upgq3CkLpJYdX9JWHihJyBk" +
            "UHUkMxQFmosXnc2gQJBAK2NWoZJT0d1pekU/LcO9tRHYUgBrU/7aadDpztGCjKALYaJW+S+QdMmCZZbp6u/g9cqnVH3QrBf1MxcYa/z4z0" +
            "CQQD/ZMOYsG8HLcub3kdHvOfuy9o1ssfy75fmNLszesR6xTro5pQOw0Nb/qmHZYVbKLJhTWDPcM5P80fzYLHq+OurAkEAnDWawTvC6+Lsz" +
            "nphbLM/X5pP6Wv4/wCf/DlckHFeRE4yq+F+oMjOnqqh/n/Iz0G1/TkSGPChSfc3pimQk7rFUQJAQlhp24OSBDpoV2CCAefYFJfOYv7DpJ+" +
            "LRVlTfEcbPY15BUHJGqCtIfvhDHRaFVlYJaDVUm5Kmkhn25i1/4WUqwJBAIth0Og1FmejpM3ctZHDJgwAMKU7/tWeDWjgkHBIzxT7Oj/T1" +
            "rfDL0Fv3TgDjWoRvO083AeIVJjfLMBsamSC71s=";

    private static final String TEST_JWT = "eyAiYWxnIjogIkhTMjU2IiwgImN0eSI6ICJKV1QiLCAidHlwIjogImp3dCIgfQ.eyAidG9rZW5O" +
            "YW1lIjogImlkX3Rva2VuIiwgImF6cCI6ICJ3ZWJhcGkiLCAic3ViIjogIjI1MDExMDEwMDAxNDQ4IiwgIm1zaXNkbiI6ICIyNTAxMTAxMD" +
            "AwMTQ0OCIsICJpc3MiOiAiUGNyZkF1dGhlbnRpY2F0aW9uU2VydmljZSIsICJ2ZXIiOiAiMS4wIiwgImlhdCI6IDE0NDI4NjE4NTgsICJl" +
            "eHAiOiAxNDQyODYxOTE4LCAidG9rZW5UeXBlIjogIkpXVFRva2VuIiwgImN1c3RvbWVySWQiOiAiZDg0YWYzMmUwNzg2NDY3YWIwNTc0Zj" +
            "FjMGJmZmY3NzUiLCAicmVhbG0iOiAiL2N1c3RvbWVyIiwgImF1dGhMZXZlbCI6IFsgIjIiIF0sICJhdWQiOiBbICJ3ZWJhcGkiIF0sICJy" +
            "ZW4iOiAxNDQyODYxOTE4LCAiYmlsbGluZ0lkIjogIjU4MDM4NjIiLCAianRpIjogIjg1MGQzZDAxLWZhNmUtNDg3YS04Y2E4LTNkNmUxNT" +
            "EzZGQ3NiIsICJpbXNpIjogIjI1MDExMDIwOTE0OTI4NCIsICJhdGgiOiAxNDQyODYxODU4IH0.iLM9zOqdbfOHgXgsK4lHBPYqzeDCtRXsKgcNZ08hwp0";


    private JwtParser mockJwtParser = mock(JwtParser.class);
    private JwtValidator validator;

    @Before
    public void setUp() {
        reset(mockJwtParser);
        validator = new JwtValidator(TEST_PRIVATE_KEY, mockJwtParser);
    }

    @Test
    public void should_be_valid_with_valid_jwt_and_expire_day_ahead() throws NoSuchAlgorithmException, JOSEException, InvalidKeySpecException, ParseException {
        SignedJWT mockJwt = mock(SignedJWT.class);
        when(mockJwtParser.parse(TEST_JWT))
                .thenReturn(mockJwt);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .expirationTime(calendar.getTime())
                .build();
        when(mockJwt.getJWTClaimsSet())
                .thenReturn(jwtClaimsSet);
        when(mockJwt.verify(any(MACVerifier.class)))
                .thenReturn(true);


        ValidationStatus status = validator.validate(TEST_JWT);
        assertNotNull(status);
        assertEquals(ValidationStatus.VALID, status);

        verify(mockJwtParser, times(1)).parse(TEST_JWT);
        verify(mockJwt, times(1)).getJWTClaimsSet();
        verify(mockJwt, times(1)).verify(any(MACVerifier.class));
    }

    @Test(expected = AalException.class)
    public void should_rethrow_suppressed_exception() throws NoSuchAlgorithmException, JOSEException, InvalidKeySpecException, ParseException {
        Whitebox.setInternalState(validator, "suppressionException", new AalException(mock(NoSuchAlgorithmException.class)));
        try {
            validator.validate(TEST_JWT);
        } catch (AalException e) {
            Throwable cause = e.getCause();
            assertNotNull(cause);
            throw e;
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_IllegalArgumentException_when_jwt_is_null() throws NoSuchAlgorithmException, JOSEException, InvalidKeySpecException, ParseException {
        try {
            validator.validate(null);
        } catch (IllegalArgumentException e) {
            assertEquals("JWT argument is missing.", e.getMessage());
            throw e;
        }
    }

    @Test
    public void should_be_expired_with_valid_jwt_and_expire_day_before_now() throws NoSuchAlgorithmException, JOSEException, InvalidKeySpecException, ParseException {
        SignedJWT mockJwt = mock(SignedJWT.class);
        when(mockJwtParser.parse(TEST_JWT))
                .thenReturn(mockJwt);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .expirationTime(calendar.getTime())
                .build();
        when(mockJwt.getJWTClaimsSet())
                .thenReturn(jwtClaimsSet);

        ValidationStatus status = validator.validate(TEST_JWT);
        assertNotNull(status);
        assertEquals(ValidationStatus.EXPIRED, status);

        verify(mockJwtParser, times(1)).parse(TEST_JWT);
        verify(mockJwt, times(1)).getJWTClaimsSet();
    }

    @Test
    public void should_return_invalid_signature_with_invalid_jwt() throws NoSuchAlgorithmException, JOSEException, InvalidKeySpecException, ParseException {
        SignedJWT mockJwt = mock(SignedJWT.class);
        when(mockJwtParser.parse(TEST_JWT))
                .thenReturn(mockJwt);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .expirationTime(calendar.getTime())
                .build();
        when(mockJwt.getJWTClaimsSet())
                .thenReturn(jwtClaimsSet);
        when(mockJwt.verify(any(MACVerifier.class)))
                .thenReturn(false);

        ValidationStatus status = validator.validate(TEST_JWT);
        assertNotNull(status);
        assertEquals(ValidationStatus.INVALID_SIGNATURE, status);

        verify(mockJwtParser, times(1)).parse(TEST_JWT);
        verify(mockJwt, times(1)).getJWTClaimsSet();
        verify(mockJwt, times(1)).verify(any(MACVerifier.class));
    }


    @Test(expected = IllegalStateException.class)
    public void should_handle_checker_IllegalStateException() throws NoSuchAlgorithmException, JOSEException, InvalidKeySpecException, ParseException, SignatureException {
        SignedJWT mockJwt = mock(SignedJWT.class);
        when(mockJwtParser.parse(TEST_JWT))
                .thenReturn(mockJwt);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .expirationTime(calendar.getTime())
                .build();
        when(mockJwt.getJWTClaimsSet())
                .thenReturn(jwtClaimsSet);
        when(mockJwt.verify(any(MACVerifier.class)))
                .thenReturn(false);

        Checker mockChecker = mock(Checker.class);
        doThrow(new IllegalStateException())
                .when(mockChecker).check(any(JWT.class));

        JwtValidator validator = new JwtValidator(TEST_PRIVATE_KEY, mockJwtParser, mockChecker);

        validator.validate(TEST_JWT);
    }

    @Test
    public void should_handle_checker_SignatureException() throws NoSuchAlgorithmException, JOSEException, InvalidKeySpecException, ParseException, SignatureException {
        SignedJWT mockJwt = mock(SignedJWT.class);
        when(mockJwtParser.parse(TEST_JWT))
                .thenReturn(mockJwt);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .expirationTime(calendar.getTime())
                .build();
        when(mockJwt.getJWTClaimsSet())
                .thenReturn(jwtClaimsSet);
        when(mockJwt.verify(any(MACVerifier.class)))
                .thenReturn(false);

        Checker mockChecker = mock(Checker.class);
        doThrow(new SignatureException())
                .when(mockChecker).check(any(JWT.class));

        JwtValidator validator = new JwtValidator(TEST_PRIVATE_KEY, mockJwtParser, mockChecker);

        ValidationStatus status = validator.validate(TEST_JWT);
        assertNotNull(status);
        assertEquals(ValidationStatus.INVALID_SIGNATURE, status);

        verify(mockJwtParser, times(1)).parse(TEST_JWT);
        verify(mockJwt, times(1)).getJWTClaimsSet();
    }

    @Test
    public void should_handle_checker_ParseException() throws NoSuchAlgorithmException, JOSEException, InvalidKeySpecException, ParseException, SignatureException {
        SignedJWT mockJwt = mock(SignedJWT.class);
        when(mockJwtParser.parse(TEST_JWT))
                .thenReturn(mockJwt);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .expirationTime(calendar.getTime())
                .build();
        when(mockJwt.getJWTClaimsSet())
                .thenReturn(jwtClaimsSet);
        when(mockJwt.verify(any(MACVerifier.class)))
                .thenReturn(false);

        Checker mockChecker = mock(Checker.class);
        doThrow(new ParseException("", 1))
                .when(mockChecker).check(any(JWT.class));

        JwtValidator validator = new JwtValidator(TEST_PRIVATE_KEY, mockJwtParser, mockChecker);

        ValidationStatus status = validator.validate(TEST_JWT);
        assertNotNull(status);
        assertEquals(ValidationStatus.INVALID_FORMAT, status);

        verify(mockJwtParser, times(1)).parse(TEST_JWT);
        verify(mockJwt, times(1)).getJWTClaimsSet();
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_IllegalArgumentException_when_jwt_parser_is_null() throws NoSuchAlgorithmException, JOSEException, InvalidKeySpecException, ParseException {
        new JwtValidator(null, (JwtParser) null, (Checker) null);
    }

    @Test(expected = AalException.class)
    public void should_suppress_NullPointerException_when_shared_key_is_null() throws NoSuchAlgorithmException, JOSEException, InvalidKeySpecException, ParseException {
        JwtValidator validator = new JwtValidator(null, mock(JwtParser.class));

        try {
            validator.validate(TEST_JWT);
        } catch (AalException e) {
            assertNotNull(e.getCause());
            assertEquals(IllegalArgumentException.class, e.getCause().getClass());
            throw e;
        }
    }

}
