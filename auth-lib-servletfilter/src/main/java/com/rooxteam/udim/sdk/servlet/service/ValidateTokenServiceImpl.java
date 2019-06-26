package com.rooxteam.udim.sdk.servlet.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rooxteam.udim.sdk.servlet.dto.TokenInfo;
import org.apache.http.HttpHeaders;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidateTokenServiceImpl implements ValidateTokenService {

    private ValidateServiceResponseHandler handler;
    private HttpClient httpClient;
    private RequestConfig requestConfig;

    public ValidateTokenServiceImpl(int socketTimeout, int connectionTimeout, int connectionRequestTimeout) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.handler = new ValidateServiceResponseHandler(objectMapper);
        this.httpClient = HttpClientBuilder.create().build();
        this.requestConfig = RequestConfig
                .custom()
                .setSocketTimeout(socketTimeout)
                .setConnectTimeout(connectionTimeout)
                .setConnectionRequestTimeout(connectionRequestTimeout)
                .build();
    }

    @Override
    public Optional<TokenInfo> getAccessTokenInfo(String tokenInfoUrl, String accessToken) {
        URI uri = null;
        try {
          URIBuilder builder = new URIBuilder(tokenInfoUrl);
          builder.addParameter("access_token", accessToken);
          uri = builder.build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        try {
            HttpGet httpUriRequest = new HttpGet(uri);
            httpUriRequest.addHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
            httpUriRequest.addHeader(HttpHeaders.ACCEPT, "application/json");
            httpUriRequest.setConfig(requestConfig);
            TokenInfo res = httpClient.execute(httpUriRequest, handler);
            return Optional.of(res);
        } catch (IOException ex) {
            //TODO Log error;
        }
        return Optional.empty();
    }

    private final static Pattern headerTokenValidationPattern = Pattern.compile("Bearer (([a-zA-Z]+)_([.\\d]+)_)?(.+)");

    @Override
    public Optional<String> extractAccessToken(String headerValue) {
        Matcher matcher = headerTokenValidationPattern.matcher(headerValue);
        if (matcher.matches()) {
            return Optional.of(matcher.group(4));
        }
        return Optional.empty();
    }
}
