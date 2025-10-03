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
    private final Map<String, Object> properties = new HashMap<String, Object>();

    public PlainPrincipal(Map<String, Object> policyContext, String publicToken) {
        this.publicToken = publicToken;
        for (Map.Entry<String, Object> entry : policyContext.entrySet()) {
            Object value = entry.getValue();
            if (value != null) {
                properties.put(entry.getKey(), value);
            }
        }
    }

    @Override
    public Object getProperty(String name) {
        return properties.get(name);
    }

    @Override
    public Map<String, Object> getProperties() {
        return properties;
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
    public boolean isAnonymous() {
        return false;
    }
}
