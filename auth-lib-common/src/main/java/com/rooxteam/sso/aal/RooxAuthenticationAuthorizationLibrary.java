package com.rooxteam.sso.aal;

import com.nimbusds.jwt.JWT;
import com.rooxteam.sso.aal.client.*;
import com.rooxteam.sso.aal.client.model.AuthenticationResponse;
import com.rooxteam.sso.aal.client.model.EvaluationRequest;
import com.rooxteam.sso.aal.client.model.EvaluationResponse;
import com.rooxteam.sso.aal.configuration.Configuration;
import com.rooxteam.sso.aal.context.TokenContextFactory;
import com.rooxteam.sso.aal.metrics.MetricsIntegration;
import com.rooxteam.sso.aal.otp.*;
import com.rooxteam.sso.aal.validation.AccessTokenValidator;
import com.rooxteam.sso.aal.validation.jwt.ValidationResult;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import static com.rooxteam.sso.aal.AalLogger.LOG;
import static com.rooxteam.sso.aal.metrics.MetricNames.METRIC_POLICY_DECISIONS_COUNT_IN_CACHE;

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

    private static final String SUBJECT_SHOULD_BE_SPECIFIED = "Subject should be specified.";
    private static final String RESOURCE_SHOULD_BE_SPECIFIED = "Resource should be specified.";
    private static final String ACTION_SHOULD_BE_SPECIFIED = "Resource should be specified.";

    private final SsoAuthorizationClient ssoAuthorizationClient;
    private final SsoAuthenticationClient ssoAuthenticationClient;
    private final SsoTokenClient ssoTokenClient;
    private final OtpClient otpClient;
    private final Timer timer;
    private final CopyOnWriteArrayList<PrincipalEventListener> principalEventListeners =
            new CopyOnWriteArrayList<PrincipalEventListener>();
    private final MetricsIntegration metricsIntegration;
    private final Configuration configuration;
    private final PrincipalProvider principalProvider;
    private final AccessTokenValidator accessTokenValidator;

    RooxAuthenticationAuthorizationLibrary(Configuration configuration,
                                           Timer timer,
                                           SsoAuthorizationClient ssoAuthorizationClient,
                                           SsoAuthenticationClient ssoAuthenticationClient,
                                           SsoTokenClient ssoTokenClient,
                                           OtpClient otpClient,
                                           PrincipalProvider principalProvider,
                                           AccessTokenValidator accessTokenValidator,
                                           MetricsIntegration metricsIntegration) {
        this.configuration = configuration;
        this.ssoAuthorizationClient = ssoAuthorizationClient;
        this.ssoAuthenticationClient = ssoAuthenticationClient;
        this.ssoTokenClient = ssoTokenClient;
        this.otpClient = otpClient;
        this.timer = timer;
        this.principalProvider = principalProvider;
        this.accessTokenValidator = accessTokenValidator;
        this.metricsIntegration = metricsIntegration;

    }

    @Override
    @Deprecated
    public Principal renew(Principal principal,
                           boolean updateLifeTime,
                           long timeOut,
                           TimeUnit timeUnit) {
        return renew(principal, updateLifeTime);
    }

    @Override
    public Principal renew(Principal principal,
                           boolean updateLifeTime) {
        if (principal == null) {
            throw new IllegalArgumentException(PRINCIPAL_IS_MISSING_MESSAGE);
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(SsoAuthenticationClient.JWT_PARAM_NAME, principal.getJwtToken());
        params.put(SsoAuthenticationClient.UPDATE_LIFE_TIME_PARAM, updateLifeTime);
        AuthenticationResponse authenticationResponse = ssoAuthenticationClient.authenticate(params);
        Principal result = null;
        if (authenticationResponse != null) {
            result = TokenContextFactory.get(TokenContextFactory.TYPE.JWTToken).createPrincipal(authenticationResponse);
            fireOnRequestPrincipal(result);
        }
        return result;
    }

    @Override
    public void invalidate(final Principal principal) {
        if (principal == null) {
            throw new IllegalArgumentException(PRINCIPAL_IS_MISSING_MESSAGE);
        }

        fireOnInvalidate(principal);
    }

    @Override
    public void invalidate() {
    }

    @Override
    @Deprecated
    public boolean isAllowed(Principal subject,
                             String resourceName,
                             String actionName,
                             Map<String, ?> envParameters,
                             long timeOut,
                             TimeUnit timeUnit) {
        return isAllowed(subject, resourceName, actionName, envParameters);
    }

    @Override
    @Deprecated
    public boolean isAllowed(Principal subject,
                             String resourceName,
                             String actionName,
                             Map<String, ?> envParameters) {
        return evaluatePolicy(subject, resourceName, actionName, envParameters).getDecision().isPositive();
    }

    @Override
    public EvaluationResponse evaluatePolicy(Principal subject,
                                             String resourceName,
                                             String actionName,
                                             Map<String, ?> envParameters) {
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

        return evaluatePolicyOnResource(key);
    }

    public Map<EvaluationRequest, EvaluationResponse> evaluatePolicies(Principal subject,
                                                                       List<EvaluationRequest> policiesToCheck) {
        if (subject == null) {
            LOG.errorIllegalSubjectParameter();
            throw new IllegalArgumentException(SUBJECT_SHOULD_BE_SPECIFIED);
        }
        if (policiesToCheck == null || policiesToCheck.isEmpty()) {
            return Collections.emptyMap();
        }

        return ssoAuthorizationClient.whichActionAreAllowed(subject, policiesToCheck);
    }

    @Override
    public String postprocessPolicy(Principal subject,
                                    String resourceName,
                                    String actionName,
                                    Map<String, ?> envParameters, String response) {
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
        LOG.tracePostprocessPolicy(key);
        return ssoAuthorizationClient.postprocess(subject, key.getResourceName(), key.getActionName(), key.getEnvParameters(), response);
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

        return ssoAuthorizationClient.isActionOnResourceAllowedByPolicy(subject,
                key.getResourceName(), key.getActionName(), key.getEnvParameters());
    }


    @Override
    public void resetPolicies() {

    }

    @Override
    public void resetPolicies(final Principal principal) {
        if (principal == null) {
            LOG.errorIllegalSubjectParameter();
            throw new IllegalArgumentException(PRINCIPAL_IS_MISSING_MESSAGE);
        }
    }

    @Override
    public Principal validate(HttpServletRequest request, String accessToken) {
        return getPreAuthenticatedUserPrincipal(request, accessToken);
    }

    @Override
    public Principal getPreAuthenticatedUserPrincipal(HttpServletRequest request, String token) {
        return principalProvider.getPrincipal(request, token);
    }

    @Override
    public ValidationResult validateJWT(JWT jwtToken) {
        return accessTokenValidator.validate(jwtToken);
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
    @Deprecated
    public OtpResponse sendOtp(Principal principal,
                               long timeOut,
                               TimeUnit timeUnit) {
        return sendOtp(principal);
    }

    @Override
    public OtpResponse sendOtp(Principal principal) {
        String jwtToken = principal != null ? principal.getJwtToken() : null;
        String realm = fetchRealm(principal);
        return otpClient.sendOtp(realm, jwtToken);
    }

    @Override
    public OtpResponse sendOtpForOperation(Principal principal,
                                           EvaluationContext context) {
        String jwtToken = principal != null ? principal.getJwtToken() : null;
        String realm = fetchRealm(principal);
        return otpClient.sendOtpForOperation(realm, jwtToken, context);
    }

    private String fetchRealm(Principal principal) {
        if (principal != null) {
            String realm = (String) principal.getProperty("realm");
            if (realm != null && !realm.isEmpty()) {
                return realm;
            }
        }
        return configuration.getString(ConfigKeys.REALM, ConfigKeys.REALM_DEFAULT);
    }

    @Override
    public OtpResponse sendOtpForOperation(SendOtpParameter sendOtpParameter) {
        return otpClient.sendOtpForOperation(sendOtpParameter);
    }

    @Override
    @Deprecated
    public OtpResponse resendOtp(OtpFlowState otpFlowState,
                                 long timeOut,
                                 TimeUnit timeUnit) {
        return resendOtp(null, otpFlowState);
    }

    @Override
    @Deprecated
    public OtpResponse resendOtp(OtpFlowState otpFlowState) {
        return otpClient.resendOtp(null, otpFlowState);
    }

    @Override
    public OtpResponse resendOtp(String realm,
                                 OtpFlowState otpFlowState) {
        return otpClient.resendOtp(realm, otpFlowState);
    }

    @Override
    public OtpResponse resendOtp(ResendOtpParameter resendOtpParameter) {
        return otpClient.resendOtp(resendOtpParameter);
    }

    @Override
    @Deprecated
    public OtpResponse validateOtp(OtpFlowState otpState,
                                   Map<String, String> fields,
                                   long timeOut,
                                   TimeUnit timeUnit) {
        return validateOtp(otpState, fields);
    }

    @Override
    @Deprecated
    public OtpResponse validateOtp(OtpFlowState otpState,
                                   Map<String, String> fields) {
        String otpCode = fields.get(OtpClient.OTP_CODE_PARAM_NAME);
        return validateOtp(otpState, otpCode);
    }

    @Override
    public OtpResponse validateOtp(OtpFlowState otpState,
                                   String otpCode) {
        return validateOtp(null, otpState, otpCode);
    }

    @Override
    public OtpResponse validateOtp(String realm,
                                   OtpFlowState otpState,
                                   String otpCode) {
        return otpClient.validateOtp(otpState, otpCode);
    }

    @Override
    public OtpResponse validateOtp(ValidateOtpParameter validateOtpParameter) {
        return otpClient.validateOtp(validateOtpParameter);
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public void close() {
        this.timer.cancel();
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
