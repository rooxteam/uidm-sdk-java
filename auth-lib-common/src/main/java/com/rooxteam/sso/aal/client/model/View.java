package com.rooxteam.sso.aal.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class View {
    private int otpCodeAvailableAttempts;
    private String msisdn;
    private Long blockedFor;
    private Long otpCodeNumber;
    private String method;
    private Map<String, Object> extendedAttributes;
}
