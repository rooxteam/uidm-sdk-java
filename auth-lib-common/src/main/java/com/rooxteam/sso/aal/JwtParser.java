package com.rooxteam.sso.aal;

import com.nimbusds.jwt.SignedJWT;

import java.text.ParseException;

class JwtParser {

    SignedJWT parse(String jwt) throws ParseException {
        return SignedJWT.parse(jwt);
    }
}
