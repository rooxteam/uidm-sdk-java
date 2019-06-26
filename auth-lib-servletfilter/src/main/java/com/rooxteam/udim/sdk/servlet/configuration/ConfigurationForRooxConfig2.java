package com.rooxteam.udim.sdk.servlet.configuration;

import com.rooxteam.config.RooxConfig;

public class ConfigurationForRooxConfig2 implements Configuration {
    private RooxConfig rooxConfig;

    public ConfigurationForRooxConfig2(RooxConfig rooxConfig) {
        this.rooxConfig = rooxConfig;
    }

    @Override
    public String getPrincipalHeader() {
        return rooxConfig.getString(ConfigValues.PRINCIPAL_KEY, "Principal");
    }

    @Override
    public String getRedirectLocation() {
        return rooxConfig.getString(ConfigValues.REDIRECT_LOCATION_KEY, "Location");
    }

    @Override
    public String getRolesHeader() {
        return rooxConfig.getString(ConfigValues.ROLES_KEY, "Roles");
    }

    @Override
    public String getAuthLevelHeader() {
        return rooxConfig.getString(ConfigValues.AUTH_LEVEL_KEY, "AuthLevel");
    }

    @Override
    public String getExpiresInHeader() {
        return rooxConfig.getString(ConfigValues.EXPIRES_IN_KEY, "ExpiresIn");
    }

    @Override
    public String getTokenInfoUrl() {
        return rooxConfig.getString(ConfigValues.TOKEN_INFO_URL_KEY, "test");
    }

    //TODO config
    @Override
    public int getSocketTimeout() {
        return 1000;
    }

    @Override
    public int getConnectionTimeout() {
        return 1000;
    }

    @Override
    public int getConnectionRequestTimeout() {
        return 1000;
    }
}
