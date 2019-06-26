package com.rooxteam.uidm.sdk.servlet.service;

import com.rooxteam.udim.sdk.servlet.service.ValidateTokenServiceImpl;
import org.junit.Assert;
import org.junit.Test;

public class ValidateTokenServiceTest {

    @Test
    public void test_validate_token_service_impl() {
        ValidateTokenServiceImpl validateTokeService = new ValidateTokenServiceImpl();

        String token1 = "JDjkdoiej83ijkfd893K";
        String header1 = "Bearer " + token1;
        Assert.assertEquals(token1, validateTokeService.extractAccessToken(header1));

        String token2 = "ujdash394i90900jk3jk3333";
        String header2 = "Bearer sso_1.2_" + token2;
        Assert.assertEquals(token2, validateTokeService.extractAccessToken(header2));

        String token3 = "kuuukukujkfsdk";
        String header3 = "Bearer" + token3;
        Assert.assertNull(validateTokeService.extractAccessToken(header3));

        String token4 = "sfdfss";
        String header4 = "Bearerr " + token4;
        Assert.assertNull(validateTokeService.extractAccessToken(header4));
    }
}
