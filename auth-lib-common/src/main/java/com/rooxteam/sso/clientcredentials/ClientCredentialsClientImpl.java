package com.rooxteam.sso.clientcredentials;

import com.rooxteam.compat.Objects;
import com.rooxteam.sso.aal.ConfigKeys;
import com.rooxteam.sso.aal.exception.AuthenticationException;
import com.rooxteam.sso.aal.exception.NetworkErrorException;
import com.rooxteam.sso.clientcredentials.configuration.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

import static com.rooxteam.sso.clientcredentials.ClientCredentialsClientLogger.LOG;


final class ClientCredentialsClientImpl implements ClientCredentialsClient {
    private final RestTemplate restTemplate;
    private final URI accessTokenEndpoint;
    private final URI tokenValidationEndpoint;
    private final MultiValueMap<String, String> defaultParameters;
    private final String headerPrefix;
    private final Configuration configuration;

    private final ConcurrentHashMap<MultiValueMap<String, String>, String> tokens =
            new ConcurrentHashMap<MultiValueMap<String, String>, String>();


    ClientCredentialsClientImpl(final RestTemplate restTemplate,
                                final URI accessTokenEndpoint,
                                final URI tokenValidationEndpoint,
                                final MultiValueMap<String, String> defaultParameters,
                                final String headerPrefix,
                                final Configuration configuration) {
        this.restTemplate = Objects.requireNonNull(restTemplate, "restTemplate");
        this.accessTokenEndpoint = Objects.requireNonNull(accessTokenEndpoint, "accessTokenEndpoint");
        this.tokenValidationEndpoint = Objects.requireNonNull(tokenValidationEndpoint, "tokenValidationEndpoint");
        if (defaultParameters != null) {
            this.defaultParameters = defaultParameters;
        } else {
            this.defaultParameters = new LinkedMultiValueMap<String, String>();
        }
        this.headerPrefix = headerPrefix;
        this.configuration = configuration;
    }

    @Override
    public String getAuthHeaderValue(MultiValueMap<String, String> params) {
        String token = getTokenValidating(params);
        return headerPrefix + token;
    }

    @Override
    public String getToken(MultiValueMap<String, String> params) {
        return getTokenValidating(params);
    }

    @Override
    public String getAuthHeaderValue() {
        String token = getTokenValidating(defaultParameters);
        return headerPrefix + token;
    }

    @Override
    public String getToken() {
        return getTokenValidating(defaultParameters);
    }


    private boolean isExpired(String token) {
        final String tokenForLogging = trimTokenForLogging(token);
        final URI uri = UriComponentsBuilder.fromUri(tokenValidationEndpoint)
                .queryParam("access_token", token)
                .build().toUri();
        try {
            restTemplate.getForEntity(uri, Object.class);
            return false;
        } catch (HttpStatusCodeException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                LOG.traceOnValidatingTokenTokenExpired(tokenForLogging);
            } else if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
                LOG.traceOnValidatingTokenTokenForbidden(tokenForLogging);
            } else {
                LOG.errorOnValidatingTokenHttp(tokenValidationEndpoint,
                        tokenForLogging,
                        e.getStatusCode(),
                        trimBodyForLogging(e.getResponseBodyAsString()),
                        e);
            }
            return true;
        } catch (ResourceAccessException e) {
            LOG.errorOnValidatingTokenIO(tokenValidationEndpoint,
                    tokenForLogging,
                    ConfigKeys.HTTP_CONNECTION_TIMEOUT,
                    configuration.getConnectTimeout(),
                    ConfigKeys.HTTP_SOCKET_TIMEOUT,
                    configuration.getReadTimeout(),
                    e);
            return true;
        } catch (Exception e) {
            LOG.errorOnValidatingToken(tokenValidationEndpoint, tokenForLogging, e);
            return true;
        }
    }

    private String getTokenValidating(MultiValueMap<String, String> requestParameters) {

        MultiValueMap<String, String> mergedParams = new LinkedMultiValueMap<String, String>();
        mergedParams.putAll(this.defaultParameters);
        mergedParams.putAll(requestParameters);

        MultiValueMap<String, String> paramsForLogging = clearParamsForLogging(mergedParams);

        LOG.traceGetToken(paramsForLogging);

        String token = tokens.get(mergedParams);
        String tokenForLogging = trimTokenForLogging(token);

        if (token == null) {
            LOG.traceNoTokenInStore(paramsForLogging);
            token = authorizeAndGetToken(mergedParams);
            putToken(mergedParams, token);
        } else {
            if (isExpired(token)) {
                LOG.traceTokenExpired(paramsForLogging, tokenForLogging);
                clearToken(mergedParams);
                token = authorizeAndGetToken(mergedParams);
                putToken(mergedParams, token);
            } else {
                LOG.traceGotTokenFromStore(paramsForLogging, tokenForLogging);
            }
        }
        return token;
    }

    private void putToken(MultiValueMap<String, String> params,
                          String token) {
        LOG.tracePutTokenInStore(clearParamsForLogging(params), trimTokenForLogging(token));
        tokens.put(params, token);
    }

    private void clearToken(MultiValueMap<String, String> params) {
        LOG.traceRemovedTokenFromStore(clearParamsForLogging(params));
        tokens.remove(params);
    }

    private String authorizeAndGetToken(MultiValueMap<String, String> additionalRequestParameters) {

        final MultiValueMap<String, String> params;
        if (additionalRequestParameters != null) {
            params = additionalRequestParameters;
        } else {
            params = new LinkedMultiValueMap<String, String>();
        }
        MultiValueMap<String, String> paramsForLogging = clearParamsForLogging(params);

        LOG.traceRequestNewToken(paramsForLogging);

        LinkedMultiValueMap<String, String> requestBody = new LinkedMultiValueMap<String, String>();
        requestBody.putAll(this.defaultParameters);
        requestBody.putAll(params);
        HttpEntity<MultiValueMap<String, String>> request = createEntity(requestBody);

        final ResponseEntity<TokenResponse> responseEntity;
        try {
            responseEntity = restTemplate.postForEntity(accessTokenEndpoint, request, TokenResponse.class);
        } catch (HttpStatusCodeException e) {
            LOG.errorOnGetTokenHttp(accessTokenEndpoint, paramsForLogging, e.getStatusCode(),
                    trimBodyForLogging(e.getResponseBodyAsString()), e);
            if (e.getStatusCode().is5xxServerError()) {
                throw new NetworkErrorException("Cannot get client_credentials token. SSO server error", e);
            } else {
                throw new AuthenticationException("Cannot get client_credentials token", e);
            }
        } catch (ResourceAccessException e) {
            LOG.errorOnGetTokenIO(accessTokenEndpoint, paramsForLogging,
                    ConfigKeys.HTTP_CONNECTION_TIMEOUT,
                    configuration.getConnectTimeout(),
                    ConfigKeys.HTTP_SOCKET_TIMEOUT,
                    configuration.getReadTimeout(),
                    e);
            throw new AuthenticationException("Cannot get client_credentials token", e);
        } catch (Exception e) {
            LOG.errorOnGetToken(accessTokenEndpoint, paramsForLogging, e);
            throw new AuthenticationException("Cannot get client_credentials token", e);
        }

        final TokenResponse body = responseEntity.getBody();
        final String token = body.getAccessToken();

        LOG.traceGotToken(paramsForLogging, trimTokenForLogging(token));

        return token;
    }

    private String trimTokenForLogging(String token) {
        if (token == null) {
            return "<none>";
        } else {
            return token.substring(0, Math.min(16, token.length()));
        }
    }

    private String trimBodyForLogging(String body) {
        if (body == null) {
            return "<none>";
        } else {
            return body.substring(0, Math.min(200, body.length()));
        }
    }

    private MultiValueMap<String, String> clearParamsForLogging(MultiValueMap<String, String> params) {
        LinkedMultiValueMap<String, String> ret = new LinkedMultiValueMap<String, String>(params);
        if (ret.containsKey("client_secret")) {
            ret.set("client_secret", "***");
        }
        return ret;
    }

    private HttpEntity<MultiValueMap<String, String>> createEntity(MultiValueMap<String, String> params) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        return new HttpEntity<MultiValueMap<String, String>>(params, headers);
    }

}