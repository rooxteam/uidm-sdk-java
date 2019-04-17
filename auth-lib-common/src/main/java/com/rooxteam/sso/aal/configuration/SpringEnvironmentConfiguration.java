package com.rooxteam.sso.aal.configuration;

import org.springframework.core.env.Environment;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Адаптер конфигурации, принимающий Spring Environment
 */
@SuppressWarnings("unused")
final class SpringEnvironmentConfiguration implements Configuration {


    private final Environment environment;

    public SpringEnvironmentConfiguration(Environment environment) {
        Objects.requireNonNull(environment);
        this.environment = environment;
    }


    @Override
    public String getString(String property, String defaultValue) {
        return environment.getProperty(property, defaultValue);
    }

    @Override
    public String getString(String property) {
        return environment.getProperty(property);
    }

    @Override
    public boolean getBoolean(String property, boolean defaultValue) {
        return environment.getProperty(property, Boolean.class, defaultValue);
    }

    @Override
    public int getInt(String property, int defaultValue) {
        return environment.getProperty(property, Integer.class, defaultValue);
    }

    @Override
    public List getList(String property) {
        return environment.getProperty(property, List.class, Collections.EMPTY_LIST);
    }

    @Override
    public String[] getStringArray(String property) {
        return environment.getProperty(property, String[].class, new String[0]);
    }
}
