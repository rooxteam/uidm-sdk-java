package com.rooxteam.uidm.sdk.spring.authorization;

import com.rooxteam.errors.exception.ErrorTranslator;
import com.rooxteam.sso.aal.Principal;
import com.rooxteam.sso.aal.otp.OtpFlowStateImpl;
import com.rooxteam.sso.aal.otp.SendOtpParameter;
import com.rooxteam.uidm.sdk.spring.authentication.AuthenticationState;
import com.rooxteam.uidm.sdk.spring.utils.RequestData;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/sign-operation")
public class M2MSignController extends BaseController {

    public static final String SERVICE_NAME = "sign_document_batch";

    public M2MSignController(M2MOtpService otpService, ErrorTranslator errorTranslator, RequestData requestData) {
        super(otpService, errorTranslator, requestData);
    }

    @RequestMapping(method = POST, value = "/send")
    public Response sendOtp(@RequestParam String signingRequestId,
                            @RequestParam(required = false) String category,
                            java.security.Principal principal) {
        final AuthenticationState authentication = principal instanceof AuthenticationState
            ? (AuthenticationState) principal : null;
        Principal caller = authentication != null ? (Principal) authentication.getAttributes().get("aalPrincipal") : null;
        String jwtToken = caller != null ? caller.getJwtToken() : null;
        String realm = authentication != null ? authentication.getRealm() : null;
        SendOtpParameter sendOtpParameter = SendOtpParameter.builder()
                .jwt(jwtToken)
                .category(category)
                .service(SERVICE_NAME)
                .realm(realm)
                .signingRequestId(signingRequestId)
                .build();
        return otpService.send(sendOtpParameter);
    }

    @RequestMapping(method = POST, value = "/resend")
    public Response resendOtp(@RequestBody OtpFlowStateImpl state,
                              java.security.Principal principal) {
        return super.resendOtp(state, SERVICE_NAME, principal);
    }

    @RequestMapping(method = POST, value = "/validate")
    public ResponseEntity<?> validateOtp(@RequestBody final OtpFlowStateImpl state,
                                      @RequestParam(required = false) final String otp,
                                      @RequestParam(required = false) final String otpCode,
                                      java.security.Principal principal) {
        return super.validateOtp(state, otp, otpCode, SERVICE_NAME, principal);
    }

    @RequestMapping(method = POST, value = "/check")
    public Response check(@RequestBody OtpFlowStateImpl state,
                          java.security.Principal principal) {
        return super.check(state, SERVICE_NAME, principal);
    }
}
