package com.rooxteam.jwt;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;

import java.security.SignatureException;
import java.text.ParseException;
import java.util.Date;

import static java.text.MessageFormat.format;

/**
 * Проверка параметра iat.
 * Если iat равен null или до текущего времени, то проверка пройдена; в остальных случаях  {@link java.lang.IllegalStateException}
 */
public class IatClaimChecker implements Checker {

    @Override
    public void check(JWT jwt) throws SignatureException, ParseException {
        JWTClaimsSet jwtClaimsSet = jwt.getJWTClaimsSet();
        Date issueTime = jwtClaimsSet.getIssueTime();
        if (issueTime != null) {
            Date now = new Date();
            if (issueTime.after(now)) {
                throw new IllegalStateException(format("Invalid ''iat'' claim {0}, now: {1}", issueTime, now));
            }
        }
    }
}
