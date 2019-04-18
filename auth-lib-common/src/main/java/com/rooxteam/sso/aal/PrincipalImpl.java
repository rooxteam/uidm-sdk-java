package com.rooxteam.sso.aal;

import com.nimbusds.jose.Algorithm;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.rooxteam.sso.aal.client.TokenHelper;
import com.rooxteam.sso.aal.exception.AalException;
import com.rooxteam.sso.aal.exception.AuthenticationException;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.rooxteam.sso.aal.AalLogger.LOG;

/**
 * Represents validated non-anonymous user
 *
 * @author Dmitry Tikhonov
 */
public class PrincipalImpl extends AbstractPrincipal {

    private final Map<String, Object> sharedIdentityProperties = new ConcurrentHashMap<>();
    private final Map<String, Object> privateIdentityProperties = new ConcurrentHashMap<>();
    private final Map<String, Object> sessionProperties = new ConcurrentHashMap<>();
    private final String policyContextJwtToken;
    private final String publicJwtToken;
    private Calendar expirationTime;


    public PrincipalImpl(String policyContextJwtToken, String publicJwtToken) {
        if (policyContextJwtToken == null || policyContextJwtToken.trim().isEmpty()) {
            throw new IllegalArgumentException("policyContextJwtToken");
        }
        this.policyContextJwtToken = policyContextJwtToken;
        initContextFromJwt();
        if (publicJwtToken != null) {
            this.publicJwtToken = publicJwtToken;
        } else {
            this.publicJwtToken = policyContextJwtToken;
        }
    }

    public PrincipalImpl(String publicJwtToken, Map<String, Object> sharedIdentityProperties, Calendar expirationTime) {
        this.policyContextJwtToken = publicJwtToken;
        this.publicJwtToken = publicJwtToken;
        for (Map.Entry<String, Object> entry : sharedIdentityProperties.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (key != null && value != null) {
                this.sharedIdentityProperties.put(key, value);
            }
        }
        this.expirationTime = expirationTime;
    }

    @Override
    public String getJwtToken() {
        return publicJwtToken;
    }

    /**
     * Возвращает JWT токен как результат текущей аутентификации.
     * Является не публичным API методом. Служит только для передачи контекста аутентификации в логин-модуль SSO
     * для вычисления политик
     *
     * @return JWT токен, хранящий текущее состояние аутентификации
     */
    public String getPrivateJwtToken() {
        return policyContextJwtToken;
    }


    @Override
    public Calendar getExpirationTime() {
        return expirationTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PrincipalImpl that = (PrincipalImpl) o;

        return !(policyContextJwtToken != null ? !policyContextJwtToken.equals(that.policyContextJwtToken) : that.policyContextJwtToken != null);
    }

    @Override
    public int hashCode() {
        return policyContextJwtToken != null ? policyContextJwtToken.hashCode() : 0;
    }


    private void initContextFromJwt() {
        try {
            JWT signedJwt = JWTParser.parse(policyContextJwtToken);
            if (signedJwt.getHeader().getAlgorithm() == Algorithm.NONE) {
                throw new AuthenticationException("alg not supported");
            }
            expirationTime = TokenHelper.expires(signedJwt);
            JWTClaimsSet claimsSet = signedJwt.getJWTClaimsSet();
            claimsSet.getClaims().forEach((key, value) -> {
                Object claimValue = claimsSet.getClaim(key);
                if (claimValue != null) {
                    sharedIdentityProperties.put(key, claimValue);
                }
            });
        } catch (RuntimeException | ParseException e) {
            LOG.errorCannotParseJwt(policyContextJwtToken, e);
            throw new AalException(e);
        }
    }

    @Override
    protected Map<String, Object> getSharedIdentityProperties() {
        return sharedIdentityProperties;
    }

    @Override
    protected Map<String, Object> getPrivateIdentityProperties() {
        return privateIdentityProperties;
    }

    @Override
    protected Map<String, Object> getSessionProperties() {
        return sessionProperties;
    }
}
