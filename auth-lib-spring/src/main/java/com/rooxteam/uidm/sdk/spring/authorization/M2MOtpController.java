package com.rooxteam.uidm.sdk.spring.authorization;

import com.rooxteam.errors.exception.BadRequestException;
import com.rooxteam.errors.exception.ErrorTranslator;
import com.rooxteam.sso.aal.Principal;
import com.rooxteam.sso.aal.client.EvaluationContext;
import com.rooxteam.sso.aal.otp.OtpFlowStateImpl;
import com.rooxteam.sso.aal.otp.ResendOtpParameter;
import com.rooxteam.sso.aal.otp.SendOtpParameter;
import com.rooxteam.sso.aal.otp.ValidateOtpParameter;
import com.rooxteam.uidm.sdk.spring.authentication.AuthenticationState;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/otp")
public class M2MOtpController {

    private final M2MOtpService otpService;
    private final ErrorTranslator errorTranslator;

    public M2MOtpController(M2MOtpService otpService, ErrorTranslator errorTranslator) {
        this.otpService = otpService;
        this.errorTranslator = errorTranslator;
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
        SendOtpParameter sendOtpParameter = SendOtpParameter.builder()
                .jwt(jwtToken)
                .msisdn(msisdn)
                .category(category)
                .service(service)
                .evaluationContext(ctx)
                .build();
        return otpService.send(sendOtpParameter);
    }

    @RequestMapping(method = POST, value = "/resend")
    public Response resendOtp(@RequestBody OtpFlowStateImpl state, @RequestParam(required = false) String service) {
        ResendOtpParameter resendOtpParameter = ResendOtpParameter.builder()
                .otpFlowState(state)
                .service(service)
                .build();
        return otpService.resend(resendOtpParameter);
    }

    @RequestMapping(method = POST, value = "/validate")
    public ResponseEntity validateOtp(@RequestBody final OtpFlowStateImpl state,
                                      @RequestParam(required = false) final String otp,
                                      @RequestParam(required = false) final String otpCode,
                                      @RequestParam(required = false) final String service) {
        if (otp == null && otpCode == null) {
            return errorTranslator.translate(new BadRequestException("Parameter is missing: otpCode"));
        }
        ValidateOtpParameter validateOtpParameter = ValidateOtpParameter.builder()
                .otpFlowState(state)
                .otpCode(otpCode != null ? otpCode : otp)
                .service(service)
                .build();
        return new ResponseEntity<Response>(otpService.validate(validateOtpParameter), HttpStatus.OK);
    }

    @RequestMapping(method = POST, value = "/check")
    public Response check(@RequestBody OtpFlowStateImpl state,
                          @RequestParam(required = false) String service
    ) {
        ValidateOtpParameter validateOtpParameter = ValidateOtpParameter.builder()
                .otpFlowState(state)
                .service(service)
                .build();
        return otpService.validate(validateOtpParameter);
    }
}
