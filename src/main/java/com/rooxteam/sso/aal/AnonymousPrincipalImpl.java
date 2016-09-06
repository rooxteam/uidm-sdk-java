package com.rooxteam.sso.aal;

import java.util.Calendar;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * Used for evaluation of anonymous context
 */
public class AnonymousPrincipalImpl implements com.rooxteam.sso.aal.Principal {



    @Override
    public Object getProperty(PropertyScope propertyScope, String name) {
        return getProperties(propertyScope).get(name);
    }

    @Override
    public Map<String, Object> getProperties(PropertyScope propertyScope) {
        return Collections.<String, Object>singletonMap("authLevel", Collections.singletonList("0"));
    }

    @Override
    public void setProperty(PropertyScope propertyScope, String name, Object value) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public Calendar getExpirationTime() {
        return Calendar.getInstance();
    }

    @Override
    public String getJwtToken() {
        return null;
    }

    @Override
    public boolean isAnonymous() {
        return true;
    }
}
