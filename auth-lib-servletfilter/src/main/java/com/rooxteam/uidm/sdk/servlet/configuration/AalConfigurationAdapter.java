package com.rooxteam.uidm.sdk.servlet.configuration;

import com.rooxteam.sso.aal.configuration.Configuration;
import com.rooxteam.uidm.sdk.servlet.util.StringUtils;

import javax.servlet.FilterConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AalConfigurationAdapter implements Configuration {
    private final FilterConfig config;

    public AalConfigurationAdapter(FilterConfig filterConfig) {
        this.config = filterConfig;
    }

    @Override
    public String getString(String property, String defaultValue) {
        return Optional.ofNullable(config.getInitParameter(property)).orElse(defaultValue);
    }

    @Override
    public String getString(String property) {
        return config.getInitParameter(property);
    }

    @Override
    public boolean getBoolean(String property, boolean defaultValue) {
        String prop = config.getInitParameter(property);
        if (prop != null) {
            return Boolean.parseBoolean(prop);
        } else {
            return defaultValue;
        }
    }

    @Override
    public int getInt(String property, int defaultValue) {
        String prop = config.getInitParameter(property);
        if (prop != null) {
            return Integer.parseInt(prop);
        } else {
            return defaultValue;
        }
    }

    @Override
    public List getList(String property) {
        String prop = config.getInitParameter(property);
        if (prop != null && !prop.isEmpty()) {
            return StringUtils.parseConfigValueAsList(prop);
        } else {
            return new ArrayList();
        }
    }

    @Override
    public String[] getStringArray(String tokenInfoAttributesForward) {
        return (String[]) this.getList(tokenInfoAttributesForward).toArray();
    }
}
