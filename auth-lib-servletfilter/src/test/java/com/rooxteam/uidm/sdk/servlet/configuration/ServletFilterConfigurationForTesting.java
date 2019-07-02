package com.rooxteam.uidm.sdk.servlet.configuration;

import com.rooxteam.sso.aal.ConfigKeys;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;

public class ServletFilterConfigurationForTesting implements FilterConfig {
    private Map<String, String> map = new TreeMap<>();
    {
        map.put(ConfigKeys.LOCAL_POLICIES, "{}");
        map.put(FilterConfigKeys.REDIRECT_LOCATION_KEY, "localhost");
        map.put(ConfigKeys.SSO_URL, "localhost");
        map.put(FilterConfigKeys.CLAIMS_HEADERS_MAP_KEY, "roles=Roles,scopes=Scopes");
        map.put(FilterConfigKeys.CLAIMS_ATTRIBUTES_MAP_KEY, "roles=Roles,scopes=Scopes");
        map.put(FilterConfigKeys.AUTHORIZATION_COOKIE_NAMES_KEY, "at");
    }

    @Override
    public String getFilterName() {
        return "testFilter";
    }

    @Override
    public ServletContext getServletContext() {
        return null;
    }

    @Override
    public String getInitParameter(String s) {
        return map.get(s);
    }

    @Override
    public Enumeration getInitParameterNames() {
        return Collections.enumeration(map.keySet());
    }
}
