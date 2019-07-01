package com.rooxteam.uidm.sdk.servlet.configuration;

public interface FilterConfigKeys {
    /**
     * Arrays of comma denominated String values.
     */
    String CLAIMS_KEY = "com.rooxteam.uidm.sdk.servlet.filter.forwarded_token_claims";

    /**
     * Arrays of comma denominated String values.
     */
    String HEADER_NAMES_OF_CLAIMS_KEY = "com.rooxteam.uidm.sdk.servlet.filter.forwarded_token_claims_header_names";

    /**
     * Arrays of comma denominated String values.
     */
    String ATTRIBUTE_NAMES_OF_CLAIMS_KEY = "com.rooxteam.uidm.sdk.servlet.filter.forwarded_token_claims_attribute_names";

    /**
     * Array of comma denominated names of authorization cookies. Access token extracted from the cookie with leftmost name in the array has priority.
     */
    String AUTHORIZATION_COOKIE_NAMES_KEY = "com.rooxteam.uidm.sdk.servlet.filter.authorization_cookie_names";

    /**
     * Url to redirect to.
     */
    String REDIRECT_LOCATION_KEY = "com.rooxteam.uidm.sdk.servlet.filter.redirect_location";
}
