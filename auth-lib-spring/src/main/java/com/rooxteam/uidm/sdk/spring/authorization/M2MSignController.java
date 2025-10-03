package com.rooxteam.uidm.sdk.spring.authorization;

import com.rooxteam.errors.auth.exception.ErrorTranslator;
import com.rooxteam.sso.aal.Principal;
import com.rooxteam.sso.aal.otp.OtpFlowStateImpl;
import com.rooxteam.sso.aal.otp.SendOtpParameter;
import com.rooxteam.sso.aal.realms.RealmDetermineService;
import com.rooxteam.uidm.sdk.spring.authentication.AuthenticationState;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class M2MSignController extends BaseController {

    public static final String SERVICE_NAME = "sign_document_batch";

    public M2MSignController(M2MOtpService otpService, ErrorTranslator errorTranslator, RealmDetermineService realmDetermineService) {
        super(otpService, errorTranslator, realmDetermineService);
    }

    @RequestMapping(method = POST, value = "/sign-operation/send")
    public Response sendOtp(@RequestParam String signingRequestId,
                            @RequestParam(required = false) String category,
                            AuthenticationState authentication) {
        return sendOtp(signingRequestId, category, realmDetermineService.determine(), authentication);
    }

    @RequestMapping(method = POST, value = "/realms/{realm}/sign-operation/send")
    public Response sendOtp(@RequestParam String signingRequestId,
                            @RequestParam(required = false) String category,
                            @PathVariable String realm,
                            AuthenticationState authentication) {
        Principal caller = authentication != null ? (Principal) authentication.getAttributes().get("aalPrincipal") : null;
        String jwtToken = caller != null ? caller.getJwtToken() : null;
        validateRealm(realm, authentication);
        SendOtpParameter sendOtpParameter = SendOtpParameter.builder()
                .jwt(jwtToken)
                .category(category)
                .service(SERVICE_NAME)
                .realm(realm)
                .signingRequestId(signingRequestId)
                .build();
        return otpService.send(sendOtpParameter);
    }

    @RequestMapping(method = POST, value = "/sign-operation/resend")
    public Response resendOtp(@RequestBody OtpFlowStateImpl state,
                              AuthenticationState authentication) {
        return super.resendOtp(state, SERVICE_NAME, realmDetermineService.determine(), authentication);
    }

    @RequestMapping(method = POST, value = "/realms/{realm}/sign-operation/resend")
    public Response resendOtp(@RequestBody OtpFlowStateImpl state,
                              @PathVariable String realm,
                              AuthenticationState authentication) {
        return super.resendOtp(state, SERVICE_NAME, realm, authentication);
    }

    @RequestMapping(method = POST, value = "/sign-operation/validate")
    public ResponseEntity<?> validateOtp(@RequestBody final OtpFlowStateImpl state,
                                         @RequestParam(required = false) final String otp,
                                         @RequestParam(required = false) final String otpCode,
                                         AuthenticationState authentication) {
        return super.validateOtp(state, otp, otpCode, SERVICE_NAME, realmDetermineService.determine(), authentication);
    }

    @RequestMapping(method = POST, value = "/realms/{realm}/sign-operation/validate")
    public ResponseEntity<?> validateOtp(@RequestBody final OtpFlowStateImpl state,
                                         @RequestParam(required = false) final String otp,
                                         @RequestParam(required = false) final String otpCode,
                                         @PathVariable String realm,
                                         AuthenticationState authentication) {
        return super.validateOtp(state, otp, otpCode, SERVICE_NAME, realm, authentication);
    }

    @RequestMapping(method = POST, value = "/sign-operation/check")
    public Response check(@RequestBody OtpFlowStateImpl state,
                          AuthenticationState authentication) {
        return super.check(state, SERVICE_NAME, realmDetermineService.determine(), authentication);
    }

    @RequestMapping(method = POST, value = "/realms/{realm}/sign-operation/check")
    public Response check(@RequestBody OtpFlowStateImpl state,
                          @PathVariable String realm,
                          AuthenticationState authentication) {
        return super.check(state, SERVICE_NAME, realm, authentication);
    }
}
