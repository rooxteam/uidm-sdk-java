
package com.rooxteam.sso.aal.client;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;

import java.text.ParseException;
import java.util.Calendar;

/**
 * Token parsing routines.
 */
public class TokenHelper {

    public static Calendar expires(JWT jwt) throws ParseException {
        Calendar expiration = Calendar.getInstance();
        expiration.setTime(jwt.getJWTClaimsSet().getExpirationTime());
        return expiration;
    }
    
    public static Calendar expires(String jwtStr) throws ParseException {
        JWT jwt = parseJwt(jwtStr);
        return expires(jwt);
    }

    public static String getId(String jwtStr) throws ParseException {
        JWT jwt = parseJwt(jwtStr);
        return jwt.getJWTClaimsSet().getJWTID();
    }

    private static JWT parseJwt(String jwt) throws ParseException {
        return JWTParser.parse(jwt);
    }
}
