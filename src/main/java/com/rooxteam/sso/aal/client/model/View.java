package com.rooxteam.sso.aal.client.model;

import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class View {
    private int otpCodeAvailableAttempts;
    private String msisdn;
    private Long blockedFor;
    private Long otpCodeNumber;
}
