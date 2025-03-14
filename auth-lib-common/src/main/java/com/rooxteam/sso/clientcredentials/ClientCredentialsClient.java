package com.rooxteam.sso.clientcredentials;

import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Main interface of this package, client to IDM that provides OAUTH2.0 Client Credentials grante.
 * <p>
 * Use ClientCredentialsClientFactory to instantiate ClientCredentialsClient.
 */
public interface ClientCredentialsClient {

    /**
     * Get valid token validating and refreshing previous if needed.*
     *
     * @param additionalRequestParameters additional request parameters
     * @return token
     * @deprecated Use {@link #getToken(java.util.Map)}
     */
    @Deprecated
    default String getToken(MultiValueMap<String, String> additionalRequestParameters) {
        return getToken(new HashMap<>(additionalRequestParameters));
    }

    /**
     * Get valid token validating and refreshing previous if needed
     *
     * @param additionalRequestParameters additional request parameters
     * @return token
     */
    String getToken(Map<String, List<String>> additionalRequestParameters);

    /**
     * Get valid token validating and refreshing previous if needed - using default params, passed to implementation on creation
     *
     * @return client token
     */
    String getToken();

    /**
     * Get valid token validating and refreshing previous if needed.*
     *
     * @param additionalRequestParameters additional request parameters
     * @return token
     */
    @Deprecated
    default String getAuthHeaderValue(MultiValueMap<String, String> additionalRequestParameters) {
        return getAuthHeaderValue(new HashMap<>(additionalRequestParameters));
    }

    /**
     * Get valid token validating and refreshing previous if needed.*
     *
     * @param additionalRequestParameters additional request parameters
     * @return token
     */
    String getAuthHeaderValue(Map<String, List<String>> additionalRequestParameters);

    /**
     * Get valid token validating and refreshing previous if needed - using default params, passed to implementation on creation
     *
     * @return value ready for usage as "Authorization" header value when making requests to API
     */
    String getAuthHeaderValue();
}
