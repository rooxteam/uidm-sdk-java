package com.rooxteam.sso.aal.otp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OtpFlowStateImpl implements OtpFlowState {
    private String execution;

    private String csrf;

    private String serverUrl;

    private String sessionId;

    @Override
    public String toString() {
        return "OtpFlowStateImpl{" +
                "execution='" + execution + '\'' +
                ", csrf='" + csrf + '\'' +
                ", serverUrl='" + serverUrl + '\'' +
                ", sessionId='" + sessionId + '\'' +
                '}';
    }
}
