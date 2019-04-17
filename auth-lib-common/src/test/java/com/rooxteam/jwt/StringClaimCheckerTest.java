package com.rooxteam.jwt;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import org.junit.Test;

import java.security.SignatureException;
import java.text.ParseException;

import static org.mockito.Mockito.*;

public class StringClaimCheckerTest {

    @Test
    public void should_pass_when_claim_is_null_and_is_not_mandatory() throws SignatureException, ParseException {
        JWT mockJwt = mock(JWT.class);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .build();
        when(mockJwt.getJWTClaimsSet())
                .thenReturn(jwtClaimsSet);


        StringClaimChecker checker = new StringClaimChecker("TEST_CLAIM", "empty", false);
        checker.check(mockJwt);

        verify(mockJwt, times(1)).getJWTClaimsSet();
    }

    @Test(expected = IllegalStateException.class)
    public void should_fail_when_claim_is_null_and_claim_is_mandatory() throws SignatureException, ParseException {
        JWT mockJwt = mock(JWT.class);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .build();
        when(mockJwt.getJWTClaimsSet())
                .thenReturn(jwtClaimsSet);


        StringClaimChecker checker = new StringClaimChecker("TEST_CLAIM", "empty", true);
        checker.check(mockJwt);
    }

    @Test
    public void should_pass_with_expected_claim_in_jwt() throws SignatureException, ParseException {
        JWT mockJwt = mock(JWT.class);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .claim("TEST_CLAIM", "TEST_VALIE")
                .build();
        when(mockJwt.getJWTClaimsSet())
                .thenReturn(jwtClaimsSet);


        StringClaimChecker checker = new StringClaimChecker("TEST_CLAIM", "TEST_VALIE");
        checker.check(mockJwt);

        verify(mockJwt, times(1)).getJWTClaimsSet();
    }

}
