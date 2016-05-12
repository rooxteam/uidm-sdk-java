package com.rooxteam.sso.aal.client.model;

import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthenticationResponse {

    @JsonProperty("PolicyContext")
    private String policyContext;

    @JsonProperty("JWTToken")
    private String publicToken;
}
