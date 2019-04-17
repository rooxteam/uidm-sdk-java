package com.rooxteam.sso.aal.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class View {
    private int otpCodeAvailableAttempts;
    private String msisdn;
    private Long blockedFor;
    private Long otpCodeNumber;
    private String method;
}
