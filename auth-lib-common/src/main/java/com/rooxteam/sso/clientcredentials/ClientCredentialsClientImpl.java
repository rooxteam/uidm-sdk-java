package com.rooxteam.sso.clientcredentials;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static com.rooxteam.sso.clientcredentials.ClientCredentialsClientLogger.LOG;


final class ClientCredentialsClientImpl implements ClientCredentialsClient {
    private final RestTemplate restTemplate;
    private final URI accessTokenEndpoint;
    private final URI tokenValidationEndpoint;
    private final MultiValueMap<String, String> defaultParameters;
    private final String headerPrefix;

    private final ConcurrentHashMap<MultiValueMap<String, String>, String> tokens = new ConcurrentHashMap<>();


    ClientCredentialsClientImpl(RestTemplate restTemplate,
                                URI accessTokenEndpoint,
                                URI tokenValidationEndpoint,
                                MultiValueMap<String, String> defaultParameters,
                                String headerPrefix) {
        this.restTemplate = Objects.requireNonNull(restTemplate, "restTemplate");
        this.accessTokenEndpoint = Objects.requireNonNull(accessTokenEndpoint, "accessTokenEndpoint");
        this.tokenValidationEndpoint = Objects.requireNonNull(tokenValidationEndpoint, "tokenValidationEndpoint");
        this.defaultParameters = Optional.ofNullable(defaultParameters).orElse(new LinkedMultiValueMap<>());
        this.headerPrefix = headerPrefix;
    }

    @Override
    public String getAuthHeaderValue(MultiValueMap<String, String> params) throws ClientAuthenticationException {
        String token = getTokenValidating(params);
        return headerPrefix + token;
    }

    @Override
    public String getToken(MultiValueMap<String, String> params) throws ClientAuthenticationException {
        return getTokenValidating(params);
    }

    @Override
    public String getAuthHeaderValue() throws ClientAuthenticationException {
        String token = getTokenValidating(defaultParameters);
        return headerPrefix + token;
    }

    @Override
    public String getToken() throws ClientAuthenticationException {
        return getTokenValidating(defaultParameters);
    }


    private boolean isExpired(String token) {
        String tokenForLogging = trimTokenForLogging(token);

        try {
            restTemplate.getForEntity(tokenValidationEndpoint, Object.class);
            return false;
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                LOG.debugOnValidatingTokenTokenExpired(tokenForLogging);
            }
            if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
                LOG.debugOnValidatingTokenTokenForbidden(tokenForLogging);
            } else {
                LOG.errorOnValidatingToken(tokenForLogging, e);
            }
            return true;
        } catch (RestClientException e) {
            LOG.errorOnValidatingToken(tokenForLogging, e);
            return true;
        }
    }

    private String getTokenValidating(MultiValueMap<String, String> requestParameters) throws ClientAuthenticationException {

        MultiValueMap<String, String> mergedParams = new LinkedMultiValueMap<>();
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

    private void putToken(MultiValueMap<String, String> params, String token) {
        LOG.tracePutTokenInStore(clearParamsForLogging(params), trimTokenForLogging(token));
        tokens.put(params, token);
    }

    private void clearToken(MultiValueMap<String, String> params) {
        LOG.traceRemovedTokenFromStore(clearParamsForLogging(params));
        tokens.remove(params);
    }

    private String authorizeAndGetToken(MultiValueMap<String, String> additionalRequestParameters) throws ClientAuthenticationException {

        final MultiValueMap<String, String> params = Optional.ofNullable(additionalRequestParameters).orElse(new LinkedMultiValueMap<>());
        MultiValueMap<String, String> paramsForLogging = clearParamsForLogging(params);

        LOG.traceRequestNewToken(paramsForLogging);

        LinkedMultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.putAll(this.defaultParameters);
        requestBody.putAll(params);
        HttpEntity<MultiValueMap<String, String>> request = createEntity(requestBody);

        final ResponseEntity<TokenResponse> responseEntity;
        try {
            responseEntity = restTemplate.postForEntity(accessTokenEndpoint, request, TokenResponse.class);
        } catch (HttpStatusCodeException e) {
            LOG.errorOnGetTokenHttp(paramsForLogging, e.getStatusCode(), trimBodyForLogging(e.getResponseBodyAsString()), e);
            throw new ClientAuthenticationException("Cannot get client_credentials token", e);
        } catch (Exception e) {
            LOG.errorOnGetToken(paramsForLogging, e);
            throw new ClientAuthenticationException("Cannot get client_credentials token", e);
        }

        final TokenResponse body = responseEntity.getBody();
        final String token = body.getAccessToken();

        LOG.traceGotToken(paramsForLogging, trimTokenForLogging(token));

        return token;
    }

    private String trimTokenForLogging(String token) {
        return Optional.ofNullable(token)
                .map(s -> s.substring(0, Math.min(16, s.length())))
                .orElse("<none>");
    }

    private String trimBodyForLogging(String body) {
        return Optional.ofNullable(body)
                .map(s -> s.substring(0, Math.min(200, s.length())))
                .orElse("<none>");
    }

    private MultiValueMap<String, String> clearParamsForLogging(MultiValueMap<String, String> params) {
        LinkedMultiValueMap<String, String> ret = new LinkedMultiValueMap<>(params);
        if (ret.containsKey("client_secret")) {
            ret.set("client_secret", "***");
        }
        return ret;
    }

    private HttpEntity<MultiValueMap<String, String>> createEntity(MultiValueMap<String, String> params) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        return new HttpEntity<>(params, headers);
    }

}