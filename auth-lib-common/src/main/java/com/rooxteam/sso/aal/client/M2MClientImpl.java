package com.rooxteam.sso.aal.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rooxteam.sso.aal.ConfigKeys;
import com.rooxteam.sso.aal.client.configuration.ClientConfiguration;
import com.rooxteam.sso.aal.client.context.ClientContextProvider;
import com.rooxteam.sso.aal.utils.StringUtils;
import com.rooxteam.util.HttpHelper;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicNameValuePair;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.rooxteam.sso.aal.client.SsoAuthenticationClient.CLIENT_ID_PARAM_NAME;
import static com.rooxteam.sso.aal.client.SsoAuthenticationClient.CLIENT_SECRET;
import static com.rooxteam.sso.aal.client.SsoAuthenticationClient.GRANT_TYPE;
import static com.rooxteam.sso.aal.client.SsoAuthenticationClient.GRANT_TYPE_M2M;
import static com.rooxteam.sso.aal.client.SsoAuthenticationClient.REALM_PARAM_NAME;
import static com.rooxteam.sso.aal.client.SsoAuthenticationClient.SERVICE_PARAM_NAME;

final class M2MClientImpl implements M2MClient {
    private static final String CLIENT_CONTEXT_PARAM = "clientContext";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final ClientConfiguration configuration;
    private final CloseableHttpClient closableHttpClient;
    private final ClientContextProvider clientContextProvider;
    private final boolean shouldCloseHttpClient;

    public M2MClientImpl(ClientConfiguration configuration,
                         CloseableHttpClient closableHttpClient,
                         ClientContextProvider clientContextProvider,
                         boolean shouldCloseHttpClient) {
        this.configuration = configuration;
        this.closableHttpClient = closableHttpClient;
        this.clientContextProvider = clientContextProvider;
        this.shouldCloseHttpClient = shouldCloseHttpClient;
    }

    @Override
    public JsonNode authenticate(String clientId, Map<String, String> args) throws Exception {
        Map<String, String> map = commonParams(clientId);
        map.putAll(args);
        return authenticate(toNameValuePairList(map));
    }

    private JsonNode authenticate(List<NameValuePair> params) throws Exception {
        URI url = configuration.getAccessTokenEndpoint();
        HttpPost post = HttpHelper.getHttpPostWithEntity(url, params);
        try(CloseableHttpResponse response = closableHttpClient.execute(post)) {
            if (success(response)) {
                byte[] json = EntityUtils.toByteArray(response.getEntity());
                return OBJECT_MAPPER.readTree(json);
            }
        }
        return null;
    }

    private Map<String, String> commonParams(String clientId) throws JsonProcessingException {
        Map<String, String> map = new HashMap<>();
        map.put(REALM_PARAM_NAME, configuration.getUidmRealm());
        map.put(GRANT_TYPE, GRANT_TYPE_M2M);
        map.put(SERVICE_PARAM_NAME, ConfigKeys.AUTH_SERVICE_DEFAULT);
        map.put(CLIENT_CONTEXT_PARAM, toJson(clientContextProvider.getContext()));
        map.put(CLIENT_ID_PARAM_NAME, clientId);
        map.put(CLIENT_SECRET, configuration.getClientSecret(clientId));
        return map;
    }

    private static List<NameValuePair> toNameValuePairList(Map<String, String> map) {
        return map.entrySet().stream()
                .filter(e -> StringUtils.isNoneBlank(e.getKey(), e.getValue()))
                .map(e -> new BasicNameValuePair(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    private static String toJson(Object u) throws JsonProcessingException {
        return OBJECT_MAPPER.writeValueAsString(u);
    }

    private static boolean success(HttpResponse r) {
        return r.getCode() == HttpStatus.SC_OK;
    }

    @Override
    public void close() throws IOException {
        if (shouldCloseHttpClient) {
            closableHttpClient.close();
        }
    }
}