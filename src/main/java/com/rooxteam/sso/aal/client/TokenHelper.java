
package com.rooxteam.sso.aal.client;

import org.forgerock.json.jose.common.JwtReconstruction;
import org.forgerock.json.jose.jws.SignedJwt;

import java.util.Calendar;

/**
 * Token parsing routines.
 */
public class TokenHelper {

    public static Calendar expires(SignedJwt jwt) {
        Calendar expiration = Calendar.getInstance();
        expiration.setTime(jwt.getClaimsSet().getExpirationTime());
        return expiration;
    }
    
    public static Calendar expires(String jwtStr) {
        SignedJwt jwt = parseJwt(jwtStr);
        return expires(jwt);
    }

    public static String getId(String jwtStr) {
        SignedJwt jwt = parseJwt(jwtStr);
        return jwt.getClaimsSet().getJwtId();
    }

    private static SignedJwt parseJwt(String jwt) {
        return new JwtReconstruction().reconstructJwt(jwt, SignedJwt.class);
    }
}
