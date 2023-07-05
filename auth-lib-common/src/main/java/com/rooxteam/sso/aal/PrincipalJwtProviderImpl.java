package com.rooxteam.sso.aal;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.rooxteam.sso.aal.validation.impl.JwtTokenValidator;
import com.rooxteam.sso.aal.validation.jwt.ValidationResult;
import lombok.AllArgsConstructor;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.Calendar;

import static com.rooxteam.sso.aal.AalLogger.LOG;

@AllArgsConstructor
public class PrincipalJwtProviderImpl implements PrincipalProvider {

    private JwtTokenValidator jwtTokenValidator;

    @Override
    public Principal getPrincipal(HttpServletRequest request, String jwtToken) {
        ValidationResult validationResult = jwtTokenValidator.validate(jwtToken);
        try {
            if (!validationResult.isSuccess()) {
                return null;
            }
            JWT jwt = JWTParser.parse(jwtToken);
            JWTClaimsSet claimsSet = jwt.getJWTClaimsSet();
            LOG.debugv("Claims: {}", claimsSet);

            Calendar exp = Calendar.getInstance();
            exp.setTime(claimsSet.getExpirationTime());
            return new PrincipalImpl(jwtToken, claimsSet.getClaims(), exp);
        } catch (ParseException e) {
            LOG.warn("JWT has an invalid structure", e);
            return null;
        }
    }
}
