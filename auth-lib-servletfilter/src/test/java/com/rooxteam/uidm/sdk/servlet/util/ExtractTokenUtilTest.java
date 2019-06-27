package com.rooxteam.uidm.sdk.servlet.util;

import com.rooxteam.udim.sdk.servlet.util.ExtractAccessTokenUtils;
import org.junit.Assert;
import org.junit.Test;

public class ExtractTokenUtilTest {

    @Test
    public void test_extract_toke_from_header() {
        String token1 = "JDjkdoiej83ijkfd893K";
        String header1 = "Bearer " + token1;
        Assert.assertEquals(token1, ExtractAccessTokenUtils.extractFromHeader(header1).get());

        String token2 = "ujdash394i90900jk3jk3333";
        String header2 = "Bearer sso_1.2_" + token2;
        Assert.assertEquals(token2, ExtractAccessTokenUtils.extractFromHeader(header2).get());

        String token3 = "kuuukukujkfsdk";
        String header3 = "Bearer" + token3;
        Assert.assertFalse(ExtractAccessTokenUtils.extractFromHeader(header3).isPresent());

        String token4 = "sfdfss";
        String header4 = "Bearerr " + token4;
        Assert.assertFalse(ExtractAccessTokenUtils.extractFromHeader(header4).isPresent());
    }
}
