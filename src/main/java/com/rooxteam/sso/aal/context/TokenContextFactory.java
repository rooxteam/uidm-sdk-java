package com.rooxteam.sso.aal.context;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rooxteam.sso.aal.Principal;
import com.rooxteam.sso.aal.client.model.AuthenticationResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Ivan Volynkin
 *         ivolynkin@roox.ru
 */
abstract public class TokenContextFactory<A extends AuthenticationResponse, P extends Principal> {
    public enum TYPE {Bearer, JWTToken}

    private static final Map<TYPE, TokenContextFactory> factories = new HashMap<TYPE, TokenContextFactory>() {{
        put(TYPE.Bearer, new BearerTokenContextFactory());
        put(TYPE.JWTToken, new JwtTokenContextFactory());
    }};

    abstract protected Class<A> getAuthenticationResponseClass();

    abstract public P createPrincipal(A authenticationResponse);

    public P createPrincipal(ObjectMapper objectMapper, JsonNode jsonNode) {
        return createPrincipal(fromJsonNode(objectMapper, jsonNode));
    }

    private A fromJsonNode(ObjectMapper objectMapper, JsonNode jsonNode) {
        return objectMapper.convertValue(jsonNode, getAuthenticationResponseClass());
    }

    public static TokenContextFactory get(TYPE type) {
        return factories.get(type);
    }

    public static TokenContextFactory get(String type) {
        return factories.get(TYPE.valueOf(type));
    }
}
