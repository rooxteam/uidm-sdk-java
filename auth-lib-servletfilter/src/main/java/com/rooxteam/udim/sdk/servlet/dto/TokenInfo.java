package com.rooxteam.udim.sdk.servlet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class TokenInfo {

    @JsonProperty("prn")
    private String principal;

    private List<String> roles;
    private List<String> scopes;

    @JsonProperty("expires_in")
    private Long expiresIn;

    @JsonProperty("auth_level")
    private String authLevel;
}
