package com.rooxteam.sso.aal.client.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenExchangeRequest {

    /**
     * REQUIRED
     * A security token that represents the identity of the party on behalf of whom the request is being made
     */
    private String subjectToken;

    /**
     * REQUIRED
     * An identifier, that indicates the type of the security token in the subject_token parameter.
     */
    private String subjectTokenType;

    /**
     * OPTIONAL
     * A URI that indicates the target service or resource where the client intends to use the requested security token.
     */
    private String resource;

    /**
     * OPTIONAL
     * A public identifier for apps
     */
    private String clientId;

    /**
     * OPTIONAL
     * An identifier that indicated realm.
     */
    private String realm;

    /**
     * OPTIONAL
     * A client secret for apps
     */
    private String clientSecret;

    /**
     * OPTIONAL
     * An identifier for the type of the requested security token.
     */
    private String requestedTokenType;

    /**
     * OPTIONAL
     * The logical name of the target service where the client intends to use the requested security token.
     */
    private String audience;

    /**
     * OPTIONAL
     * A list, that allow the client to specify the desired scope of the requested security token in the context of the service or
     * resource where the token will be used
     */
    private String scope;

    /**
     * OPTIONAL
     * A list of roles to replaced with
     */
    private String roles;
}
