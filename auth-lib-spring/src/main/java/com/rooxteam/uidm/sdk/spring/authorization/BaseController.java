package com.rooxteam.uidm.sdk.spring.authorization;

import com.rooxteam.errors.exception.BadRequestException;
import com.rooxteam.errors.exception.ErrorTranslator;
import com.rooxteam.sso.aal.otp.OtpFlowStateImpl;
import com.rooxteam.sso.aal.otp.ResendOtpParameter;
import com.rooxteam.sso.aal.otp.ValidateOtpParameter;
import com.rooxteam.uidm.sdk.spring.authentication.AuthenticationState;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@AllArgsConstructor
public class BaseController {
    protected final M2MOtpService otpService;
    private final ErrorTranslator errorTranslator;

    protected Response resendOtp(OtpFlowStateImpl state,
                                 String service,
                                 java.security.Principal principal) {
        final AuthenticationState authentication = principal instanceof AuthenticationState
                ? (AuthenticationState) principal : null;
        String realm = authentication != null ? authentication.getRealm() : null;
        ResendOtpParameter resendOtpParameter = ResendOtpParameter.builder()
                .otpFlowState(state)
                .service(service)
                .realm(realm)
                .build();
        return otpService.resend(resendOtpParameter);
    }

    protected ResponseEntity<?> validateOtp(final OtpFlowStateImpl state,
                                            final String otp,
                                            final String otpCode,
                                            final String service,
                                            java.security.Principal principal) {
        if (otp == null && otpCode == null) {
            return errorTranslator.translate(new BadRequestException("Parameter is missing: otpCode"));
        }
        final AuthenticationState authentication = principal instanceof AuthenticationState
                ? (AuthenticationState) principal : null;
        String realm = authentication != null ? authentication.getRealm() : null;
        ValidateOtpParameter validateOtpParameter = ValidateOtpParameter.builder()
                .otpFlowState(state)
                .otpCode(otpCode != null ? otpCode : otp)
                .service(service)
                .realm(realm)
                .build();
        Response response = otpService.validate(validateOtpParameter);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    protected Response check(OtpFlowStateImpl state,
                             String service,
                             java.security.Principal principal) {
        final AuthenticationState authentication = principal instanceof AuthenticationState
                ? (AuthenticationState) principal : null;
        String realm = authentication != null ? authentication.getRealm() : null;

        ValidateOtpParameter validateOtpParameter = ValidateOtpParameter.builder()
                .otpFlowState(state)
                .service(service)
                .realm(realm)
                .build();
        return otpService.validate(validateOtpParameter);
    }
}
