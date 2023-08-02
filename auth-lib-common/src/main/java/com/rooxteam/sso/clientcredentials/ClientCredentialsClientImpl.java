package com.rooxteam.sso.clientcredentials;

import com.rooxteam.compat.Objects;
import com.rooxteam.sso.aal.ConfigKeys;
import com.rooxteam.sso.aal.exception.AuthenticationException;
import com.rooxteam.sso.aal.exception.NetworkErrorException;
import com.rooxteam.sso.clientcredentials.configuration.Configuration;
import com.rooxteam.util.TokenUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

import static com.rooxteam.sso.clientcredentials.ClientCredentialsClientLogger.LOG;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;


final class ClientCredentialsClientImpl implements ClientCredentialsClient {
    private final RestTemplate restTemplate;
    private final URI accessTokenEndpoint;
    private final URI tokenValidationEndpoint;
    private final MultiValueMap<String, String> defaultParameters;
    private final String headerPrefix;
    private final Configuration configuration;

    private final ConcurrentHashMap<MultiValueMap<String, String>, ClientCredentialsTokenModel> tokens = new ConcurrentHashMap<>();

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
        if (token == null || token.isEmpty()) {
            return true;
        }
        try {
            if (configuration.sendTokenInAuthorizationHeaderInValidationProcess()) {
                final URI uri = UriComponentsBuilder.fromUri(tokenValidationEndpoint)
                        .build().toUri();
                RequestEntity.HeadersBuilder<?> requestBuilder = RequestEntity.get(uri)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE);
                requestBuilder.header(AUTHORIZATION, TokenUtils.wrapBearerToken(token));
                RequestEntity<Void> requestEntity = requestBuilder.build();
                restTemplate.exchange(requestEntity, Object.class);
            } else {
                final URI uri = UriComponentsBuilder.fromUri(tokenValidationEndpoint)
                        .queryParam("access_token", token)
                        .build().toUri();
                restTemplate.getForEntity(uri, Object.class);
            }
            return false;
        } catch (HttpStatusCodeException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                LOG.traceOnValidatingTokenTokenExpired(token);
            } else if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
                LOG.traceOnValidatingTokenTokenForbidden(token);
            } else {
                LOG.errorOnValidatingTokenHttp(tokenValidationEndpoint,
                        token,
                        e.getStatusCode(),
                        trimBodyForLogging(e.getResponseBodyAsString()),
                        e);
            }
            return true;
        } catch (ResourceAccessException e) {
            LOG.errorOnValidatingTokenIO(tokenValidationEndpoint,
                    token,
                    ConfigKeys.HTTP_CONNECTION_TIMEOUT,
                    configuration.getConnectTimeout(),
                    ConfigKeys.HTTP_SOCKET_TIMEOUT,
                    configuration.getReadTimeout(),
                    e);
            return true;
        } catch (Exception e) {
            LOG.errorOnValidatingToken(tokenValidationEndpoint, token, e);
            return true;
        }
    }

    private String getTokenValidating(MultiValueMap<String, String> requestParameters) {

        MultiValueMap<String, String> mergedParams = new LinkedMultiValueMap<String, String>();
        mergedParams.putAll(this.defaultParameters);
        mergedParams.putAll(requestParameters);

        LOG.traceGetToken(mergedParams);

        ClientCredentialsTokenModel token = configuration.isTokensCacheEnabled() ? tokens.get(mergedParams) : null;

        if (token == null) {
            LOG.traceNoTokenInStore(mergedParams);
            token = authorizeAndGetToken(mergedParams);
            if (configuration.isTokensCacheEnabled()) {
                putToken(mergedParams, token);
            }
        } else {
            String tokenForLogging = token.getValue();
            if (isExpired(token.getValue())) {
                LOG.traceTokenExpired(mergedParams, tokenForLogging);
                clearToken(mergedParams);
                token = authorizeAndGetToken(mergedParams);
                putToken(mergedParams, token);
            } else {
                LocalDateTime expirationUpdateTime = LocalDateTime.now().plusSeconds(configuration.getUpdateTimeBeforeTokenExpiration());
                if (expirationUpdateTime.isAfter(token.getExpiresIn())) {
                    LOG.traceTokenExpired(mergedParams, tokenForLogging);
                    clearToken(mergedParams);
                    token = authorizeAndGetToken(mergedParams);
                    putToken(mergedParams, token);
                }
                LOG.traceGotTokenFromStore(mergedParams, tokenForLogging);
            }
        }
        return token.getValue();
    }

    private void putToken(MultiValueMap<String, String> params, ClientCredentialsTokenModel token) {
        LOG.tracePutTokenInStore(params, token.getValue());
        tokens.put(params, token);
    }

    private void clearToken(MultiValueMap<String, String> params) {
        LOG.traceRemovedTokenFromStore(params);
        tokens.remove(params);
    }

    private ClientCredentialsTokenModel authorizeAndGetToken(MultiValueMap<String, String> additionalRequestParameters) {

        final MultiValueMap<String, String> params;
        if (additionalRequestParameters != null) {
            params = additionalRequestParameters;
        } else {
            params = new LinkedMultiValueMap<String, String>();
        }

        LOG.traceRequestNewToken(params);

        LinkedMultiValueMap<String, String> requestBody = new LinkedMultiValueMap<String, String>();
        requestBody.putAll(this.defaultParameters);
        requestBody.putAll(params);
        HttpEntity<MultiValueMap<String, String>> request = createEntity(requestBody);

        final ResponseEntity<TokenResponse> responseEntity;
        try {
            responseEntity = restTemplate.postForEntity(accessTokenEndpoint, request, TokenResponse.class);
        } catch (HttpStatusCodeException e) {
            LOG.errorOnGetTokenHttp(accessTokenEndpoint, params, e.getStatusCode(),
                    trimBodyForLogging(e.getResponseBodyAsString()), e);
            if (e.getStatusCode().is5xxServerError()) {
                throw new NetworkErrorException("Cannot get client_credentials token. SSO server error", e);
            } else {
                throw new AuthenticationException("Cannot get client_credentials token", e);
            }
        } catch (ResourceAccessException e) {
            LOG.errorOnGetTokenIO(accessTokenEndpoint, params,
                    ConfigKeys.HTTP_CONNECTION_TIMEOUT,
                    configuration.getConnectTimeout(),
                    ConfigKeys.HTTP_SOCKET_TIMEOUT,
                    configuration.getReadTimeout(),
                    e);
            throw new AuthenticationException("Cannot get client_credentials token", e);
        } catch (Exception e) {
            LOG.errorOnGetToken(accessTokenEndpoint, params, e);
            throw new AuthenticationException("Cannot get client_credentials token", e);
        }

        final TokenResponse body = responseEntity.getBody();
        final String token = body.getAccessToken();
        LocalDateTime issueDate = LocalDateTime.now();
        ClientCredentialsTokenModel tokenModel = new ClientCredentialsTokenModel(token, issueDate, issueDate.plusSeconds(body.getExpiresIn()));

        LOG.traceGotToken(params, token);

        return tokenModel;
    }

    private String trimBodyForLogging(String body) {
        if (body == null) {
            return "<none>";
        } else {
            return body.substring(0, Math.min(200, body.length()));
        }
    }

    private HttpEntity<MultiValueMap<String, String>> createEntity(MultiValueMap<String, String> params) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        return new HttpEntity<MultiValueMap<String, String>>(params, headers);
    }

}