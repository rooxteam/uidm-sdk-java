package com.rooxteam.uidm.sdk.spring.authentication;

import com.rooxteam.sso.aal.ConfigKeys;
import com.rooxteam.sso.aal.configuration.Configuration;
import com.rooxteam.sso.aal.utils.StringUtils;
import com.rooxteam.uidm.sdk.spring.UidmSdkSpringLogger;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import static com.rooxteam.sso.aal.ConfigKeys.TOKEN_COOKIE_LEGACY_ROOX_PRODUCTS_KEY;
import static com.rooxteam.sso.aal.ConfigKeys.TOKEN_COOKIE_NAME_KEY;
import static com.rooxteam.sso.aal.ConfigKeys.TOKEN_COOKIE_NAME_PER_CLIENT_KEY;
import static com.rooxteam.sso.aal.ConfigKeys.USER_ATTRIBUTES_EXPOSE_TO_MDC;

public class RooxConfigBasedUserPreAuthFilterSettingsImpl implements UserPreAuthFilterSettings, EnvironmentAware {

    @Getter(AccessLevel.PROTECTED)
    private final Configuration config;

    @Setter
    private Environment environment;

    public RooxConfigBasedUserPreAuthFilterSettingsImpl(Configuration config) {
        this.config = config;
    }

    @Override
    public String getCookieName() {
        return config.getString(TOKEN_COOKIE_NAME_KEY,
                config.getString(TOKEN_COOKIE_LEGACY_ROOX_PRODUCTS_KEY));
    }

    @Override
    public String getCookieName(String clientId) {
        if (StringUtils.isNotEmpty(clientId)) {
            String cookieName = getConfig().getString(TOKEN_COOKIE_NAME_PER_CLIENT_KEY.replace("{clientId}", clientId));
            if (StringUtils.isNotEmpty(cookieName)) {
                UidmSdkSpringLogger.LOG.debugv("Resolved token cookie name for client_id ''{0}'': {1}", clientId, cookieName);
                return cookieName;
            }
        }
        return getCookieName();
    }

    @Override
    public String[] getPrincipalAttributesExposedToMDC() {
        return environment.getProperty(USER_ATTRIBUTES_EXPOSE_TO_MDC,
                String[].class,
                new String[0]);
    }

    @Override
    public String getDefaultClientId() {
        return getConfig().getString(ConfigKeys.CLIENT_ID, "");
    }
}
