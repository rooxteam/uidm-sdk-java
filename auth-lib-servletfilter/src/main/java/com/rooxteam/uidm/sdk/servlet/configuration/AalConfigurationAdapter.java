package com.rooxteam.uidm.sdk.servlet.configuration;

import com.rooxteam.sso.aal.configuration.Configuration;
import com.rooxteam.uidm.sdk.servlet.util.ParseStringUtils;

import javax.servlet.FilterConfig;
import java.util.ArrayList;
import java.util.List;

public class AalConfigurationAdapter implements Configuration {
    private final FilterConfig config;

    public AalConfigurationAdapter(FilterConfig filterConfig) {
        this.config = filterConfig;
    }

    @Override
    public String getString(String property,
                            String defaultValue) {
        if (config.getInitParameter(property) != null) {
            return config.getInitParameter(property);
        } else {
            return defaultValue;
        }
    }

    @Override
    public String getString(String property) {
        return config.getInitParameter(property);
    }

    @Override
    public boolean getBoolean(String property,
                              boolean defaultValue) {
        String prop = config.getInitParameter(property);
        if (prop != null) {
            return Boolean.parseBoolean(prop);
        } else {
            return defaultValue;
        }
    }

    @Override
    public int getInt(String property,
                      int defaultValue) {
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
            return ParseStringUtils.parseConfigValueAsList(prop);
        } else {
            return new ArrayList();
        }
    }

    @Override
    public String[] getStringArray(String tokenInfoAttributesForward) {
        String prop = config.getInitParameter(tokenInfoAttributesForward);
        if (prop != null) {
            List<String> list = ParseStringUtils.parseConfigValueAsList(prop);
            String[] arr = new String[list.size()];
            arr = list.toArray(arr);
            return arr;
        } else {
            return new String[0];
        }
    }
}
