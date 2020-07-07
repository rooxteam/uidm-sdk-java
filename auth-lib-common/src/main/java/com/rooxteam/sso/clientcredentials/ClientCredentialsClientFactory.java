package com.rooxteam.sso.clientcredentials;

import com.rooxteam.compat.Objects;
import com.rooxteam.sso.aal.utils.StringUtils;
import com.rooxteam.sso.clientcredentials.configuration.Configuration;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;


@SuppressWarnings("unused")
public final class ClientCredentialsClientFactory {

    /**
     * Instantiate new instance of ClientCredentialsClient.
     * Use one instance of ClientCredentialsClient for each client_id.
     *
     * @param config configuration. Use Configuration Factory for instantiation from different sources
     * @return ClientCredentialsClient
     */
    public static ClientCredentialsClient create(final Configuration config) {
        return create(config, RestTemplateFactory.getDefaultConfiguredRestTemplate(config));
    }

    /**
     * Instantiate new instance of ClientCredentialsClient.
     * Use one instance of ClientCredentialsClient for each client_id.
     *
     * @param config       configuration. Use Configuration Factory for instantiation from different sources
     * @param restTemplate RestTemplate used for makng requests. When using this signature it's caller responsibility
     *                     to configure it properly. Otherwise use ClientCredentialsClient create(final Configuration
     *                     config) signature
     * @return ClientCredentialsClient
     */
    @SuppressWarnings("WeakerAccess")
    public static ClientCredentialsClient create(final Configuration config,
                                                 final RestTemplate restTemplate) {
        Objects.requireNonNull(config, "config");
        Objects.requireNonNull(config, "restTemplate");
        if (restTemplate.getErrorHandler() != null && !(restTemplate.getErrorHandler() instanceof DefaultResponseErrorHandler)) {
            throw new IllegalArgumentException("restTemplate can't have non-default error handler because this will fail " +
                    "client credentials token refresh");
        }

        final ClientCredentialsClientBuilder builder = new ClientCredentialsClientBuilder(
                restTemplate,
                config.getAccessTokenEndpoint(),
                config.getTokenValidationEndpoint(),
                config);

        if (!StringUtils.isEmpty(config.getHeaderPrefix())) {
            builder.headerPrefix(config.getHeaderPrefix());
        } else {
            builder.headerPrefix("Bearer ");
        }

        builder.params("client_id", config.getClientId());
        builder.params("client_secret", config.getClientSecret());
        builder.params("grant_type", "client_credentials");

        if (!StringUtils.isEmpty(config.getUidmRealm())) {
            builder.params("realm", config.getUidmRealm());
        }

        if (config.getAdditionalRequestParameters() != null) {
            builder.params(config.getAdditionalRequestParameters());
        }

        return builder.build();
    }


}
