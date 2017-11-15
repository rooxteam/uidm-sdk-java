package com.rooxteam.sso.aal;

import java.util.Map;

/**
 * @author Ivan Volynkin
 *         ivolynkin@roox.ru
 */
public abstract class AbstractPrincipal implements Principal {

    @Override
    public Object getProperty(PropertyScope propertyScope, String name) {
        return getProperties(propertyScope).get(name);
    }

    @Override
    public Map<String, Object> getProperties(PropertyScope propertyScope) {
        switch (propertyScope) {
            case SHARED_IDENTITY_PARAMS:
                return getSharedIdentityProperties();
            case PRIVATE_IDENTITY_PARAMS:
                return getPrivateIdentityProperties();
            case SESSION_PARAMS:
                return getSessionProperties();
        }
        throw new IllegalArgumentException("Unknown property scope " + propertyScope);
    }


    protected abstract Map<String, Object> getSharedIdentityProperties();

    protected abstract Map<String, Object> getPrivateIdentityProperties();

    protected abstract Map<String, Object> getSessionProperties();

    @Override
    public void setProperty(PropertyScope propertyScope, String name, Object value) {
        getProperties(propertyScope).put(name, value);
    }

    @Override
    public boolean isAnonymous() {
        return false;
    }
}
