package com.rooxteam.sso.aal.configuration;

import com.rooxteam.sso.aal.exception.AalException;

import java.util.Map;

public final class ConfigurationBuilder {

    public static Configuration fromMap(Map<String, Object> map) {
        return new MapConfigurationImpl(map);
    }

    public static Configuration fromApacheCommonsConfiguration(Object config) {
        Class apacheConfiguration;
        try {
            apacheConfiguration = Class.forName("org.apache.commons.configuration.Configuration");
        } catch (ClassNotFoundException e) {
            throw new AalException("Can't instantiate AAL from Apache Commons Configuration because library is not on classpath", e);
        }
        if (!apacheConfiguration.isInstance(config)) {
            throw new AalException("Can't instantiate AAL from Apache Commons Configuration because object passed is not " +
                    "an instance of org.apache.commons.configuration.Configuration");
        }
        Class configClass;
        try {
            configClass = Class.forName("com.rooxteam.sso.aal.configuration.ApacheCommonsConfiguration");
        } catch (ClassNotFoundException e) {
            throw new AalException("Can't instantiate AAL from Apache Commons Configuration because library is not on classpath", e);
        }
        try {
            return (Configuration) configClass.getConstructor(apacheConfiguration).newInstance(config);
        } catch (Exception e) {
            throw new AalException("Can't instantiate AAL from Apache Commons Configuration", e);
        }
    }
}
