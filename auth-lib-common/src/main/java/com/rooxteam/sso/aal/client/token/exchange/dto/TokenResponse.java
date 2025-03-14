package com.rooxteam.sso.aal.client.token.exchange.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class TokenResponse {

    /**
     * REQUIRED. The security token issued by the authorization server
     * in response to the token exchange request
     */
    @JsonProperty("access_token")
    private String accessToken;

    /**
     * REQUIRED. An identifier for the representation of the issued security token.
     */
    @JsonProperty("issued_token_type")
    private Integer issuedTokenType;

    /**
     * REQUIRED. A case-insensitive value specifying the method of using the access token issued
     */
    @JsonProperty("token_type")
    private String tokenType;

    /**
     * RECOMMENDED. The validity lifetime, in seconds,
     * of the token issued by the authorization server.
     */
    @JsonProperty("expires_in")
    private Integer expiresIn;

    /**
     * OPTIONAL. If the scope of the issued security token is identical
     * to the scope requested by the client; otherwise, it is REQUIRED.
     */
    @JsonProperty("scope")
    private List<String> scope;

    /**
     * OPTIONAL. A refresh token will typically not be issued when the exchange is of one temporary credential
     * (the subject_token) for a different temporary credential (the issued token) for use in some other context.
     */
    @JsonProperty("refresh_token")
    private String refreshToken;

}
