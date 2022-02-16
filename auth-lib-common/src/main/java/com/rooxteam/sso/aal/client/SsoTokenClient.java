package com.rooxteam.sso.aal.client;

import com.rooxteam.sso.aal.ConfigKeys;
import com.rooxteam.sso.aal.configuration.Configuration;
import com.rooxteam.util.HttpHelper;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import static com.rooxteam.sso.aal.AalLogger.LOG;

/**
 * This class holds Token API routines. It's expected to be used in PD-4087 as
 * token validity checker.
 */
public class SsoTokenClient {

    static final String TOKEN_API_PATH = "/api/token";
    static final String TOKEN_ID_PARAM_NAME = "tokenId";

    private final Configuration config;
    private final CloseableHttpClient closableHttpClient;

    public SsoTokenClient(Configuration rooxConfig, CloseableHttpClient client) {
        config = rooxConfig;
        closableHttpClient = client;
    }

    /**
     * Queries SSO Token API for token existence.
     *
     * @param tokenId token ID to lookup.
     * @return true if Token API returns 200, false otherwise.
     */
    public boolean queryExistence(String tokenId) {
        try {
            HttpGet request = createRequest(tokenId);
            CloseableHttpResponse response = closableHttpClient.execute(request);
            try {
                if (success(response)) {
                    return true;
                }
                if (!notFound(response)) {
                    LOG.errorUnexpectedTokenApiResponse(response);
                }
                return false;
            } finally {
                response.close();
            }
        } catch (IOException e) {
            LOG.errorTokenRequestFailed(e);
            return false;
        }
    }

    private HttpGet createRequest(String tokenId) throws UnsupportedEncodingException {
        String url = config.getString(ConfigKeys.SSO_URL) + TOKEN_API_PATH;
        List<NameValuePair> params = Arrays.<NameValuePair>asList(new BasicNameValuePair(TOKEN_ID_PARAM_NAME, tokenId));
        return HttpHelper.getHttpGet(url, params);
    }

    private static boolean success(HttpResponse r) {
        return r.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
    }

    private static boolean notFound(HttpResponse r) {
        return r.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND;
    }
}
