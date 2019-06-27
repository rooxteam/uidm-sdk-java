package com.rooxteam.udim.sdk.servlet.testing;

import com.rooxteam.sso.aal.ConfigKeys;
import com.rooxteam.udim.sdk.servlet.configuration.ConfigValues;
import com.rooxteam.udim.sdk.servlet.configuration.ServletFilterConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class ServletFilterConfigurationForTesting implements ServletFilterConfiguration {
    private Map<String, String> map = new TreeMap<>();
    {
        map.put(ConfigKeys.LOCAL_POLICIES, "{}");
        map.put(ConfigValues.AUTH_LEVEL_KEY, "AuthLevel");
        map.put(ConfigValues.EXPIRES_IN_KEY, "ExpiresIn");
        map.put(ConfigValues.PRINCIPAL_KEY, "Principal");
        map.put(ConfigValues.REDIRECT_LOCATION_KEY, "localhost");
        map.put(ConfigValues.ROLES_KEY, "Roles");
        map.put(ConfigValues.SCOPES_KEY, "Scopes");
        map.put(ConfigKeys.SSO_URL, "localhost");
    }

    @Override
    public String getString(String property, String defaultValue) {
        return defaultValue;
    }

    @Override
    public String getString(String property) {
        return map.get(property);
    }

    @Override
    public boolean getBoolean(String property, boolean defaultValue) {
        return defaultValue;
    }

    @Override
    public int getInt(String property, int defaultValue) {
        return defaultValue;
    }

    @Override
    public List getList(String property) {
        return new ArrayList();
    }

    @Override
    public String[] getStringArray(String tokenInfoAttributesForward) {
        String[] strs = new String[2];
        strs[0] = "roles";
        strs[0] = "scopes";
        return strs;
    }

    @Override
    public Set<String> getAuthorizationCookieNames() {
        Set<String> set = new TreeSet<>();
        set.add("at");
        return set;
    }
}
