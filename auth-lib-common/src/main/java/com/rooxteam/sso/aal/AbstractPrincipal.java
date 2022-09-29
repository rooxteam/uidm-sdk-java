package com.rooxteam.sso.aal;

import java.util.Map;

/**
 * @author Ivan Volynkin
 * ivolynkin@roox.ru
 */
public abstract class AbstractPrincipal implements Principal {


    @Override
    public Object getProperty(PropertyScope propertyScope, String name) {
        return getProperties().get(name);
    }

    @Override
    public Map<String, Object> getProperties(PropertyScope propertyScope) {
        return getProperties();
    }


    @Override
    public boolean isAnonymous() {
        return false;
    }
}
