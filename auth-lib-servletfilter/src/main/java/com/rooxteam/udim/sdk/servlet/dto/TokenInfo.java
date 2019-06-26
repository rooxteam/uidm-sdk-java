package com.rooxteam.udim.sdk.servlet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
public class TokenInfo {

    /**
     * For backward compatibility.
     */
    public void setSub(String sub) {
        if (sub != null) {
            this.principal = sub;
        }
    }

    @JsonProperty("prn")
    private String principal;

    private Set<String> roles;
    private Set<String> scopes;

    @JsonProperty("expires_in")
    private Long expiresIn;

    @JsonProperty("auth_level")
    private Long authLevel;
}
