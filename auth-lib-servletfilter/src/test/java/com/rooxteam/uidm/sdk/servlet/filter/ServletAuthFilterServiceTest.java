package com.rooxteam.uidm.sdk.servlet.filter;

import com.rooxteam.sso.aal.AuthenticationAuthorizationLibrary;
import com.rooxteam.sso.aal.Principal;
import com.rooxteam.sso.aal.PrincipalImpl;
import com.rooxteam.uidm.sdk.servlet.configuration.ServletFilterConfigurationForTesting;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.http.Cookie;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.mockito.Matchers.anyMap;
import static org.mockito.Mockito.when;

public class ServletAuthFilterServiceTest {

    public ServletFilterHelper prepare(String principalId, String token, List<String> scopes, List<String> roles) {
        Map<String, Object> sharedIdentityProperties = new TreeMap<>();
        sharedIdentityProperties.put("scopes", scopes);
        sharedIdentityProperties.put("roles", roles);
        sharedIdentityProperties.put("prn", principalId);
        Calendar expirationTime = new GregorianCalendar();
        Principal principal = new PrincipalImpl(token, sharedIdentityProperties, expirationTime);

        AuthenticationAuthorizationLibrary aal = Mockito.mock(AuthenticationAuthorizationLibrary.class);
        when(aal.authenticate(anyMap())).thenReturn(principal);

        return new ServletFilterHelper(new ServletFilterConfigurationForTesting(), aal);
    }

    @Test
    public void test_extract_token_from_header_service_impl() {
        ServletFilterHelper helper = prepare("222", "d2112",  Arrays.asList("scope1", "scope2"), Arrays.asList("role1", "role2") );

        Cookie[] cookiesNoToken = new Cookie[2];
        cookiesNoToken[0] = new Cookie("test", "test");
        cookiesNoToken[1] = new Cookie("test2", "test2");

        String token1 = "JDjkdoiej83ijkfd893K";
        String header1 = "Bearer " + token1;
        Assert.assertEquals(token1, helper.extractAccessToken(cookiesNoToken, header1).get());

        String token2 = "ujdash394i90900jk3jk3333";
        String header2 = "Bearer sso_1.2_" + token2;
        Assert.assertEquals(token2, helper.extractAccessToken(cookiesNoToken, header2).get());

        String token3 = "kuuukukujkfsdk";
        String header3 = "Bearer" + token3;
        Assert.assertFalse(helper.extractAccessToken(cookiesNoToken, header3).isPresent());

        String token4 = "sfdfss";
        String header4 = "Bearerr " + token4;
        Assert.assertFalse(helper.extractAccessToken(cookiesNoToken, header4).isPresent());
    }

    @Test
    public void test_extract_token_from_cookies_service_impl() {
        ServletFilterHelper helper = prepare("222", "d2112",  Arrays.asList("scope1", "scope2"), Arrays.asList("role1", "role2") );

        String token = "JDjkdofdyu43u";
        Cookie[] cookies = new Cookie[2];
        cookies[0] = new Cookie("at", token);
        cookies[1] = new Cookie("test2", "test2");

        String header1 = "Bearer " + token + "xxxxxxx";
        Assert.assertEquals(token, helper.extractAccessToken(cookies, header1).get());

        String header2 = "Bearer sso_1.2_" + token + "xxxxxxx";
        Assert.assertEquals(token, helper.extractAccessToken(cookies, header2).get());

        String header3 = "Bearer" + token + "xxxxxxx";
        Assert.assertEquals(token, helper.extractAccessToken(cookies, header3).get());
    }

    @Test
    public void test_extract_token_from_header_no_cookie_service_impl() {
        ServletFilterHelper helper = prepare("222", "d2112",  Arrays.asList("scope1", "scope2"), Arrays.asList("role1", "role2") );

        String token = "sdf324rwfs43u";
        Cookie[] cookies1 = new Cookie[2];
        cookies1[0] = new Cookie("at", "");
        cookies1[1] = new Cookie("test2", "test2");

        Cookie[] cookies2 = new Cookie[2];
        cookies2[0] = new Cookie("at", null);
        cookies2[1] = new Cookie("test2", "test2");

        String header1 = "Bearer " + token;
        Assert.assertEquals(token, helper.extractAccessToken(cookies1, header1).get());

        String header2 = "Bearer sso_1.2_" + token;
        Assert.assertEquals(token, helper.extractAccessToken(cookies1, header2).get());

        String header3 = "Bearer " + token;
        Assert.assertEquals(token, helper.extractAccessToken(cookies2, header3).get());

        String header4 = "Bearer sso_1.2_" + token;
        Assert.assertEquals(token, helper.extractAccessToken(cookies2, header4).get());
    }
}
