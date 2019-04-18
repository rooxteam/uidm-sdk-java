package com.rooxteam.jwt;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.rooxteam.sso.aal.utils.StringUtils;
import lombok.*;

import java.security.SignatureException;
import java.text.ParseException;


/**
 * Проверяет наличие {@code claimName} в JWT и сверяет его с {@code expectedValue}
 */
@RequiredArgsConstructor
@AllArgsConstructor
public class StringClaimChecker implements Checker {

    @NonNull
    @Getter
    @Setter
    private String claimName;

    @NonNull
    @Getter
    @Setter
    private String expectedValue;

    @Getter
    @Setter
    private boolean isMandatory;

    @Override
    public void check(JWT jwt) throws SignatureException, ParseException {
        JWTClaimsSet jwtClaimsSet = jwt.getJWTClaimsSet();
        String claim = jwtClaimsSet.getStringClaim(claimName);
        if (isMandatory && StringUtils.isEmpty(claim)) {
            throw new IllegalStateException(String.format("Mandatory claim %s not found", claimName));
        }

        if (claim != null && !expectedValue.equals(claim)) {
            throw new IllegalStateException(String.format("Expected claim %s, but found %s", expectedValue, claim));
        }

    }

}
