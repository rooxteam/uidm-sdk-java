package com.rooxteam.sso.aal.client;

import com.rooxteam.sso.aal.ConfigKeys;
import org.apache.commons.configuration.Configuration;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicStatusLine;
import org.junit.Test;
import org.mockito.ArgumentMatcher;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class SsoTokenClientTest {

    private static final String TOKEN_API_URL = "https://api.example.com/api/test";
    private static final String TOKEN_ID = "some-token";

    @Test
    public void testQueryExistenceFound() throws Exception {

        CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
        CloseableHttpResponse response = mock(CloseableHttpResponse.class);
        StatusLine status = new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), HttpStatus.SC_OK, "Ok");

        when(response.getStatusLine())
                .thenReturn(status);
        when(httpClient.execute(requestMatcher(TOKEN_ID)))
                .thenReturn(response);

        testQueryExistenceBase(httpClient, true);

        verify(httpClient, times(1))
                .execute(requestMatcher(TOKEN_ID));
        verifyNoMoreInteractions(httpClient);
    }

    @Test
    public void testQueryExistenceNotFound() throws Exception {

        CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
        CloseableHttpResponse response = mock(CloseableHttpResponse.class);
        StatusLine status = new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), HttpStatus.SC_NOT_FOUND, "Not Found");

        when(response.getStatusLine())
                .thenReturn(status);
        when(httpClient.execute(requestMatcher(TOKEN_ID)))
                .thenReturn(response);

        testQueryExistenceBase(httpClient, false);

        verify(httpClient, times(1))
                .execute(requestMatcher(TOKEN_ID));
        verifyNoMoreInteractions(httpClient);
    }

    @Test
    public void testQueryExistenceHttpError() throws Exception {

        CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
        CloseableHttpResponse response = mock(CloseableHttpResponse.class);
        StatusLine status = new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1),
                HttpStatus.SC_INTERNAL_SERVER_ERROR, "Internal Server Error");

        when(response.getStatusLine())
                .thenReturn(status);
        when(httpClient.execute(requestMatcher(TOKEN_ID)))
                .thenReturn(response);

        testQueryExistenceBase(httpClient, false);

        verify(httpClient, times(1))
                .execute(requestMatcher(TOKEN_ID));
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
        SsoTokenClient tokenClient = new SsoTokenClient(config, httpClient);

        when(config.getString(ConfigKeys.SSO_URL))
                .thenReturn(TOKEN_API_URL);

        boolean exists = tokenClient.queryExistence(TOKEN_ID);
        assertEquals(expectedExistence, exists);
    }

    private static HttpGet requestMatcher(final String tokenId) throws Exception {
        return argThat(new ArgumentMatcher<HttpGet>() {

            @Override
            public boolean matches(Object argument) {
                HttpGet get = (HttpGet) argument;
                String uri = get.getRequestLine().getUri();
                String expectedParam = SsoTokenClient.TOKEN_ID_PARAM_NAME + "=" + tokenId;
                return uri.contains(expectedParam);
            }
        });
    }
}