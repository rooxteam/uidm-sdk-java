package com.rooxteam.sso.clientcredentials;

import org.springframework.util.MultiValueMap;

/**
 * Main interface of this package, client to IDM that provides OAUTH2.0 Client Credentials grante.
 * <p>
 * Use ClientCredentialsClientFactory to instantiate ClientCredentialsClient.
 *
 */
public interface ClientCredentialsClient {

    /**
     * Get valid token validating and refreshing previous if needed
     * @param additionalRequestParameters
     * @return
     */
    String getToken(MultiValueMap<String, String> additionalRequestParameters) throws ClientAuthenticationException;

    /**
     * Get valid token validating and refreshing previous if needed - using default params, passed to implementation on creation
     * @return client token
     */
    String getToken() throws ClientAuthenticationException;

    /**
     * Get valid token validating and refreshing previous if needed.*
     * @param additionalRequestParameters
     * @return
     */
    String getAuthHeaderValue(MultiValueMap<String, String> additionalRequestParameters) throws ClientAuthenticationException;

    /**
     * Get valid token validating and refreshing previous if needed - using default params, passed to implementation on creation
     * @return value ready for usage as "Authorization" header value when making requests to API
     */
    String getAuthHeaderValue() throws ClientAuthenticationException;
}
