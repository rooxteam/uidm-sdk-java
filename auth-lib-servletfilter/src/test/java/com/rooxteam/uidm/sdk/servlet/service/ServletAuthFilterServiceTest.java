package com.rooxteam.uidm.sdk.servlet.service;

import com.rooxteam.sso.aal.client.CommonSsoAuthorizationClient;
import com.rooxteam.udim.sdk.servlet.configuration.ServletFilterServiceConfiguration;
import com.rooxteam.udim.sdk.servlet.service.ServletFilterServiceImpl;
import javax.servlet.http.Cookie;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Collections;

import static org.mockito.Mockito.when;

public class ServletAuthFilterServiceTest {

    private  ServletFilterServiceImpl validateTokeService;

    @Before
    public void prepare() {
        CommonSsoAuthorizationClient commonSsoAuthorizationClient = Mockito.mock(CommonSsoAuthorizationClient.class);
        ServletFilterServiceConfiguration configuration = Mockito.mock(ServletFilterServiceConfiguration.class);
        when(configuration.getAuthorizationCookieNames()).thenReturn(Collections.singleton("at"));

        validateTokeService = new ServletFilterServiceImpl(commonSsoAuthorizationClient, configuration);
    }

    @Test
    public void test_extract_token_from_header_service_impl() {

        Cookie[] cookiesNoToken = new Cookie[2];
        cookiesNoToken[0] = new Cookie("test", "test");
        cookiesNoToken[1] = new Cookie("test2", "test2");

        String token1 = "JDjkdoiej83ijkfd893K";
        String header1 = "Bearer " + token1;
        Assert.assertEquals(token1, validateTokeService.extractAccessToken(cookiesNoToken, header1).get());

        String token2 = "ujdash394i90900jk3jk3333";
        String header2 = "Bearer sso_1.2_" + token2;
        Assert.assertEquals(token2, validateTokeService.extractAccessToken(cookiesNoToken, header2).get());

        String token3 = "kuuukukujkfsdk";
        String header3 = "Bearer" + token3;
        Assert.assertFalse(validateTokeService.extractAccessToken(cookiesNoToken, header3).isPresent());

        String token4 = "sfdfss";
        String header4 = "Bearerr " + token4;
        Assert.assertFalse(validateTokeService.extractAccessToken(cookiesNoToken, header4).isPresent());
    }

    @Test
    public void test_extract_token_from_cookies_service_impl() {
        String token = "JDjkdofdyu43u";
        Cookie[] cookies = new Cookie[2];
        cookies[0] = new Cookie("at", token);
        cookies[1] = new Cookie("test2", "test2");

        String header1 = "Bearer " + token + "xxxxxxx";
        Assert.assertEquals(token, validateTokeService.extractAccessToken(cookies, header1).get());

        String header2 = "Bearer sso_1.2_" + token + "xxxxxxx";
        Assert.assertEquals(token, validateTokeService.extractAccessToken(cookies, header2).get());

        String header3 = "Bearer" + token + "xxxxxxx";
        Assert.assertEquals(token, validateTokeService.extractAccessToken(cookies, header3).get());
    }

    @Test
    public void test_extract_token_from_header_no_cookie_service_impl() {
        String token = "sdf324rwfs43u";
        Cookie[] cookies1 = new Cookie[2];
        cookies1[0] = new Cookie("at", "");
        cookies1[1] = new Cookie("test2", "test2");

        Cookie[] cookies2 = new Cookie[2];
        cookies2[0] = new Cookie("at", null);
        cookies2[1] = new Cookie("test2", "test2");

        String header1 = "Bearer " + token;
        Assert.assertEquals(token, validateTokeService.extractAccessToken(cookies1, header1).get());

        String header2 = "Bearer sso_1.2_" + token;
        Assert.assertEquals(token, validateTokeService.extractAccessToken(cookies1, header2).get());

        String header3 = "Bearer " + token;
        Assert.assertEquals(token, validateTokeService.extractAccessToken(cookies2, header3).get());

        String header4 = "Bearer sso_1.2_" + token;
        Assert.assertEquals(token, validateTokeService.extractAccessToken(cookies2, header4).get());
    }
}
