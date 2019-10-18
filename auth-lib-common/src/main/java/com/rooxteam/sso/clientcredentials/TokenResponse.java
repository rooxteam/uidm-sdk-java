package com.rooxteam.sso.clientcredentials;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.util.List;

@Value
class TokenResponse {
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("expires_in")
    private Integer expiresIn;
    @JsonProperty("token_type")
    private String tokenType;
    @JsonProperty("JWTToken")
    private String jwtToken;
    @JsonProperty("scope")
    private List<String> scope;
    @JsonProperty("PolicyContext")
    private String policyContext;
    @JsonProperty("refresh_token")
    private String refreshToken;
}