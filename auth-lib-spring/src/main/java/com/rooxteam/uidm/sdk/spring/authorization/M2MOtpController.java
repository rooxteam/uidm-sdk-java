package com.rooxteam.uidm.sdk.spring.authorization;

import com.rooxteam.errors.auth.exception.ErrorTranslator;
import com.rooxteam.sso.aal.Principal;
import com.rooxteam.sso.aal.client.EvaluationContext;
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
public class M2MOtpController extends BaseController {

    public M2MOtpController(M2MOtpService otpService, ErrorTranslator errorTranslator, RealmDetermineService realmDetermineService) {
        super(otpService, errorTranslator, realmDetermineService);
    }

    @RequestMapping(method = POST, value = "/otp/send")
    public Response sendOtp(@RequestBody EvaluationContext ctx,
                            @RequestParam(required = false) String msisdn,
                            @RequestParam(required = false) String category,
                            @RequestParam(required = false) String service,
                            AuthenticationState authentication) {
        return sendOtp(ctx, msisdn, category, service, realmDetermineService.determine(), authentication);
    }
    @RequestMapping(method = POST, value = "/realms/{realm}/otp/send")
    public Response sendOtp(@RequestBody EvaluationContext ctx,
                            @RequestParam(required = false) String msisdn,
                            @RequestParam(required = false) String category,
                            @RequestParam(required = false) String service,
                            @PathVariable String realm,
                            AuthenticationState authentication) {
        Principal caller = authentication != null ? (Principal) authentication.getAttributes().get("aalPrincipal") : null;
        String jwtToken = caller != null ? caller.getJwtToken() : null;
        validateRealm(realm, authentication);
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

    @RequestMapping(method = POST, value = "/otp/resend")
    public Response resendOtp(@RequestBody OtpFlowStateImpl state,
                              @RequestParam(required = false) String service,
                              AuthenticationState authentication) {
        return super.resendOtp(state, service, realmDetermineService.determine(), authentication);
    }

    @Override
    @RequestMapping(method = POST, value = "/realms/{realm}/otp/resend")
    public Response resendOtp(@RequestBody OtpFlowStateImpl state,
                              @RequestParam(required = false) String service,
                              @PathVariable String realm,
                              AuthenticationState authentication) {
        return super.resendOtp(state, service, realm, authentication);
    }

    @RequestMapping(method = POST, value = "/otp/validate")
    public ResponseEntity<?> validateOtp(@RequestBody final OtpFlowStateImpl state,
                                         @RequestParam(required = false) final String otp,
                                         @RequestParam(required = false) final String otpCode,
                                         @RequestParam(required = false) final String service,
                                         AuthenticationState authentication) {
        return super.validateOtp(state, otp, otpCode, service, realmDetermineService.determine(), authentication);
    }

    @RequestMapping(method = POST, value = "/realms/{realm}/otp/validate")
    public ResponseEntity<?> validateOtp(@RequestBody final OtpFlowStateImpl state,
                                         @RequestParam(required = false) final String otp,
                                         @RequestParam(required = false) final String otpCode,
                                         @RequestParam(required = false) final String service,
                                         @PathVariable String realm,
                                         AuthenticationState authentication) {
        return super.validateOtp(state, otp, otpCode, service, realm, authentication);
    }

    @RequestMapping(method = POST, value = "/otp/check")
    public Response check(@RequestBody OtpFlowStateImpl state,
                          @RequestParam(required = false) String service,
                          AuthenticationState authentication) {
        return super.check(state, service, realmDetermineService.determine(), authentication);
    }

    @Override
    @RequestMapping(method = POST, value = "/realms/{realm}/otp/check")
    public Response check(@RequestBody OtpFlowStateImpl state,
                          @RequestParam(required = false) String service,
                          @PathVariable String realm,
                          AuthenticationState authentication) {
        return super.check(state, service, realm, authentication);
    }
}
