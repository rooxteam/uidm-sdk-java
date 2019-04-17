package com.rooxteam.sso.aal.context;

import com.rooxteam.sso.aal.PrincipalImpl;
import com.rooxteam.sso.aal.client.model.JWTAuthenticationResponse;

/**
 * @author Ivan Volynkin
 *         ivolynkin@roox.ru
 *         <p>
 *         We must not use JWTToken field as token
 */
@Deprecated
public class JwtTokenContextFactory extends TokenContextFactory<JWTAuthenticationResponse, PrincipalImpl> {

    @Override
    public Class<JWTAuthenticationResponse> getAuthenticationResponseClass() {
        return JWTAuthenticationResponse.class;
    }

    @Override
    public PrincipalImpl createPrincipal(JWTAuthenticationResponse authenticationResponse) {
        return new PrincipalImpl(authenticationResponse.getPolicyContext(), authenticationResponse.getPublicToken());
    }

}
