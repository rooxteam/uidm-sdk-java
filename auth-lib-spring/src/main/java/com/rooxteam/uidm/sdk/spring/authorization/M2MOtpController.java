package com.rooxteam.uidm.sdk.spring.authorization;

import com.rooxteam.errors.exception.ErrorTranslator;
import com.rooxteam.sso.aal.Principal;
import com.rooxteam.sso.aal.client.EvaluationContext;
import com.rooxteam.sso.aal.otp.OtpFlowStateImpl;
import com.rooxteam.sso.aal.otp.SendOtpParameter;
import com.rooxteam.uidm.sdk.spring.authentication.AuthenticationState;
import com.rooxteam.uidm.sdk.spring.utils.RawRequest;
import com.rooxteam.uidm.sdk.spring.utils.RequestData;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/otp")
public class M2MOtpController extends BaseController {

    public M2MOtpController(M2MOtpService otpService, ErrorTranslator errorTranslator, RequestData requestData) {
        super(otpService, errorTranslator, requestData);
    }

    @RequestMapping(method = POST, value = "/send")
    public Response sendOtp(@RequestBody EvaluationContext ctx,
                            @RequestParam(required = false) String msisdn,
                            @RequestParam(required = false) String category,
                            @RequestParam(required = false) String service,
                            java.security.Principal principal) {
        if (principal == null && SecurityContextHolder.getContext() != null) { // What for? For back compatibility. TODO: to be removed
            principal = SecurityContextHolder.getContext().getAuthentication();
        }
        final AuthenticationState authentication = principal instanceof AuthenticationState
            ? (AuthenticationState) principal : null;
        Principal caller = authentication != null ? (Principal) authentication.getAttributes().get("aalPrincipal") : null;
        String jwtToken = caller != null ? caller.getJwtToken() : null;
        String realm = authentication != null ? authentication.getRealm() : null;
        SendOtpParameter sendOtpParameter = SendOtpParameter.builder()
                .jwt(jwtToken)
                .msisdn(msisdn)
                .category(category)
                .service(service)
                .realm(realm)
                .evaluationContext(ctx)
                .build();
        return otpService.send(sendOtpParameter);
    }

    @Override
    @RequestMapping(method = POST, value = "/resend")
    public Response resendOtp(@RequestBody OtpFlowStateImpl state,
                              @RequestParam(required = false) String service,
                              java.security.Principal principal) {
        return super.resendOtp(state, service, principal);
    }

    @Override
    @RawRequest
    @RequestMapping(method = POST, value = "/validate")
    public ResponseEntity<?> validateOtp(@RequestBody final OtpFlowStateImpl state,
                                         @RequestParam(required = false) final String otp,
                                         @RequestParam(required = false) final String otpCode,
                                         @RequestParam(required = false) final String service,
                                         java.security.Principal principal) {
        return super.validateOtp(state, otp, otpCode, service, principal);
    }

    @Override
    @RequestMapping(method = POST, value = "/check")
    public Response check(@RequestBody OtpFlowStateImpl state,
                          @RequestParam(required = false) String service,
                          java.security.Principal principal) {
        return super.check(state, service, principal);
    }
}
