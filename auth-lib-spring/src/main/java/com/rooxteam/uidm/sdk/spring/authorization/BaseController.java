package com.rooxteam.uidm.sdk.spring.authorization;

import com.rooxteam.errors.auth.exception.AuthApiException;
import com.rooxteam.errors.auth.exception.AuthBadRequestException;
import com.rooxteam.errors.auth.exception.ErrorTranslator;
import com.rooxteam.sso.aal.otp.OtpFlowStateImpl;
import com.rooxteam.sso.aal.otp.ResendOtpParameter;
import com.rooxteam.sso.aal.otp.ValidateOtpParameter;
import com.rooxteam.sso.aal.realms.RealmDetermineService;
import com.rooxteam.uidm.sdk.spring.authentication.AuthenticationState;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@AllArgsConstructor
public abstract class BaseController {
    protected final M2MOtpService otpService;
    private final ErrorTranslator errorTranslator;
    protected final RealmDetermineService realmDetermineService;

    protected Response resendOtp(OtpFlowStateImpl state,
                                 String service,
                                 String realm,
                                 AuthenticationState authentication) {
        validateRealm(realm, authentication);
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
                                            final String realm,
                                            AuthenticationState authentication) {
        validateRealm(realm, authentication);
        if (otp == null && otpCode == null) {
            return errorTranslator.translate(new AuthBadRequestException("Parameter is missing: otpCode"));
        }
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
                             String realm,
                             AuthenticationState authentication) {
        validateRealm(realm, authentication);
        ValidateOtpParameter validateOtpParameter = ValidateOtpParameter.builder()
                .otpFlowState(state)
                .service(service)
                .realm(realm)
                .build();
        return otpService.validate(validateOtpParameter);
    }

    protected void validateRealm(String realm, AuthenticationState authentication) {
        String principalRealm = authentication != null ? authentication.getRealm() : null;
        if (!realm.equals(principalRealm)) {
            throw new AuthApiException(403, "Realm not matched");
        }
    }
}
