package com.rooxteam.uidm.sdk.servlet.configuration;

public interface FilterConfigKeys {
    /**
     * Key which value is a dictionary of token claims that are placed in the request in corresponding headers.
     */
    String CLAIMS_HEADERS_MAP_KEY = "com.rooxteam.uidm.sdk.servlet.filter.token_claims_header_names";

    /**
     * Key which value is a dictionary of token claims that are placed in request in corresponding attributes.
     */
    String CLAIMS_ATTRIBUTES_MAP_KEY = "com.rooxteam.uidm.sdk.servlet.filter.token_claims_attribute_names";

    /**
     * Key which value is an array of cookie names where the access token can be stored.
     */
    String AUTHORIZATION_COOKIE_NAMES_KEY = "com.rooxteam.uidm.sdk.servlet.filter.authorization_cookie_names";

    /**
     * Redirection url in case of authentication failure.
     */
    String REDIRECT_LOCATION_KEY = "com.rooxteam.uidm.sdk.servlet.filter.redirect_location";
}
