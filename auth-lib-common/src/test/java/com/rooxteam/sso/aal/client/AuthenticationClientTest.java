package com.rooxteam.sso.aal.client;

import com.rooxteam.sso.aal.configuration.ConfigurationBuilder;
import com.rooxteam.sso.aal.exception.AuthenticationException;
import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthenticationClientTest {

    @Test
    public void authenticateByIp_validIp_notNullToken() throws IOException {
        Configuration config = new BaseConfiguration();
        config.setProperty("com.rooxteam.aal.auth.client", "webapi");
        config.setProperty("com.rooxteam.aal.auth.password", "password");
        config.setProperty("com.rooxteam.sso.endpoint", "http://all.yota.hosted:8080/sso/");

        CloseableHttpClient client = mock(CloseableHttpClient.class);
        CloseableHttpResponse response = CloseableHttpResponse.adapt(mock(ClassicHttpResponse.class));
        HttpEntity entity = mock(HttpEntity.class);
        String testTokenContent = "{\"JWTToken\": \"123123\"}";

        when(entity.getContent()).thenReturn(new ByteArrayInputStream(testTokenContent.getBytes(StandardCharsets.UTF_8)));
        when(response.getEntity()).thenReturn(entity);
        when(client.execute(any(HttpUriRequest.class), any(HttpClientContext.class))).thenReturn(response);

        SsoAuthenticationClient instance = new SsoAuthenticationClient(ConfigurationBuilder.fromApacheCommonsConfiguration(config), client);

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("ip", "10.0.0.0");
        String token = instance.authenticate(params).getPublicToken();

        assertEquals(token, "123123");
    }

    @Test(expected = AuthenticationException.class)
    public void authenticateByIp_invalidIp_NullToken() throws IOException {
        Configuration config = new BaseConfiguration();
        config.setProperty("com.rooxteam.aal.auth.client", "webapi");
        config.setProperty("com.rooxteam.aal.auth.password", "password");
        config.setProperty("com.rooxteam.sso.endpoint", "http://all.yota.hosted:8080/sso/");

        CloseableHttpClient client = mock(CloseableHttpClient.class);
        CloseableHttpResponse response = CloseableHttpResponse.adapt(mock(ClassicHttpResponse.class));
        HttpEntity entity = mock(HttpEntity.class);
        String testTokenContent = "{\"error\": \"invalid_grant\", \"error_subtype\": \"Not Found\"}";

        when(entity.getContent()).thenReturn(new ByteArrayInputStream(testTokenContent.getBytes(StandardCharsets.UTF_8)));
        when(response.getEntity()).thenReturn(entity);
        when(client.execute(any(HttpUriRequest.class), any(HttpClientContext.class))).thenReturn(response);

        SsoAuthenticationClient instance = new SsoAuthenticationClient(ConfigurationBuilder.fromApacheCommonsConfiguration(config), client);

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("ip", "229.213.38.10");
        try {
            instance.authenticate(params).getPublicToken();
        } catch (AuthenticationException e) {
            assertEquals("invalid_grant", e.getError());
            assertEquals("Not Found", e.getErrorSubtype());
            throw e;
        }

    }
}
