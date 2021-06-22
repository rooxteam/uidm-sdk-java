package com.rooxteam.uidm.sdk.spring.authentication;

import com.rooxteam.sso.aal.configuration.Configuration;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import javax.servlet.ServletRequest;

import static com.rooxteam.sso.aal.ConfigKeys.TOKEN_COOKIE_LEGACY_ROOX_PRODUCTS_KEY;
import static com.rooxteam.sso.aal.ConfigKeys.TOKEN_COOKIE_NAME_KEY;
import static com.rooxteam.sso.aal.ConfigKeys.USER_ATTRIBUTES_EXPOSE_TO_MDC;

public class ConfigBasedUserPreAuthFilterSettingsImpl implements UserPreAuthFilterSettings, EnvironmentAware {

    @Getter(AccessLevel.PROTECTED)
    private final Configuration config;

    @Setter
    private Environment environment;

    public ConfigBasedUserPreAuthFilterSettingsImpl(Configuration config) {
        this.config = config;
    }

    @Override
    public String factoryGetCookieName(ServletRequest request) {
        return config.getString(TOKEN_COOKIE_NAME_KEY,
                config.getString(TOKEN_COOKIE_LEGACY_ROOX_PRODUCTS_KEY));
    }

    @Override
    public String[] factoryGetPrincipalAttributesExposedToMDC(ServletRequest request) {
        return environment.getProperty(USER_ATTRIBUTES_EXPOSE_TO_MDC,
                String[].class,
                new String[0]);
    }
}
