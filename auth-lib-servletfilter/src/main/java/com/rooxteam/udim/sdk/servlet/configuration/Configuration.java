package com.rooxteam.udim.sdk.servlet.configuration;

public interface Configuration {
    String getPrincipalHeader();
    String getRedirectLocation();
    String getRolesHeader();
    String getAuthLevelHeader();
    String getExpiresInHeader();
    String getTokenInfoUrl();
    int getSocketTimeout();
    int getConnectionTimeout();
    int getConnectionRequestTimeout();
}
