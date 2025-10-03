package com.rooxteam.sso.aal.realms.impl;

import com.nimbusds.jwt.JWT;
import com.rooxteam.sso.aal.AalFactory;
import com.rooxteam.sso.aal.AuthenticationAuthorizationLibrary;
import com.rooxteam.sso.aal.Principal;
import com.rooxteam.sso.aal.PrincipalEventListener;
import com.rooxteam.sso.aal.client.EvaluationContext;
import com.rooxteam.sso.aal.client.model.EvaluationRequest;
import com.rooxteam.sso.aal.client.model.EvaluationResponse;
import com.rooxteam.sso.aal.configuration.Configuration;
import com.rooxteam.sso.aal.otp.OtpFlowState;
import com.rooxteam.sso.aal.otp.OtpResponse;
import com.rooxteam.sso.aal.otp.ResendOtpParameter;
import com.rooxteam.sso.aal.otp.SendOtpParameter;
import com.rooxteam.sso.aal.otp.ValidateOtpParameter;
import com.rooxteam.sso.aal.realms.RealmDetermineService;
import com.rooxteam.sso.aal.validation.jwt.ValidationResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author sergey.syroezhkin
 * @since 04.08.2025
 */

public class RealmBasedAuthenticationAuthorizationLibrary implements AuthenticationAuthorizationLibrary {

    private final Map<String, AuthenticationAuthorizationLibrary> delegatesAalMap = new HashMap<>();
    private final Configuration realmBasedConfiguration;
    private final RealmDetermineService realmDetermineService;

    public RealmBasedAuthenticationAuthorizationLibrary(Configuration baseConfiguration,
                                                        RealmDetermineService realmDetermineService) {
        this.realmDetermineService = realmDetermineService;
        this.realmBasedConfiguration = RealmBasedConfiguration.of(baseConfiguration, realmDetermineService::determine);
    }

    private AuthenticationAuthorizationLibrary getOrCreateDelegate() {
        String realm = realmDetermineService.determine();
        return delegatesAalMap.computeIfAbsent(realm, k -> AalFactory.create(realmBasedConfiguration));
    }


    @Override
    public Principal renew(Principal principal, boolean updateLifeTime, long timeOut, TimeUnit timeUnit) {
        return getOrCreateDelegate().renew(principal, updateLifeTime, timeOut, timeUnit);
    }

    @Override
    public Principal renew(Principal principal, boolean updateLifeTime) {
        return getOrCreateDelegate().renew(principal, updateLifeTime);
    }

    @Override
    public void invalidate(Principal principal) {
        getOrCreateDelegate().invalidate(principal);
    }

    @Override
    public void invalidate() {
        getOrCreateDelegate().invalidate();
    }

    @Override
    public boolean isAllowed(Principal subject, String resourceName, String actionName, Map<String, ?> envParameters, long timeOut, TimeUnit timeUnit) {
        return getOrCreateDelegate().isAllowed(subject, resourceName, actionName, envParameters, timeOut, timeUnit);
    }

    @Override
    public boolean isAllowed(Principal subject, String resourceName, String actionName, Map<String, ?> envParameters) {
        return getOrCreateDelegate().isAllowed(subject, resourceName, actionName, envParameters);
    }

    @Override
    public EvaluationResponse evaluatePolicy(Principal subject, String resourceName, String actionName, Map<String, ?> envParameters) {
        return getOrCreateDelegate().evaluatePolicy(subject, resourceName, actionName, envParameters);
    }

    @Override
    public Map<EvaluationRequest, EvaluationResponse> evaluatePolicies(Principal subject, List<EvaluationRequest> policiesToCheck) {
        return getOrCreateDelegate().evaluatePolicies(subject, policiesToCheck);
    }

    @Override
    public String postprocessPolicy(Principal subject, String resourceName, String actionName, Map<String, ?> envParameters, String response) {
        return getOrCreateDelegate().postprocessPolicy(subject, resourceName, actionName, envParameters, response);
    }

    @Override
    public void resetPolicies() {
        getOrCreateDelegate().resetPolicies();
    }

    @Override
    public void resetPolicies(Principal principal) {
        getOrCreateDelegate().resetPolicies(principal);
    }

    @Override
    public Principal validate(String accessToken) {
        return getOrCreateDelegate().validate(accessToken);
    }

    @Override
    public Principal getPreAuthenticatedUserPrincipal(String accessToken) {
        return getOrCreateDelegate().getPreAuthenticatedUserPrincipal(accessToken);
    }

    @Override
    public ValidationResult validateJWT(JWT jwt) {
        return getOrCreateDelegate().validateJWT(jwt);
    }

    @Override
    public void addPrincipalListener(PrincipalEventListener listener) {
        getOrCreateDelegate().addPrincipalListener(listener);
    }

    @Override
    public void removePrincipalListener(PrincipalEventListener listener) {
        getOrCreateDelegate().removePrincipalListener(listener);
    }

    @Override
    public OtpResponse sendOtp(Principal principal, long timeOut, TimeUnit timeUnit) {
        return getOrCreateDelegate().sendOtp(principal, timeOut, timeUnit);
    }

    @Override
    public OtpResponse sendOtp(Principal principal) {
        return getOrCreateDelegate().sendOtp(principal);
    }

    @Override
    public OtpResponse sendOtpForOperation(Principal principal, EvaluationContext context) {
        return getOrCreateDelegate().sendOtpForOperation(principal, context);
    }

    @Override
    public OtpResponse sendOtpForOperation(SendOtpParameter sendOtpParameter) {
        return getOrCreateDelegate().sendOtpForOperation(sendOtpParameter);
    }

    @Override
    public OtpResponse resendOtp(OtpFlowState otpFlowState, long timeOut, TimeUnit timeUnit) {
        return getOrCreateDelegate().resendOtp(otpFlowState, timeOut, timeUnit);
    }

    @Override
    public OtpResponse resendOtp(OtpFlowState otpFlowState) {
        return getOrCreateDelegate().resendOtp(otpFlowState);
    }

    @Override
    public OtpResponse resendOtp(String realm, OtpFlowState otpFlowState) {
        return getOrCreateDelegate().resendOtp(realm, otpFlowState);
    }

    @Override
    public OtpResponse resendOtp(ResendOtpParameter resendOtpParameter) {
        return getOrCreateDelegate().resendOtp(resendOtpParameter);
    }

    @Override
    public OtpResponse validateOtp(OtpFlowState otpState, Map<String, String> fields, long timeOut, TimeUnit timeUnit) {
        return getOrCreateDelegate().validateOtp(otpState, fields, timeOut, timeUnit);
    }

    @Override
    public OtpResponse validateOtp(OtpFlowState otpState, Map<String, String> fields) {
        return getOrCreateDelegate().validateOtp(otpState, fields);
    }

    @Override
    public OtpResponse validateOtp(OtpFlowState otpState, String otpCode) {
        return getOrCreateDelegate().validateOtp(otpState, otpCode);
    }

    @Override
    public OtpResponse validateOtp(String realm, OtpFlowState otpState, String otpCode) {
        return getOrCreateDelegate().validateOtp(realm, otpState, otpCode);
    }

    @Override
    public OtpResponse validateOtp(ValidateOtpParameter validateOtpParameter) {
        return getOrCreateDelegate().validateOtp(validateOtpParameter);
    }

    @Override
    public Configuration getConfiguration() {
        return realmBasedConfiguration;
    }

    @Override
    public RealmDetermineService getRealmDetermineService() {
        return realmDetermineService;
    }

    @Override
    public void close() throws Exception {
        getOrCreateDelegate().close();
    }
}
