package com.rooxteam.udim.sdk.servlet.configuration;

import com.rooxteam.config.RooxConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class ServletFilterConfigurationForRooxConfig implements ServletFilterConfiguration {
    private RooxConfig rooxConfig;

    public ServletFilterConfigurationForRooxConfig(RooxConfig rooxConfig) {
        this.rooxConfig = rooxConfig;
    }

    @Override
    public String getString(String property, String defaultValue) {
        return rooxConfig.getString(property, defaultValue);
    }

    @Override
    public String getString(String property) {
        return rooxConfig.getString(property);
    }

    @Override
    public boolean getBoolean(String property, boolean defaultValue) {
        return rooxConfig.getBoolean(property, defaultValue);
    }

    @Override
    public int getInt(String property, int defaultValue) {
        return rooxConfig.getInt(property, defaultValue);
    }

    @Override
    public List getList(String property) {
        return rooxConfig.getList(property, new ArrayList<>());
    }

    @Override
    public String[] getStringArray(String tokenInfoAttributesForward) {
        return (String[]) getList(tokenInfoAttributesForward).toArray();
    }

    @Override
    public Set<String> getAuthorizationCookieNames() {
        Set<String> set = new TreeSet<>();
        set.add("at");
        return set;
    }
}
