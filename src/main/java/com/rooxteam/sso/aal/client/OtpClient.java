package com.rooxteam.sso.aal.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rooxteam.errors.exception.ApiException;
import com.rooxteam.sso.aal.ConfigKeys;
import com.rooxteam.sso.aal.client.exception.UnknownResponseException;
import com.rooxteam.sso.aal.client.model.*;
import com.rooxteam.sso.aal.context.TokenContextFactory;
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
import org.springframework.http.HttpStatus;

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
    public static final String MSISDN_PARAM_NAME = "msisdn";
    private static final String CATEGORY_PARAM_NAME = "category";
    private static final String EVENT_ID_PARAM_NAME = "_eventId";
    private static final String EVENT_ID_VALIDATE = "validate";
    private static final String EVENT_ID_SEND = "send";
    private static final String SESSION_ID_COOKIE_NAME = "RX_SID";
    private static final String NEXT_OTP_OPERATION_PERIOD_PARAM_NAME = "com.rooxteam.uidm.otp.operation.next_otp_period";
    private static final int NEXT_OTP_OPERATION_PERIOD_DEFAULT_VALUE = 10;
    private final Map<String, Class<? extends AuthenticationResponse>> responseTypes;

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
        responseTypes = new HashMap<>();
        responseTypes.put("Bearer", BearerAuthenticationResponse.class);
        responseTypes.put("JWTToken", BearerAuthenticationResponse.class);
    }

    public OtpResponse sendOtp(String jwt) {
        List<NameValuePair> params = commonOtpParams();
        params.add(new BasicNameValuePair(currentTokenParamName(), jwt));

        return makeOtpRequest(params, null);
    }

    public OtpResponse sendOtpForOperation(String jwt, EvaluationContext context) {
        SendOtpParameter sendOtpParameter = SendOtpParameter.builder()
                .jwt(jwt)
                .service(getDefaultService())
                .evaluationContext(context)
                .build();
        return sendOtpForOperation(sendOtpParameter);
    }

    public OtpResponse sendOtpForOperation(SendOtpParameter sendOtpParameter) {
        String contextJson;
        try {
            contextJson = jsonMapper.writeValueAsString(sendOtpParameter.getEvaluationContext());
        } catch (IOException e) {
            LOG.warnInvalidContextJson(sendOtpParameter.getEvaluationContext(), e);
            return OtpResponseImpl.exception(e);
        }
        List<NameValuePair> params = commonOtpParams(sendOtpParameter.getService());
        if (!StringUtils.isEmpty(sendOtpParameter.getJwt())) {
            params.add(new BasicNameValuePair(currentTokenParamName(), sendOtpParameter.getJwt()));
        }
        if (!StringUtils.isEmpty(sendOtpParameter.getMsisdn())) {
            params.add(new BasicNameValuePair(MSISDN_PARAM_NAME, sendOtpParameter.getMsisdn()));
        }
        if (!StringUtils.isEmpty(sendOtpParameter.getCategory())) {
            params.add(new BasicNameValuePair(CATEGORY_PARAM_NAME, sendOtpParameter.getCategory()));
        }
        params.add(new BasicNameValuePair("operation", contextJson));
        return makeOtpRequest(params, null);
    }

    public OtpResponse resendOtp(OtpFlowState otpFlowState) {
        ResendOtpParameter resendOtpParameter = ResendOtpParameter.builder().otpFlowState(otpFlowState).build();
        return resendOtp(resendOtpParameter);
    }

    public OtpResponse resendOtp(ResendOtpParameter resendOtpParameter) {
        return sendOtpEvent(resendOtpParameter.getOtpFlowState(), null, EVENT_ID_SEND, resendOtpParameter.getService());
    }

    public OtpResponse validateOtp(OtpFlowState otpState, String otpCode) {
        ValidateOtpParameter validateOtpParameter = ValidateOtpParameter.builder()
                .otpFlowState(otpState).otpCode(otpCode).build();
        return validateOtp(validateOtpParameter);
    }

    public OtpResponse validateOtp(ValidateOtpParameter validateOtpParameter) {
        return sendOtpEvent(validateOtpParameter.getOtpFlowState(), validateOtpParameter.getOtpCode(), EVENT_ID_VALIDATE,
                validateOtpParameter.getService());
    }

    protected String currentTokenParamName() {
        return config.getString(ConfigKeys.OTP_CURRENT_TOKEN_PARAM_NAME, ConfigKeys.OTP_CURRENT_TOKEN_PARAM_NAME_DEFAULT);
    }

    private OtpResponse sendOtpEvent(OtpFlowState otpState, String otpCode, String eventId, String service) {
        if (StringUtils.isEmpty(otpState.getSessionId()) ||
                StringUtils.isEmpty(otpState.getExecution()) ||
                StringUtils.isEmpty(otpState.getServerUrl())) {
            throw new IllegalStateException("OtpFlowState should contain all fields");
        }

        List<NameValuePair> params = commonOtpParams(service);
        params.add(new BasicNameValuePair(EXECUTION_PARAM_NAME, otpState.getExecution()));
        params.add(new BasicNameValuePair(EVENT_ID_PARAM_NAME, eventId));
        if (!StringUtils.isEmpty(otpCode)) {
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
            int status = response.getStatusLine().getStatusCode();
            String sessionIdCookie = getSessionIdCookie(cookieStore);

            HttpEntity entity = response.getEntity();
            String json = EntityUtils.toString(entity, Charsets.UTF_8);

            return prepareOtpResponse(status, json, sessionIdCookie);
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

    private List<NameValuePair> commonOtpParams(String service) {

        if (StringUtils.isEmpty(service)) {
            service = getDefaultService();
        }

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair(REALM_PARAM_NAME, config.getString(ConfigKeys.REALM, ConfigKeys.REALM_DEFAULT)));
        params.add(new BasicNameValuePair(CLIENT_ID_PARAM_NAME, config.getString(ConfigKeys.CLIENT_ID)));
        params.add(new BasicNameValuePair(CLIENT_SECRET, config.getString(ConfigKeys.CLIENT_SECRET)));
        params.add(new BasicNameValuePair(GRANT_TYPE, GRANT_TYPE_M2M));
        params.add(new BasicNameValuePair(SERVICE_PARAM_NAME, service));
        return params;
    }

    private List<NameValuePair> commonOtpParams() {
        return commonOtpParams(null);
    }

    private OtpResponse prepareOtpResponse(int status, String json, String sessionIdCookie) {
        OtpFlowStateJson otpFlowStateJson;
        try {
            JsonNode jsonNode = jsonMapper.readTree(json);
            if (jsonNode.has("token_type")) {
                TokenContextFactory factory = TokenContextFactory.get(jsonNode.get("token_type").asText());
                OtpResponseImpl response = new OtpResponseImpl();
                response.setStatus(OtpStatus.SUCCESS);
                response.setPrincipal(factory.createPrincipal(jsonMapper, jsonNode));
                return response;
            }
            if (!jsonNode.has("form") || !jsonNode.has("view")) {
                if (status >= 400) {
                    throw handleServerException(status, jsonNode);
                }
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
        otpResponse.setNextOtpCodeOperationPeriod((long) config.getInt(NEXT_OTP_OPERATION_PERIOD_PARAM_NAME, NEXT_OTP_OPERATION_PERIOD_DEFAULT_VALUE));
        otpResponse.setOtpCodeNumber(otpFlowStateJson.getView().getOtpCodeNumber());

        return otpResponse;
    }

    private ApiException handleServerException(int status, JsonNode jsonNode) {
        HttpStatus httpStatus = HttpStatus.valueOf(status);
        String message;
        if (jsonNode.has("error_description")) {
            message = jsonNode.get("error_description").asText();
        } else if (jsonNode.has("error")) {
            message = jsonNode.get("error").asText();
        } else {
            message = httpStatus.getReasonPhrase();
        }
        return new ApiException(httpStatus, message);
    }

    private Class<? extends AuthenticationResponse> resolveClass(String tokenType) {
        Class<? extends AuthenticationResponse> value = responseTypes.get(tokenType);
        return value == null ? JWTAuthenticationResponse.class : value;
    }

    private String getDefaultService() {
        return config.getString(ConfigKeys.OTP_SERVICE, ConfigKeys.OTP_SERVICE_DEFAULT);
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

