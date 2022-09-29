package com.rooxteam.uidm.sdk.spring.authentication;


import com.rooxteam.sso.aal.configuration.Configuration;
import org.springframework.core.env.Environment;

public class DevTokenUtils {

    private static final String DEV_TOKEN_ENABLED_PARAM = "com.rooxteam.webapi.auth.dev-token.enabled";
    private static final boolean DEV_TOKEN_ENABLED_PARAM_DEFAULT = false;

    public static boolean isDevTokenEnabled(Configuration config) {
        return config.getBoolean(DEV_TOKEN_ENABLED_PARAM, DEV_TOKEN_ENABLED_PARAM_DEFAULT);
    }
}
