package com.rooxteam.udim.sdk.servlet.configuration;

public interface ConfigValues {
    /**
     * Support keys are, but not limited to, authLevel, realm, prn, roles, expires_in, scopes.
     */
    String PROPERTIES_KEY = "com.rooxteam.uidm.sdk.auth.filter.forward_token_properties";
    String HEADER_NAMES_OF_PROPERTIES_KEY = "com.rooxteam.uidm.sdk.auth.filter.forward_token_properties_header_names";
    String REDIRECT_LOCATION_KEY = "com.rooxteam.uidm.sdk.auth.filter.redirect_location";
    String SSO_URL = "com.rooxteam.uidm.sdk.auth.filter.sso_endpoint";
}
