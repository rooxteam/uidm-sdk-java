package com.rooxteam.sso.aal.client;

import com.rooxteam.sso.aal.configuration.ConfigurationBuilder;
import com.rooxteam.sso.aal.exception.AuthenticationException;
import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class AuthenticationClientTest {

    @Test
    public void authenticateByIp_validIp_notNullToken() throws IOException {
        Configuration config = new BaseConfiguration();
        config.setProperty("com.rooxteam.aal.auth.client", "webapi");
        config.setProperty("com.rooxteam.aal.auth.password", "password");
        config.setProperty("com.rooxteam.sso.endpoint", "http://all.yota.hosted:8080/sso/");

        CloseableHttpClient client = mock(CloseableHttpClient.class);
        CloseableHttpResponse response = mock(CloseableHttpResponse.class);
        HttpEntity entity = mock(HttpEntity.class);
        String testTokenContent = "{\"JWTToken\": \"123123\"}";

        when(entity.getContent()).thenReturn(new ByteArrayInputStream(testTokenContent.getBytes(Charset.forName("UTF-8"))));
        when(response.getEntity()).thenReturn(entity);
        when(client.execute(any(HttpUriRequest.class), any(HttpClientContext.class))).thenReturn(response);

        SsoAuthenticationClient instance = new SsoAuthenticationClient(ConfigurationBuilder.fromApacheCommonsConfiguration(config), client);

        HashMap<String, Object> params = new HashMap<>();
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
        CloseableHttpResponse response = mock(CloseableHttpResponse.class);
        HttpEntity entity = mock(HttpEntity.class);
        String testTokenContent = "{\"error\": \"invalid_grant\", \"error_subtype\": \"Not Found\"}";

        when(entity.getContent()).thenReturn(new ByteArrayInputStream(testTokenContent.getBytes(Charset.forName("UTF-8"))));
        when(response.getEntity()).thenReturn(entity);
        when(client.execute(any(HttpUriRequest.class), any(HttpClientContext.class))).thenReturn(response);

        SsoAuthenticationClient instance = new SsoAuthenticationClient(ConfigurationBuilder.fromApacheCommonsConfiguration(config), client);

        HashMap<String, Object> params = new HashMap<>();
        params.put("ip", "229.213.38.10");
        String token = null;
        try {
            token = instance.authenticate(params).getPublicToken();
        } catch (AuthenticationException e) {
            assertEquals("invalid_grant", e.getError());
            assertEquals("Not Found", e.getErrorSubtype());
            throw e;
        }

    }
}
