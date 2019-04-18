package com.rooxteam.uidm.sdk.spring.authentication;


import org.springframework.core.env.Environment;

public class DevTokenUtils {

    private static final String DEV_TOKEN_ENABLED_PARAM = "com.rooxteam.webapi.auth.dev-token.enabled";
    private static final boolean DEV_TOKEN_ENABLED_PARAM_DEFAULT = false;

    public static boolean isDevTokenEnabled(Environment config) {
        return config.getProperty(DEV_TOKEN_ENABLED_PARAM, Boolean.class, DEV_TOKEN_ENABLED_PARAM_DEFAULT);
    }
}
