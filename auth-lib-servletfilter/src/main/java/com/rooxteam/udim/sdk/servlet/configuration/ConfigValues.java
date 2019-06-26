package com.rooxteam.udim.sdk.servlet.configuration;

public interface ConfigValues {
    String PRINCIPAL_KEY = "com.rooxteam.uidm.sdk.auth.filter.headers.token_principal";
    String REDIRECT_LOCATION_KEY = "com.rooxteam.uidm.sdk.auth.filter.redirect_location";
    String ROLES_KEY = "com.rooxteam.uidm.sdk.auth.filter.headers.token_roles";
    String EXPIRES_IN_KEY = "com.rooxteam.uidm.sdk.auth.filter.headers.expires_in";
    String AUTH_LEVEL_KEY = "com.rooxteam.uidm.sdk.auth.filter.headers.auth_level";
    String TOKEN_INFO_URL_KEY = "com.rooxteam.uidm.sdk.auth.filter.tokeninfo_url";
}
