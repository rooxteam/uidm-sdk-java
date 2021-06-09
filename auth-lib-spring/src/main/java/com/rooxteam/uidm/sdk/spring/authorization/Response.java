package com.rooxteam.uidm.sdk.spring.authorization;

import com.rooxteam.sso.aal.otp.OtpFlowState;
import com.rooxteam.sso.aal.otp.OtpStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor
public class Response {

    private final OtpStatus status;
    private final OtpFlowState otpFlowState;
    private final Set<String> requiredFieldNames;
    private final Integer availableAttempts;
    private final String token;
    private final Long blockedFor;
    private Long nextOtpCodeOperationPeriod;
    private Long otpCodeNumber;
    private final String method;
    private Map<String, Object> extendedAttributes;

    /**
     * @deprecated use {@link Response#Response(OtpStatus, OtpFlowState, Set, Integer, String, Long, Long, Long, String, Map)} instead
     */
    @Deprecated
    public Response(OtpStatus status, OtpFlowState otpFlowState, Set<String> requiredFieldNames, Integer availableAttempts, String token, Long blockedFor, Long nextOtpCodeOperationPeriod, Long otpCodeNumber) {
        this.status = status;
        this.otpFlowState = otpFlowState;
        this.requiredFieldNames = requiredFieldNames;
        this.availableAttempts = availableAttempts;
        this.token = token;
        this.blockedFor = blockedFor;
        this.nextOtpCodeOperationPeriod = nextOtpCodeOperationPeriod;
        this.otpCodeNumber = otpCodeNumber;
        this.method = "SMS";
    }
}
