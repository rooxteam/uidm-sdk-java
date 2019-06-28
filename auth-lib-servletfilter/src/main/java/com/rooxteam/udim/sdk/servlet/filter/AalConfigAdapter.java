package com.rooxteam.udim.sdk.servlet.filter;

import com.rooxteam.sso.aal.ConfigKeys;
import com.rooxteam.sso.aal.configuration.Configuration;
import com.rooxteam.udim.sdk.servlet.configuration.ConfigValues;
import com.rooxteam.udim.sdk.servlet.configuration.ServletFilterConfiguration;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;
import java.util.Optional;

/**
 * This wrapper intercepts calls by AAL for properties and fetches the necessary values.
 * This class provides a universal configuration interface and hides away implementation details.
 */
class AalConfigAdapter implements Configuration {
    private ServletFilterConfiguration configuration;

    AalConfigAdapter(ServletFilterConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public String getString(String property, String defaultValue) {
        return Optional.ofNullable(configuration.getString(property)).orElse(defaultValue);
    }

    @Override
    public String getString(String property) {
        return configuration.getString(translate(property));
    }

    @Override
    public boolean getBoolean(String property, boolean defaultValue) {
        throw new NotImplementedException();
    }

    @Override
    public int getInt(String property, int defaultValue) {
        throw new NotImplementedException();
    }

    @Override
    public List<String> getList(String property) {
        return configuration.getList(translate(property));
    }

    @Override
    public String[] getStringArray(String tokenInfoAttributesForward) {
        return (String[]) this.getList(tokenInfoAttributesForward).toArray();
    }

    private String translate(String prop) {
        switch (prop) {
            case ConfigKeys.TOKEN_INFO_ATTRIBUTES_FORWARD:
                return ConfigValues.PROPERTIES_KEY;
            case ConfigKeys.SSO_URL:
                return ConfigValues.SSO_URL;
            default:
                return prop;
        }
    }
}
