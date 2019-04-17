package com.rooxteam.sso.aal.context;

import com.rooxteam.sso.aal.PrincipalImpl;
import com.rooxteam.sso.aal.client.model.BearerAuthenticationResponse;

/**
 * @author Ivan Volynkin
 *         ivolynkin@roox.ru
 */
public class BearerTokenContextFactory extends TokenContextFactory<BearerAuthenticationResponse, PrincipalImpl> {
    @Override
    public Class<BearerAuthenticationResponse> getAuthenticationResponseClass() {
        return BearerAuthenticationResponse.class;
    }

    @Override
    public PrincipalImpl createPrincipal(BearerAuthenticationResponse authenticationResponse) {
        return new PrincipalImpl(authenticationResponse.getPolicyContext(), authenticationResponse.getPublicToken());
    }
}
