package com.rooxteam.sso.aal.client;

import com.rooxteam.sso.aal.ConfigKeys;
import com.rooxteam.sso.aal.configuration.ConfigurationBuilder;
import org.apache.commons.configuration.Configuration;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.junit.Test;
import org.mockito.ArgumentMatcher;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class SsoTokenClientTest {

    private static final String TOKEN_API_URL = "https://api.example.com/api/test";
    private static final String TOKEN_ID = "some-token";

    @Test
    public void testQueryExistenceFound() throws Exception {

        CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
        CloseableHttpResponse response = CloseableHttpResponse.adapt(mock(ClassicHttpResponse.class));

        when(response.getCode()).thenReturn(200);

        when(httpClient.execute(requestMatcher(TOKEN_ID))).thenReturn(response);

        testQueryExistenceBase(httpClient, true);

        verify(httpClient, times(1)).execute(requestMatcher(TOKEN_ID));
        verifyNoMoreInteractions(httpClient);
    }

    @Test
    public void testQueryExistenceNotFound() throws Exception {

        CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
        CloseableHttpResponse response = CloseableHttpResponse.adapt(mock(ClassicHttpResponse.class));

        when(response.getCode()).thenReturn(404);
        when(httpClient.execute(requestMatcher(TOKEN_ID))).thenReturn(response);

        testQueryExistenceBase(httpClient, false);

        verify(httpClient, times(1)).execute(requestMatcher(TOKEN_ID));
        verifyNoMoreInteractions(httpClient);
    }

    @Test
    public void testQueryExistenceHttpError() throws Exception {

        CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
        CloseableHttpResponse response = CloseableHttpResponse.adapt(mock(ClassicHttpResponse.class));

        when(response.getCode()).thenReturn(500);
        when(httpClient.execute(requestMatcher(TOKEN_ID))).thenReturn(response);

        testQueryExistenceBase(httpClient, false);

        verify(httpClient, times(1)).execute(requestMatcher(TOKEN_ID));
        verifyNoMoreInteractions(httpClient);
    }

    @Test
    public void testQueryExistenceIOError() throws Exception {

        CloseableHttpClient httpClient = mock(CloseableHttpClient.class);

        when(httpClient.execute(requestMatcher(TOKEN_ID)))
                .thenThrow(new IOException());

        testQueryExistenceBase(httpClient, false);

        verify(httpClient, times(1))
                .execute(requestMatcher(TOKEN_ID));
        verifyNoMoreInteractions(httpClient);
    }

    protected void testQueryExistenceBase(CloseableHttpClient httpClient, boolean expectedExistence) throws Exception {

        Configuration config = mock(Configuration.class);
        SsoTokenClient tokenClient = new SsoTokenClient(ConfigurationBuilder.fromApacheCommonsConfiguration(config), httpClient);

        when(config.getString(ConfigKeys.SSO_URL))
                .thenReturn(TOKEN_API_URL);

        boolean exists = tokenClient.queryExistence(TOKEN_ID);
        assertEquals(expectedExistence, exists);
    }

    private static HttpGet requestMatcher(final String tokenId) {
        return argThat(new ArgumentMatcher<HttpGet>() {

            @Override
            public boolean matches(Object argument) {
                HttpGet get = (HttpGet) argument;
                String uri = get.getRequestUri();
                String expectedParam = SsoTokenClient.TOKEN_ID_PARAM_NAME + "=" + tokenId;
                return uri.contains(expectedParam);
            }
        });
    }
}