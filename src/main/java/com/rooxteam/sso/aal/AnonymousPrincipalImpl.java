package com.rooxteam.sso.aal;

import java.util.Calendar;
import java.util.Collections;
import java.util.Objects;

/**
 * Used for evaluation of anonymous context
 */
public class AnonymousPrincipalImpl implements com.rooxteam.sso.aal.Principal {
    @Override
    public Object getProperty(PropertyScope propertyScope, String name) {
        if (Objects.equals(name,"authLevel")){
            return Collections.singletonList("0");
        }else{
            return null;
        }
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
