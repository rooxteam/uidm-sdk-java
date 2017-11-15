package com.rooxteam.sso.aal.context;

import com.rooxteam.sso.aal.PlainPrincipal;
import com.rooxteam.sso.aal.client.model.BearerAuthenticationResponse;

/**
 * @author Ivan Volynkin
 *         ivolynkin@roox.ru
 */
public class BearerTokenContextFactory extends TokenContextFactory<BearerAuthenticationResponse, PlainPrincipal> {
    @Override
    public Class<BearerAuthenticationResponse> getAuthenticationResponseClass() {
        return BearerAuthenticationResponse.class;
    }

    @Override
    public PlainPrincipal createPrincipal(BearerAuthenticationResponse authenticationResponse) {
        return new PlainPrincipal(authenticationResponse.getPolicyContext(), authenticationResponse.getPublicToken());
    }
}
