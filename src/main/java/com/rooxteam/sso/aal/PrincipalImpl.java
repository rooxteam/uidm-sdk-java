package com.rooxteam.sso.aal;

import com.rooxteam.sso.aal.client.TokenHelper;
import com.rooxteam.sso.aal.exception.AuthenticationException;
import org.forgerock.json.fluent.JsonValue;
import org.forgerock.json.jose.common.JwtReconstruction;
import org.forgerock.json.jose.jws.JwsAlgorithm;
import org.forgerock.json.jose.jws.SignedJwt;
import org.forgerock.json.jose.jwt.JwtClaimsSet;

import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.rooxteam.sso.aal.AalLogger.LOG;

/**
 * Represents validated non-anonymous user
 *
 * @author Dmitry Tikhonov
 */
public final class PrincipalImpl extends AbstractPrincipal {

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
            SignedJwt signedJwt = new JwtReconstruction().reconstructJwt(policyContextJwtToken, SignedJwt.class);
            if (signedJwt.getHeader().getAlgorithm() == JwsAlgorithm.NONE) {
                throw new AuthenticationException("alg not supported");
            }
            expirationTime = TokenHelper.expires(signedJwt);
            JwtClaimsSet claimsSet = signedJwt.getClaimsSet();
            for (String claimKey : claimsSet.keys()) {
                JsonValue claimValue = claimsSet.get(claimKey);
                if (claimValue != null) {
                    Object claimValueObject = claimValue.getObject();
                    sharedIdentityProperties.put(claimKey, claimValueObject);
                }
            }
        } catch (RuntimeException e) {
            LOG.errorCannotParseJwt(policyContextJwtToken, e);
            throw e;
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
