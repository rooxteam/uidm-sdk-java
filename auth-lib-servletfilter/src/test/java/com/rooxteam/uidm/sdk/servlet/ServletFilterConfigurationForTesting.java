package com.rooxteam.uidm.sdk.servlet;

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
    private Map<String, List<String>> mapList = new TreeMap<>();
    {
        map.put(ConfigKeys.LOCAL_POLICIES, "{}");
        map.put(ConfigValues.REDIRECT_LOCATION_KEY, "localhost");
        map.put(ConfigKeys.SSO_URL, "localhost");

        List<String> props = new ArrayList<>();
        props.add("roles");
        props.add("scopes");
        props.add("authLevel");
        mapList.put(ConfigValues.PROPERTIES_KEY, props);

        List<String> headers = new ArrayList<>();
        headers.add("Roles");
        headers.add("Scopes");
        headers.add("AuthLevel");
        mapList.put(ConfigValues.HEADER_NAMES_OF_PROPERTIES_KEY, headers);
    }

    @Override
    public Set<String> getAuthorizationCookieNames() {
        Set<String> set = new TreeSet<>();
        set.add("at");
        return set;
    }

    @Override
    public String getString(String property) {
        return map.get(property);
    }

    @Override
    public List<String> getList(String property) {
        return mapList.get(property);
    }
}
