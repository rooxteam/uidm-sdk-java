package com.rooxteam.sso.aal.configuration;

import com.rooxteam.sso.aal.exception.AalException;

import java.util.Map;

public final class ConfigurationBuilder {

    public static Configuration fromMap(Map<String, Object> map) {
        return new MapConfigurationImpl(map);
    }

    public static Configuration fromSpringEnvironment(Object environment) {
        Class environmentClass;
        try {
            environmentClass = Class.forName("org.springframework.core.env.Environment");
        } catch (ClassNotFoundException e) {
            throw new AalException("Can't instantiate AAL from Spring because library is not on classpath", e);
        }
        if (!environmentClass.isInstance(environment)) {
            throw new AalException("Can't instantiate AAL from Spring because object passed is not " +
                    "an instance of org.springframework.core.env.Environment");
        }
        Class configClass;
        try {
            configClass = Class.forName("com.rooxteam.sso.aal.configuration.SpringEnvironmentConfiguration");
        } catch (ClassNotFoundException e) {
            throw new AalException("Can't instantiate AAL from Spring because library is not on classpath", e);
        }
        try {
            return (Configuration) configClass.getConstructor(environmentClass).newInstance(environment);
        } catch (Exception e) {
            throw new AalException("Can't instantiate AAL from Spring", e);
        }
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
