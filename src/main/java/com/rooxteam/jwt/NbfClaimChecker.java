package com.rooxteam.jwt;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;

import java.security.SignatureException;
import java.text.ParseException;
import java.util.Date;

import static java.text.MessageFormat.format;

/**
 * Проверка параметра nbf.
 * Если nbf равен null или до текущего времени, то проверка пройдена; в остальных случаях {@link java.lang.IllegalStateException}
 */
public class NbfClaimChecker implements Checker {

    @Override
    public void check(JWT jwt) throws SignatureException, ParseException {
        JWTClaimsSet jwtClaimsSet = jwt.getJWTClaimsSet();
        Date notBeforeTime = jwtClaimsSet.getNotBeforeTime();
        if (notBeforeTime != null) {
            Date now = new Date();
            if (now.before(notBeforeTime)) {
                throw new IllegalStateException(format("Invalid ''nbf'' claim {0}, now: {1}", notBeforeTime, now));
            }
        }
    }
}
