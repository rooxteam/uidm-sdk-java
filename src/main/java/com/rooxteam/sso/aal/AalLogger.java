package com.rooxteam.sso.aal;

import com.rooxteam.sso.aal.otp.OtpFlowState;
import org.apache.http.HttpResponse;
import org.jboss.logging.BasicLogger;
import org.jboss.logging.Logger;
import org.jboss.logging.annotations.*;

import java.io.IOException;

import static org.jboss.logging.Logger.Level.*;
import static org.jboss.logging.annotations.Message.Format.MESSAGE_FORMAT;

@MessageLogger(projectCode = "RX_UIDM_AAL___", length = 4)
@ValidIdRanges({
        // FATAL
        @ValidIdRange(min = 1, max = 999),
        // CRITICAL
        @ValidIdRange(min = 2001, max = 2999),
        // ERROR
        @ValidIdRange(min = 3001, max = 3999),
        // WARN
        @ValidIdRange(min = 4001, max = 4999),
        // INFO
        @ValidIdRange(min = 6001, max = 6999),
        // DEBUG
        @ValidIdRange(min = 7001, max = 7999),
        // TRACE
        @ValidIdRange(min = 9001, max = 9999)
})
public interface AalLogger extends BasicLogger {

    AalLogger LOG = Logger.getMessageLogger(AalLogger.class, AalLogger.class.getPackage().getName());

    @LogMessage(level = ERROR)
    @Message(id = 3001, format = MESSAGE_FORMAT,
            value = "Authentication login exception. For details see cause.")
    void errorAuthLogin(@Cause Exception e);

    @LogMessage(level = ERROR)
    @Message(id = 3002, format = MESSAGE_FORMAT,
            value = "Localization exception. For details see cause.")
    void errorI10n(@Cause Exception e);

    @LogMessage(level = ERROR)
    @Message(id = 3003, format = MESSAGE_FORMAT,
            value = "Login exception. For details see cause.")
    void errorLogin(@Cause Exception loginException);

    @LogMessage(level = ERROR)
    @Message(id = 3004, format = MESSAGE_FORMAT,
            value = "Exception during getting policy evaluator for service {0}. For details see cause.")
    void errorCreateEvaluator(String webAgentServiceName, @Cause Exception e);

    @LogMessage(level = ERROR)
    @Message(id = 3005, format = MESSAGE_FORMAT,
            value = "Sso token is invalid during getting policy evaluator for service {0}. For details see cause.")
    void errorSsoTokenInvalid(String webAgentServiceName, @Cause Exception e);

    @LogMessage(level = ERROR)
    @Message(id = 3009, format = MESSAGE_FORMAT,
            value = "Subject parameter should be specified.")
    void errorIllegalSubjectParameter();

    @LogMessage(level = ERROR)
    @Message(id = 3010, format = MESSAGE_FORMAT,
            value = "Resource parameter should be specified.")
    void errorIllegalResourceParameter();

    @LogMessage(level = ERROR)
    @Message(id = 3011, format = MESSAGE_FORMAT,
            value = "Action parameter should be specified.")
    void errorIllegalActionParameter();

    @LogMessage(level = ERROR)
    @Message(id = 3015, format = MESSAGE_FORMAT,
            value = "Unable to parse response json = {0}. See cause for more details.")
    void errorSendOtpUnableToParseResponseJson(String json, @Cause IOException e);

    @LogMessage(level = ERROR)
    @Message(id = 3016, format = MESSAGE_FORMAT,
            value = "Unable to close response resource. OtpFlowState = {0}. See cause for more details.")
    void errorValidateOtpUnableToCloseResponse(OtpFlowState otpState, @Cause IOException e);

    @LogMessage(level = ERROR)
    @Message(id = 3019, format = MESSAGE_FORMAT,
            value = "Error during send validation request. OtpFlowState = {0}. See cause for more details")
    void errorValidateOtpByMsisdnError(OtpFlowState otpState, @Cause IOException e);

    @LogMessage(level = ERROR)
    @Message(id = 3020, format = MESSAGE_FORMAT,
            value = "Unable to close response resource. See cause for more details.")
    void errorSendOtpUnableToCloseResponse(@Cause IOException e);

    @LogMessage(level = ERROR)
    @Message(id = 3021, format = MESSAGE_FORMAT,
            value = "Principal should contain JWT with MSISDN.")
    void errorSendOtpPrincipalNotContainsMsisdn();

    @LogMessage(level = ERROR)
    @Message(id = 3023, format = MESSAGE_FORMAT,
            value = "Cannot parse JWT {0}")
    void errorCannotParseJwt(String jwt, @Cause Exception e);

    @LogMessage(level = ERROR)
    @Message(id = 3024, format = MESSAGE_FORMAT,
            value = "Unknown response from WebSSO:\n {0}")
    void errorUnknownWebSSOResponse(String json);

    @LogMessage(level = ERROR)
    @Message(id = 3025, format = MESSAGE_FORMAT,
            value = "Token API request IO error")
    void errorTokenRequestFailed(@Cause IOException e);

    @LogMessage(level = ERROR)
    @Message(id = 3029, format = MESSAGE_FORMAT,
            value = "Unexpected Token API response: {0}")
    void errorUnexpectedTokenApiResponse(HttpResponse response);

    @LogMessage(level = ERROR)
    @Message(id = 3030, format = MESSAGE_FORMAT,
            value = "Unexpected error while executing principal event listener. For details see cause.")
    void errorExecutingPrincipalEventListener(@Cause Exception e);

    @LogMessage(level = ERROR)
    @Message(id = 3031, format = MESSAGE_FORMAT,
            value = "Got error while verifying signature.\n" +
                    "For details see cause.")
    void errorSignatureVerifyingException(@Cause Exception e);

    @LogMessage(level = ERROR)
    @Message(id = 3032, format = MESSAGE_FORMAT,
            value = "Got error while parsing signature.\n" +
                    "For details see cause.")
    void errorSignatureParsingException(@Cause Exception e);

    @LogMessage(level = ERROR)
    @Message(id = 3033, format = MESSAGE_FORMAT,
            value = "Got error while authentication.\n" +
                    "For details see cause.")
    void errorAuthentication(@Cause Exception e);

    @LogMessage(level = ERROR)
    @Message(id = 3034, format = MESSAGE_FORMAT,
            value = "Got error while authentication.\n" +
                    "Unexpected status after authentication in SSO: ''{0}''")
    void errorUnexpectedStateAfterAuthenticationInSso(String status);

    @LogMessage(level = ERROR)
    @Message(id = 3035, format = MESSAGE_FORMAT,
            value = "Invalid response obtained from the remote SSO on policy evaluation: name ''{0}'', value ''{1}''")
    void errorInvalidAdviceContentType(String adviceName, String adviceValue);

    @LogMessage(level = WARN)
    @Message(id = 4001, format = MESSAGE_FORMAT,
            value = "Token is null.")
    void warnNullSsoToken();

    @LogMessage(level = WARN)
    @Message(id = 4002, format = MESSAGE_FORMAT,
            value = "Resource is null.")
    void warnNullResource();

    @LogMessage(level = WARN)
    @Message(id = 4003, format = MESSAGE_FORMAT,
            value = "Unexpected Jwt validator creation exception.\n" +
                    "For details see cause.")
    void warnJwtValidatorGotSuppressedException(@Cause Exception e);

    @LogMessage(level = WARN)
    @Message(id = 4004, format = MESSAGE_FORMAT,
            value = "Skipping auth param {0} because it is set by AAL itself")
    void warnSkippingCommonParamInAuthRequest(String key);

    @LogMessage(level = WARN)
    @Message(id = 4005, format = MESSAGE_FORMAT,
            value = "Method is null.")
    void warnNullMethod();

    @LogMessage(level = WARN)
    @Message(id = 4006, format = MESSAGE_FORMAT,
            value = "Unable to serialize operation context into JSON: {0}")
    void warnInvalidContextJson(Object evaluationContext, @Cause Exception e);


    @LogMessage(level = TRACE)
    @Message(id = 9001, format = MESSAGE_FORMAT,
            value = "Hard call for policies for key: {0}.")
    void traceHardCallPolicyDecision(PolicyDecisionKey key);

    @LogMessage(level = TRACE)
    @Message(id = 9002, format = MESSAGE_FORMAT,
            value = "SSO authentication request: ip = {0}, jwt = {1}, clientIps = {2}")
    void traceSsoAuthenticationRequest(String ip, String jwt, String clientIps);

    @LogMessage(level = TRACE)
    @Message(id = 9004, format = MESSAGE_FORMAT,
            value = "Attempt to get policy decision by key: {0}.")
    void traceGetPolicyDecision(PolicyDecisionKey key);

    @LogMessage(level = TRACE)
    @Message(id = 9005, format = MESSAGE_FORMAT,
            value = "Init policy decision cache with size: {0}.")
    void traceInitPolicyCacheWithSize(int policyCacheSize);

    @LogMessage(level = TRACE)
    @Message(id = 9006, format = MESSAGE_FORMAT,
            value = "Init principal cache with size: {0}.")
    void traceInitPrincipalCacheWithSize(int principalCacheSize);

    @LogMessage(level = TRACE)
    @Message(id = 9007, format = MESSAGE_FORMAT,
            value = "Allowed policy decisions cache invalidating by polling.")
    void traceCacheInvalidatingByPolling();

    @LogMessage(level = TRACE)
    @Message(id = 9010, format = MESSAGE_FORMAT,
            value = "Principal has cached authorization session. Checking expiration.")
    void traceHasSSOTokenInPrincipal();

    @LogMessage(level = TRACE)
    @Message(id = 9011, format = MESSAGE_FORMAT,
            value = "Cached authorization session expired. Renewing.")
    void traceSSOTokenExpired();

    @LogMessage(level = TRACE)
    @Message(id = 9012, format = MESSAGE_FORMAT,
            value = "Failed to check cached authorization session expiration. Renewing.")
    void traceFailedToGetSSOTimeLeft(@Cause Exception e);

    @LogMessage(level = TRACE)
    @Message(id = 9013, format = MESSAGE_FORMAT,
            value = "Cached authorization session is not expired. Using it. ")
    void traceSSOTokenInPrincipalNotExpired();

    @LogMessage(level = TRACE)
    @Message(id = 9014, format = MESSAGE_FORMAT,
            value = "Principal has no valid cached authorization session.")
    void traceNoSSOTokenInPrincipal();

    @LogMessage(level = TRACE)
    @Message(id = 9020, format = MESSAGE_FORMAT,
            value = "Failed to invalidate cached authorization session.")
    void traceFailedToInvalidateSSOToken(@Cause Exception e);

    @LogMessage(level = TRACE)
    @Message(id = 9021, format = MESSAGE_FORMAT,
            value = "Failed to authenticate because IP not in pool.")
    void traceIpNotInPool();
}
