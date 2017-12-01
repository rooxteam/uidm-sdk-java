package com.rooxteam.sso.aal.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BearerAuthenticationResponse extends AuthenticationResponse<String> {

    @JsonProperty("access_token")
    @Override
    public void setPublicToken(String publicToken) {
        super.setPublicToken(publicToken);
    }
}
