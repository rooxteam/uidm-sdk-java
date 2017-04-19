package com.rooxteam.sso.aal.otp;

import com.rooxteam.sso.aal.Principal;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class OtpResponseImpl implements OtpResponse {
    private OtpStatus status;
    private OtpFlowState otpFlowState;
    private Set<String> requiredFieldNames;
    private Integer availableAttempts;
    private Principal principal;
    private Long blockedFor;
    private Exception exception;
    private Long nextOtpCodeOperationPeriod;

    public OtpResponseImpl() {
    }

    public static OtpResponse exception(Exception e) {
        OtpResponseImpl response = new OtpResponseImpl();
        response.status = OtpStatus.EXCEPTION;
        response.exception = e;
        return response;
    }
}
