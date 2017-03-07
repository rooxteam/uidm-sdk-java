package com.rooxteam.sso.aal;

import com.codahale.metrics.Gauge;
import com.google.common.cache.Cache;
import com.iplanet.sso.SSOToken;
import com.rooxteam.sso.aal.client.*;
import com.rooxteam.sso.aal.client.model.AuthenticationResponse;
import com.rooxteam.sso.aal.client.model.Decision;
import com.rooxteam.sso.aal.client.model.EvaluationResponse;
import com.rooxteam.sso.aal.exception.AuthenticationException;
import com.rooxteam.sso.aal.metrics.AalMetricsHelper;
import com.rooxteam.sso.aal.otp.*;
import org.apache.commons.collections.MapUtils;
import org.forgerock.json.jose.common.JwtReconstruction;
import org.forgerock.json.jose.jws.SignedJwt;
import org.forgerock.json.jose.jwt.JwtClaimsSet;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import static com.rooxteam.sso.aal.AalLogger.LOG;
import static com.rooxteam.sso.aal.metrics.AalMetricsHelper.*;

/**
 * Реализация AuthenticationAuthorizationLibrary, работающая с ForgeRock OpenAM
 * и Roox UIDM.
 */
class RooxAuthenticationAuthorizationLibrary implements AuthenticationAuthorizationLibrary {

    public static final String PRINCIPAL_IS_MISSING_MESSAGE = "Principal argument is missing.";

    /**
     * how many char to trim when logging JWT
     */
    public static final int TRIM_JWT_CHARS = 50;

    private static final String AUTHENTICATE_PARAMS_SHOULDNT_BE_EMPTY = "Authenticate parameters shouldn't be empty.";
    private static final String UNSUPPORTED_AUTHENTICATION_PARAMETER = "Unsupported authentication parameter: ";
    private static final String SUBJECT_SHOULD_BE_SPECIFIED = "Subject should be specified.";
    private static final String RESOURCE_SHOULD_BE_SPECIFIED = "Resource should be specified.";
    private static final String ACTION_SHOULD_BE_SPECIFIED = "Resource should be specified.";
    private static final String IMSI_CLAIM_NAME = "imsi";

    private final SsoAuthorizationClient ssoAuthorizationClient;
    private final SsoAuthenticationClient ssoAuthenticationClient;
    private final SsoTokenClient ssoTokenClient;
    private final OtpClient otpClient;
    private final Cache<PolicyDecisionKey, EvaluationResponse> isAllowedPolicyDecisionsCache;
    private final Cache<PrincipalKey, Principal> PrincipalCache;

    private final Timer timer;
    private final CopyOnWriteArrayList<PrincipalEventListener> principalEventListeners = new CopyOnWriteArrayList<>();
    private final JwtValidator jwtValidator;
    private final AuthorizationType authorizationType;
    private volatile PollingBean pollingBean;

    RooxAuthenticationAuthorizationLibrary(Timer timer,
                                           SsoAuthorizationClient ssoAuthorizationClient,
                                           SsoAuthenticationClient ssoAuthenticationClient,
                                           SsoTokenClient ssoTokenClient,
                                           OtpClient otpClient,
                                           Cache<PolicyDecisionKey, EvaluationResponse> policyDecisionsCache,
                                           Cache<PrincipalKey, Principal> principalCache,
                                           JwtValidator jwtValidator,
                                           AuthorizationType authorizationType) {

        this.ssoAuthorizationClient = ssoAuthorizationClient;
        this.ssoAuthenticationClient = ssoAuthenticationClient;
        this.ssoTokenClient = ssoTokenClient;
        this.otpClient = otpClient;
        this.isAllowedPolicyDecisionsCache = policyDecisionsCache;
        this.PrincipalCache = principalCache;
        this.timer = timer;
        this.jwtValidator = jwtValidator;
        this.authorizationType = authorizationType;


        if (!getMetricRegistry().getNames().contains(METRIC_POLICY_DECISIONS_COUNT_IN_CACHE)) {
            getMetricRegistry().register(METRIC_POLICY_DECISIONS_COUNT_IN_CACHE,
                    new Gauge<Long>() {
                        @Override
                        public Long getValue() {
                            return isAllowedPolicyDecisionsCache.size();
                        }
                    });
        }

        if (!getMetricRegistry().getNames().contains(METRIC_PRINCIPALS_COUNT_IN_CACHE)) {
            getMetricRegistry().register(METRIC_PRINCIPALS_COUNT_IN_CACHE,
                    new Gauge<Long>() {
                        @Override
                        public Long getValue() {
                            return PrincipalCache.size();
                        }
                    });
        }
    }

    @Override
    @Deprecated
    public Principal authenticate(Map<String, ?> params, long timeOut, TimeUnit timeUnit) {
        return authenticate(params);
    }

    @Override
    public Principal authenticate(Map<String, ?> params) {
        if (MapUtils.isEmpty(params)) {
            throw new IllegalArgumentException(AUTHENTICATE_PARAMS_SHOULDNT_BE_EMPTY);
        }

        Principal result = null;

        if (!(params.containsKey(AuthParamType.IP.getValue()) || params.containsKey(AuthParamType.JWT.getValue()))) {
            throw new IllegalArgumentException(AUTHENTICATE_PARAMS_SHOULDNT_BE_EMPTY);
        }

        // search in cache by IP address
        String ip = (String) params.get(AuthParamType.IP.getValue());

        String clientIps = (String) params.get(AuthParamType.CLIENT_IPS.getValue());

        if (ip != null) {
            result = getPrincipalFromCache(new PrincipalKey(AuthParamType.IP, ip, clientIps));
        }

        if (result == null) {
            result = authenticateOnSsoServer(params);
        }

        if (result != null) {
            fireOnAuthenticate(result);
        }

        return result;
    }


    /**
     * Searches principal in cache by given key
     *
     * @param key
     * @return principal or null if not found in cache
     */
    private Principal getPrincipalFromCache(PrincipalKey key) {
        Principal result;
        result = PrincipalCache.getIfPresent(key);
        if (result != null) {
            AalMetricsHelper.getPrincipalCacheHitMeter().mark();
        } else {
            AalMetricsHelper.getPrincipalCacheMissMeter().mark();
        }
        return result;
    }

    private Principal authenticateOnSsoServer(Map<String, ?> params) {
        String ip = (String) params.get(AuthParamType.IP.getValue());

        String jwt = trimJwt((String) params.get(AuthParamType.JWT.getValue()));

        String clientIps = (String) params.get(AuthParamType.CLIENT_IPS.getValue());

        LOG.traceSsoAuthenticationRequest(ip, jwt, clientIps);
        AuthenticationResponse authResult = ssoAuthenticationClient.authenticate(params);

        if (authResult != null) {
            PrincipalImpl principal = new PrincipalImpl(authResult.getPolicyContext(), authResult.getPublicToken());
            String authType = (String) principal.getProperty(PropertyScope.SHARED_IDENTITY_PARAMS, "authType");
            if (authType != null && authType.equals(AuthParamType.IP.getValue())) {
                PrincipalCache.put(new PrincipalKey(AuthParamType.IP, ip, clientIps), principal);
                AalMetricsHelper.getPrincipalCacheAddMeter().mark();
            }
            return principal;
        } else {
            return null;
        }
    }

    /**
     * Trim passed JWT to TRIM_JWT_CHARS
     *
     * @param jwt token to trim or null
     * @return trimmed jwt or null if not passed
     */
    private String trimJwt(String jwt) {
        if (jwt == null) return null;
        if (jwt.length() >= TRIM_JWT_CHARS) {
            return jwt.substring(0, TRIM_JWT_CHARS) + "...";
        } else {
            return jwt;
        }
    }


    @Override
    @Deprecated
    public Principal renew(Principal principal, boolean updateLifeTime, long timeOut, TimeUnit timeUnit) {
        return renew(principal, updateLifeTime);
    }

    @Override
    public Principal renew(Principal principal, boolean updateLifeTime) {
        if (principal == null) {
            throw new IllegalArgumentException(PRINCIPAL_IS_MISSING_MESSAGE);
        }
        Map<String, Object> params = new HashMap<>();
        params.put(SsoAuthenticationClient.JWT_PARAM_NAME, principal.getJwtToken());
        params.put(SsoAuthenticationClient.UPDATE_LIFE_TIME_PARAM, updateLifeTime);
        AuthenticationResponse authenticationResponse = ssoAuthenticationClient.authenticate(params);
        Principal result = null;
        if (authenticationResponse != null) {
            result = new PrincipalImpl(authenticationResponse.getPolicyContext(), authenticationResponse.getPublicToken());
            fireOnRequestPrincipal(result);
        }
        return result;
    }

    @Override
    public void invalidate(final Principal principal) {
        if (principal == null) {
            throw new IllegalArgumentException(PRINCIPAL_IS_MISSING_MESSAGE);
        }
        ConcurrentMap<PrincipalKey, Principal> principalMap = PrincipalCache.asMap();
        for (Map.Entry<PrincipalKey, Principal> entry : principalMap.entrySet()) {
            if (entry.getValue().equals(principal)) {
                PrincipalCache.invalidate(entry.getKey());
            }
        }

        ConcurrentMap<PolicyDecisionKey, EvaluationResponse> policyDecisionMap = isAllowedPolicyDecisionsCache.asMap();
        for (PolicyDecisionKey key : policyDecisionMap.keySet()) {
            if (key.getSubject().equals(principal)) {
                isAllowedPolicyDecisionsCache.invalidate(key);
            }
        }

        fireOnInvalidate(principal);
    }

    @Override
    public void invalidate() {
        Collection<Principal> principals = PrincipalCache.asMap().values();
        for (Principal principal : principals) {
            fireOnInvalidate(principal);
        }
        PrincipalCache.invalidateAll();
        isAllowedPolicyDecisionsCache.invalidateAll();
    }

    @Override
    public void invalidateByImsi(final String imsi) {
        if (imsi == null) {
            throw new IllegalArgumentException("imsi");
        }
        ConcurrentMap<PrincipalKey, Principal> principalMap = PrincipalCache.asMap();
        for (Map.Entry<PrincipalKey, Principal> entry : principalMap.entrySet()) {
            Principal principal = entry.getValue();
            String token;
            if (principal instanceof PrincipalImpl) {
                token = ((PrincipalImpl) principal).getPrivateJwtToken();
            } else {
                token = principal.getJwtToken();
            }
            SignedJwt jwt = new JwtReconstruction().reconstructJwt(token, SignedJwt.class);
            JwtClaimsSet claims = jwt.getClaimsSet();
            if (claims.getClaim(IMSI_CLAIM_NAME) != null) {
                String claimImsi = claims.getClaim(IMSI_CLAIM_NAME, String.class);
                if (imsi.equalsIgnoreCase(claimImsi)) {
                    PrincipalCache.invalidate(entry.getKey());
                    fireOnInvalidate(principal);
                }
            }
        }
        ConcurrentMap<PolicyDecisionKey, EvaluationResponse> policyDecisionMap = isAllowedPolicyDecisionsCache.asMap();
        for (PolicyDecisionKey key : policyDecisionMap.keySet()) {
            Object currentImsi = key.getSubject().getProperty(PropertyScope.SHARED_IDENTITY_PARAMS, IMSI_CLAIM_NAME);
            if (imsi.equals(currentImsi)) {
                isAllowedPolicyDecisionsCache.invalidate(key);
            }
        }
    }

    @Override
    @Deprecated
    public boolean isAllowed(Principal subject, String resourceName, String actionName, Map<String, ?> envParameters, long timeOut, TimeUnit timeUnit) {
        return isAllowed(subject, resourceName, actionName, envParameters);
    }

    @Override
    @Deprecated
    public boolean isAllowed(Principal subject, String resourceName, String actionName, Map<String, ?> envParameters) {
        return evaluatePolicy(subject, resourceName, actionName, envParameters).getDecision().isPositive();
    }

    @Override
    public EvaluationResponse evaluatePolicy(Principal subject, String resourceName, String actionName, Map<String, ?> envParameters) {
        if (subject == null) {
            LOG.errorIllegalSubjectParameter();
            throw new IllegalArgumentException(SUBJECT_SHOULD_BE_SPECIFIED);
        }

        if (resourceName == null) {
            LOG.errorIllegalResourceParameter();
            throw new IllegalArgumentException(RESOURCE_SHOULD_BE_SPECIFIED);
        }

        if (actionName == null) {
            LOG.errorIllegalActionParameter();
            throw new IllegalArgumentException(ACTION_SHOULD_BE_SPECIFIED);
        }

        PolicyDecisionKey key = new PolicyDecisionKey(subject, resourceName, actionName, envParameters);
        LOG.traceGetPolicyDecision(key);
        EvaluationResponse result = isAllowedPolicyDecisionsCache.getIfPresent(key);
        if (result != null) {
            AalMetricsHelper.getPolicyCacheHitMeter().mark();
        } else {
            AalMetricsHelper.getPolicyCacheMissMeter().mark();
            result = evaluatePolicyOnResource(key);
        }

        return result;
    }

    /**
     * Make sso request to evaluate policy decision.
     * Before requesting desicion authentication is performed. If it fails method returns false immediately
     * After decision is evaluated it is added to policy decision cache.
     *
     * @param key policy request
     * @return true if allowed, false if not allowed or session is invalidated
     */
    private EvaluationResponse evaluatePolicyOnResource(PolicyDecisionKey key) {
        LOG.traceHardCallPolicyDecision(key);

        Principal subject = key.getSubject();
        String jwt = null;
        if (subject instanceof PrincipalImpl) {
            jwt = ((PrincipalImpl) subject).getPrivateJwtToken();
        } else {
            jwt = subject.getJwtToken();
        }

        EvaluationResponse result;
        switch (authorizationType) {
            default:
            case SSO_TOKEN: {
                SSOToken ssoToken = (SSOToken) subject.getProperty(PropertyScope.PRIVATE_IDENTITY_PARAMS, Principal.SESSION_PARAM);

                if (ssoToken != null) {
                    LOG.traceHasSSOTokenInPrincipal();
                    try {
                        if (ssoToken.getTimeLeft() < 0) {
                            LOG.traceSSOTokenExpired();
                            ssoToken = null;
                        } else {
                            LOG.traceSSOTokenInPrincipalNotExpired();
                        }
                    } catch (Exception e) {
                        LOG.traceFailedToGetSSOTimeLeft(e);
                    }
                }
                if (ssoToken == null) {
                    LOG.traceNoSSOTokenInPrincipal();
                    ssoToken = ssoAuthorizationClient.authenticateByJwt(jwt);
                    if (ssoToken == null) {
                        return new EvaluationResponse(Decision.Deny);
                    } else {
                        subject.setProperty(PropertyScope.PRIVATE_IDENTITY_PARAMS, Principal.SESSION_PARAM, ssoToken);
                    }
                }
                result = ssoAuthorizationClient.isActionOnResourceAllowedByPolicy(ssoToken, key.getResourceName(), key.getActionName());
                break;
            }
            case JWT: {
                result = ssoAuthorizationClient.isActionOnResourceAllowedByPolicy(jwt, key.getResourceName(), key.getActionName(), key.getEnvParameters());
                break;
            }
            case CONFIG: {
                result = ssoAuthorizationClient.isActionOnResourceAllowedByConfigPolicy(subject, key.getResourceName(), key.getActionName());
                break;
            }
        }

        isAllowedPolicyDecisionsCache.put(key, result);
        getPolicyCacheAddMeter().mark();
        return result;
    }


    @Override
    public void resetPolicies() {
        isAllowedPolicyDecisionsCache.invalidateAll();
    }

    @Override
    public void resetPolicies(final Principal principal) {
        if (principal == null) {
            LOG.errorIllegalSubjectParameter();
            throw new IllegalArgumentException(PRINCIPAL_IS_MISSING_MESSAGE);
        }
        final ConcurrentMap<PolicyDecisionKey, EvaluationResponse> decisionsMap = isAllowedPolicyDecisionsCache.asMap();
        for (Map.Entry<PolicyDecisionKey, EvaluationResponse> entry : decisionsMap.entrySet()) {
            if (entry.getKey().getSubject().equals(principal)) {
                isAllowedPolicyDecisionsCache.invalidate(entry.getKey());
            }
        }
    }

    @Override
    public Principal parseToken(String jwt) {
        throw new AuthenticationException("for authentication by jwt use AAL.authenticate");
    }

    @Override
    public Principal validate(String jwt) {
        return ssoAuthorizationClient.validate(jwt);
    }

    @Override
    public void addPrincipalListener(PrincipalEventListener listener) {
        principalEventListeners.addIfAbsent(listener);
    }

    @Override
    public void removePrincipalListener(PrincipalEventListener listener) {
        principalEventListeners.remove(listener);
    }

    @Override
    public boolean isPollingEnabled() {
        return pollingBean != null;
    }

    @Override
    public final void enablePolling(int period, TimeUnit unit) {
        // Both enable- and disable- methods are synchronized to preserve pollingBean
        // overwriting during disablePolling call.
        synchronized (timer) {
            if (isPollingEnabled()) {
                disablePolling();
            }
            pollingBean = new PollingBean(ssoTokenClient, isAllowedPolicyDecisionsCache, PrincipalCache, principalEventListeners);
            timer.schedule(pollingBean, 0, TimeUnit.MILLISECONDS.convert(period, unit));
        }
    }

    @Override
    public void disablePolling() {
        synchronized (timer) {
            if (pollingBean != null) {
                pollingBean.cancel();
                pollingBean = null;
            }
        }
    }

    @Override
    @Deprecated
    public OtpResponse sendOtp(Principal principal, long timeOut, TimeUnit timeUnit) {
        return sendOtp(principal);
    }

    @Override
    public OtpResponse sendOtp(Principal principal) {
        String jwtToken = principal != null ? principal.getJwtToken() : null;
        return otpClient.sendOtp(jwtToken);
    }

    @Override
    public OtpResponse sendOtpForOperation(Principal principal, EvaluationContext context) {
        String jwtToken = principal != null ? principal.getJwtToken() : null;
        return otpClient.sendOtpForOperation(jwtToken, context);
    }

    @Override
    public OtpResponse sendOtpForOperation(SendOtpParameter sendOtpParameter){
        return otpClient.sendOtpForOperation(sendOtpParameter);
    }

    @Override
    @Deprecated
    public OtpResponse resendOtp(OtpFlowState otpFlowState, long timeOut, TimeUnit timeUnit) {
        return resendOtp(otpFlowState);
    }

    @Override
    public OtpResponse resendOtp(OtpFlowState otpFlowState) {
        return otpClient.resendOtp(otpFlowState);
    }

    @Override
    public OtpResponse resendOtp(ResendOtpParameter resendOtpParameter) {
        return otpClient.resendOtp(resendOtpParameter);
    }

    @Override
    @Deprecated
    public OtpResponse validateOtp(OtpFlowState otpState, Map<String, String> fields, long timeOut, TimeUnit timeUnit) {
        return validateOtp(otpState, fields);
    }

    @Override
    @Deprecated
    public OtpResponse validateOtp(OtpFlowState otpState, Map<String, String> fields) {
        String otpCode = fields.get(OtpClient.OTP_CODE_PARAM_NAME);
        return validateOtp(otpState, otpCode);
    }

    @Override
    public OtpResponse validateOtp(OtpFlowState otpState, String otpCode) {
        return otpClient.validateOtp(otpState, otpCode);
    }

    @Override
    public OtpResponse validateOtp(ValidateOtpParameter validateOtpParameter){
        return otpClient.validateOtp(validateOtpParameter);
    }

    @Override
    public void close() throws Exception {
        this.timer.cancel();
    }

    private void fireOnAuthenticate(Principal principal) {
        for (PrincipalEventListener eventListener : principalEventListeners) {
            try {
                eventListener.onAuthenticate(principal);
            } catch (Exception e) {
                LOG.errorExecutingPrincipalEventListener(e);
            }
        }
    }

    private void fireOnInvalidate(Principal principal) {
        for (PrincipalEventListener eventListener : principalEventListeners) {
            try {
                eventListener.onInvalidate(principal);
            } catch (Exception e) {
                LOG.errorExecutingPrincipalEventListener(e);
            }
        }
    }

    private void fireOnRequestPrincipal(Principal principal) {
        for (PrincipalEventListener eventListener : principalEventListeners) {
            try {
                eventListener.onRequestPrincipal(principal);
            } catch (Exception e) {
                LOG.errorExecutingPrincipalEventListener(e);
            }
        }
    }
}
