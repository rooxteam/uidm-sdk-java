package com.rooxteam.sso.aal;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Ivan Volynkin
 *         ivolynkin@roox.ru
 */
public class PlainPrincipal extends AbstractPrincipal {
    private final String publicToken;
    private final Map<String, Object> sharedIdentityProperties = new HashMap<String, Object>();

    public PlainPrincipal(Map<String, Object> policyContext, String publicToken) {
        this.publicToken = publicToken;
        for (Map.Entry<String, Object> entry : policyContext.entrySet()) {
            Object value = entry.getValue();
            if (value != null) {
                sharedIdentityProperties.put(entry.getKey(), value);
            }
        }
    }

    @Override
    public Calendar getExpirationTime() {
        return null;
    }

    @Override
    public String getJwtToken() {
        return publicToken;
    }

    @Override
    protected Map<String, Object> getSharedIdentityProperties() {
        return sharedIdentityProperties;
    }

    @Override
    protected Map<String, Object> getPrivateIdentityProperties() {
        return new HashMap<String, Object>();
    }

    @Override
    protected Map<String, Object> getSessionProperties() {
        return new HashMap<String, Object>();
    }

    @Override
    public boolean isAnonymous() {
        return false;
    }
}
