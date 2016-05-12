package com.rooxteam.jwt;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import org.junit.Test;

import java.security.SignatureException;
import java.text.ParseException;
import java.util.Calendar;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class IatClaimCheckerTest {

    @Test
    public void should_pass_with_iat_before_now() throws SignatureException, ParseException {
        JWT mockJwt = mock(JWT.class);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .issueTime(calendar.getTime())
                .build();
        when(mockJwt.getJWTClaimsSet())
                .thenReturn(jwtClaimsSet);


        IatClaimChecker checker = new IatClaimChecker();
        checker.check(mockJwt);

        verify(mockJwt, times(1)).getJWTClaimsSet();
    }

    @Test
    public void should_pass_when_iat_is_null() throws SignatureException, ParseException {
        JWT mockJwt = mock(JWT.class);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .build();
        when(mockJwt.getJWTClaimsSet())
                .thenReturn(jwtClaimsSet);


        NbfClaimChecker checker = new NbfClaimChecker();
        checker.check(mockJwt);

        verify(mockJwt, times(1)).getJWTClaimsSet();
    }

    @Test(expected = IllegalStateException.class)
    public void should_fail_with_iat_after_now() throws SignatureException, ParseException {
        JWT mockJwt = mock(JWT.class);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .notBeforeTime(calendar.getTime())
                .build();
        when(mockJwt.getJWTClaimsSet())
                .thenReturn(jwtClaimsSet);


        NbfClaimChecker checker = new NbfClaimChecker();
        try {
            checker.check(mockJwt);
        } catch (IllegalStateException e) {
            assertTrue(e.getMessage().startsWith("Invalid 'nbf' claim "));
            throw e;
        }
    }
}
