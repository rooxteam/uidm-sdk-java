package com.rooxteam.sso.aal.client;

import com.rooxteam.sso.aal.ConfigKeys;
import com.rooxteam.sso.aal.PrincipalImpl;
import com.rooxteam.sso.aal.client.exception.UnknownResponseException;
import com.rooxteam.sso.aal.client.model.AuthenticationResponse;
import com.rooxteam.sso.aal.client.model.Form;
import com.rooxteam.sso.aal.client.model.OtpFlowStateJson;
import com.rooxteam.sso.aal.client.model.ResponseError;
import com.rooxteam.sso.aal.otp.*;
import org.apache.commons.codec.Charsets;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.rooxteam.sso.aal.AalLogger.LOG;
import static com.rooxteam.sso.aal.client.SsoAuthenticationClient.*;

public class OtpClient {

    public static final String OTP_CODE_PARAM_NAME = "otpCode";
    public static final String OAUTH2_ACCESS_TOKEN_PATH = "/oauth2/access_token";

    private static final String EXECUTION_PARAM_NAME = "execution";
    private static final String EVENT_ID_PARAM_NAME = "_eventId";
    private static final String EVENT_ID_VALIDATE = "validate";
    private static final String EVENT_ID_SEND = "send";
    private static final String SESSION_ID_COOKIE_NAME = "RX_SID";

    private Configuration config;

    private CloseableHttpClient httpClient;

    private ObjectMapper jsonMapper;

    private Map<String, OtpStatus> otpStatusMapping = new HashMap<String, OtpStatus>() {{
        put("too_many_sms", OtpStatus.TOO_MANY_OTP);
        put("error_sending_otp", OtpStatus.SEND_OTP_FAIL);
        put("invalid_otp", OtpStatus.OTP_REQUIRED);
        put("too_many_wrong_code", OtpStatus.TOO_MANY_WRONG_CODE);
    }};

    public OtpClient(Configuration config, CloseableHttpClient httpClient) {
        this.config = config;
        this.jsonMapper = new ObjectMapper();
        this.httpClient = httpClient;
    }

    public OtpResponse sendOtp(String jwt) {
        List<NameValuePair> params = commonOtpParams();
        params.add(new BasicNameValuePair(currentTokenParamName(), jwt));

        return makeOtpRequest(params, null);
    }

    public OtpResponse sendOtpForOperation(String jwt, EvaluationContext context) {
        String contextJson;
        try {
            contextJson = jsonMapper.writeValueAsString(context);
        } catch (IOException e) {
            LOG.warnInvalidContextJson(context, e);
            return OtpResponseImpl.exception(e);
        }
        List<NameValuePair> params = commonOtpParams();
        params.add(new BasicNameValuePair(currentTokenParamName(), jwt));
        params.add(new BasicNameValuePair("operation", contextJson));
        return makeOtpRequest(params, null);
    }

    public OtpResponse resendOtp(OtpFlowState otpFlowState) {
        return sendOtpEvent(otpFlowState, null, EVENT_ID_SEND);
    }

    public OtpResponse validateOtp(OtpFlowState otpState, String otpCode) {
        return sendOtpEvent(otpState, otpCode, EVENT_ID_VALIDATE);
    }

    protected String currentTokenParamName() {
        return config.getString(ConfigKeys.OTP_CURRENT_TOKEN_PARAM_NAME, ConfigKeys.OTP_CURRENT_TOKEN_PARAM_NAME_DEFAULT);
    }

    private OtpResponse sendOtpEvent(OtpFlowState otpState, String otpCode, String eventId) {
        if (StringUtils.isEmpty(otpState.getSessionId()) ||
                StringUtils.isEmpty(otpState.getExecution()) ||
                StringUtils.isEmpty(otpState.getServerUrl())) {
            throw new IllegalStateException("OtpFlowState should contain all fields");
        }

        List<NameValuePair> params = commonOtpParams();
        params.add(new BasicNameValuePair(EXECUTION_PARAM_NAME, otpState.getExecution()));
        params.add(new BasicNameValuePair(EVENT_ID_PARAM_NAME, eventId));
        if (otpCode != null) {
            params.add(new BasicNameValuePair(OTP_CODE_PARAM_NAME, otpCode));
        }

        return makeOtpRequest(params, otpState);
    }

    private OtpResponse makeOtpRequest(List<NameValuePair> params, OtpFlowState otpState) {
        HttpPost post = new HttpPost(config.getString(ConfigKeys.SSO_URL) + OAUTH2_ACCESS_TOKEN_PATH);
        post.addHeader(new BasicHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType()));
        post.setEntity(new UrlEncodedFormEntity(params, Charsets.UTF_8));

        CookieStore basicCookieStore = new BasicCookieStore();
        HttpClientContext context = new HttpClientContext();
        context.setCookieStore(basicCookieStore);
        CloseableHttpResponse response = null;
        try {
            CookieStore cookieStore = context.getCookieStore();
            if (otpState != null) {
                BasicClientCookie cookie = new BasicClientCookie(SESSION_ID_COOKIE_NAME, otpState.getSessionId());
                cookie.setDomain(post.getURI().getHost());
                cookieStore.addCookie(cookie);
            }
            response = httpClient.execute(post, context);
            String sessionIdCookie = getSessionIdCookie(cookieStore);

            HttpEntity entity = response.getEntity();
            String json = EntityUtils.toString(entity, Charsets.UTF_8);

            return prepareOtpResponse(json, sessionIdCookie);
        } catch (IOException e) {
            LOG.errorValidateOtpByMsisdnError(otpState, e);
            return OtpResponseImpl.exception(e);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    LOG.errorValidateOtpUnableToCloseResponse(otpState, e);
                }
            }
        }
    }

    private List<NameValuePair> commonOtpParams() {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair(REALM_PARAM_NAME, config.getString(ConfigKeys.REALM, ConfigKeys.REALM_DEFAULT)));
        params.add(new BasicNameValuePair(CLIENT_ID_PARAM_NAME, config.getString(ConfigKeys.CLIENT_ID)));
        params.add(new BasicNameValuePair(CLIENT_SECRET, config.getString(ConfigKeys.CLIENT_SECRET)));
        params.add(new BasicNameValuePair(GRANT_TYPE, GRANT_TYPE_M2M));
        params.add(new BasicNameValuePair(SERVICE_PARAM_NAME, config.getString(ConfigKeys.OTP_SERVICE, ConfigKeys.OTP_SERVICE_DEFAULT)));
        return params;
    }


    private OtpResponse prepareOtpResponse(String json, String sessionIdCookie) {
        OtpFlowStateJson otpFlowStateJson;
        try {
            JsonNode jsonNode = jsonMapper.readTree(json);
            if (jsonNode.has("JWTToken")) {
                AuthenticationResponse authenticationResponse = jsonMapper.readValue(jsonNode, AuthenticationResponse.class);
                OtpResponseImpl response = new OtpResponseImpl();
                response.setStatus(OtpStatus.SUCCESS);
                response.setPrincipal(new PrincipalImpl(authenticationResponse.getPolicyContext(), authenticationResponse.getPublicToken()));
                return response;
            }
            if (!jsonNode.has("form") || !jsonNode.has("view")) {
                LOG.errorUnknownWebSSOResponse(json);
                OtpResponseImpl response = new OtpResponseImpl();
                response.setStatus(OtpStatus.EXCEPTION);
                response.setException(new UnknownResponseException(json));
                return response;
            }

            otpFlowStateJson = jsonMapper.readValue(json, OtpFlowStateJson.class);
        } catch (IOException e) {
            LOG.errorSendOtpUnableToParseResponseJson(json, e);
            return OtpResponseImpl.exception(e);
        }

        OtpFlowStateImpl otpFlowState = new OtpFlowStateImpl();
        otpFlowState.setExecution(otpFlowStateJson.getExecution());
        otpFlowState.setServerUrl(otpFlowStateJson.getServerUrl());
        otpFlowState.setSessionId(sessionIdCookie);

        OtpResponseImpl otpResponse = new OtpResponseImpl();
        otpResponse.setStatus(getOtpStatus(otpFlowStateJson));
        otpResponse.setOtpFlowState(otpFlowState);
        otpResponse.setRequiredFieldNames(otpFlowStateJson.getForm().getFields().keySet());
        otpResponse.setAvailableAttempts(otpFlowStateJson.getView().getOtpCodeAvailableAttempts());
        otpResponse.setBlockedFor(otpFlowStateJson.getView().getBlockedFor());

        return otpResponse;
    }

    private OtpStatus getOtpStatus(OtpFlowStateJson otpFlowStateJson) {
        Form form = otpFlowStateJson.getForm();
        List<ResponseError> errors = form.getErrors();
        for (ResponseError error : errors) {
            OtpStatus status = otpStatusMapping.get(error.getMessage());
            if (status != null) {
                return status;
            }
        }
        return OtpStatus.OTP_REQUIRED;
    }

    private String getSessionIdCookie(CookieStore cookieStore) {
        List<Cookie> cookies = cookieStore.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(SESSION_ID_COOKIE_NAME)) {
                return cookie.getValue();
            }
        }
        return null;
    }
}

