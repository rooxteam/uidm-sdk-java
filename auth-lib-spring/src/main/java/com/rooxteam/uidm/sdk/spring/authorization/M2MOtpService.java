package com.rooxteam.uidm.sdk.spring.authorization;

import com.rooxteam.sso.aal.AuthenticationAuthorizationLibrary;
import com.rooxteam.sso.aal.Principal;
import com.rooxteam.sso.aal.PrincipalImpl;
import com.rooxteam.sso.aal.client.EvaluationContext;
import com.rooxteam.sso.aal.otp.OtpFlowState;
import com.rooxteam.sso.aal.otp.OtpResponse;
import com.rooxteam.sso.aal.otp.OtpResponseImpl;
import com.rooxteam.sso.aal.otp.OtpStatus;
import com.rooxteam.sso.aal.otp.ResendOtpParameter;
import com.rooxteam.sso.aal.otp.SendOtpParameter;
import com.rooxteam.sso.aal.otp.ValidateOtpParameter;
import com.rooxteam.uidm.sdk.spring.UidmSdkSpringLogger;

public class M2MOtpService {

    private AuthenticationAuthorizationLibrary aal;

    public M2MOtpService(AuthenticationAuthorizationLibrary aal) {
        this.aal = aal;
    }

    public Response send(Principal caller,
                         EvaluationContext ctx) {
        OtpResponse result = aal.sendOtpForOperation(caller, ctx);
        return convert(result);
    }

    public Response send(SendOtpParameter sendOtpParameter) {
        OtpResponse result = aal.sendOtpForOperation(sendOtpParameter);
        return convert(result);
    }

    public Response resend(OtpFlowState state) {
        ResendOtpParameter resendOtpParameter = ResendOtpParameter.builder()
                .otpFlowState(state)
                .build();
        return resend(resendOtpParameter);
    }

    public Response resend(ResendOtpParameter resendOtpParameter) {
        OtpResponse result = aal.resendOtp(resendOtpParameter);
        return convert(result);
    }

    public Response validate(OtpFlowState state,
                             String otp) {
        ValidateOtpParameter validateOtpParameter = ValidateOtpParameter.builder()
                .otpFlowState(state)
                .otpCode(otp)
                .build();
        return validate(validateOtpParameter);
    }

    public Response validate(ValidateOtpParameter validateOtpParameter) {
        OtpResponse result = aal.validateOtp(validateOtpParameter);
        return convert(result);
    }

    private Response convert(OtpResponse result) {
        if (result.getStatus() == OtpStatus.EXCEPTION && result instanceof OtpResponseImpl) {
            Exception e = ((OtpResponseImpl) result).getException();
            UidmSdkSpringLogger.LOG.warnExceptionOnAalOtpRequest(e);
        }
        String jwt = null;
        if (result.getPrincipal() != null && result.getPrincipal() instanceof PrincipalImpl) {
            final PrincipalImpl principal = (PrincipalImpl) result.getPrincipal();
            jwt = principal.getPrivateJwtToken();
        }
        return new Response(result.getStatus(), result.getOtpFlowState(), result.getRequiredFieldNames(),
                result.getAvailableAttempts(), jwt, result.getBlockedFor(), result.getNextOtpCodeOperationPeriod(),
                result.getOtpCodeNumber(), result.getMethod(), result.getExtendedAttributes());
    }
}
