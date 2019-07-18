package com.rooxteam.uidm.sdk.servlet.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class ParseStringUtilTest {

    @Test
    public void test_parse_token_from_header() {
        String token1 = "JDjkdoiej83ijkfd893K";
        String header1 = "Bearer " + token1;
        Assert.assertEquals(token1, ParseStringUtils.parseAuthorizationHeader(header1).get());

        String token2 = "ujdash394i90900jk3jk3333";
        String header2 = "Bearer sso_1.2_" + token2;
        Assert.assertEquals(token2, ParseStringUtils.parseAuthorizationHeader(header2).get());

        String token3 = "kuuukukujkfsdk";
        String header3 = "Bearer" + token3;
        Assert.assertFalse(ParseStringUtils.parseAuthorizationHeader(header3).isPresent());

        String token4 = "sfdfss";
        String header4 = "Bearerr " + token4;
        Assert.assertFalse(ParseStringUtils.parseAuthorizationHeader(header4).isPresent());
    }

    @Test
    public void test_parse_list() {
        String str = "a1, 22a, 333 ";
        List<String> list = ParseStringUtils.parseConfigValueAsList(str);
        Assert.assertEquals("a1", list.get(0));
        Assert.assertEquals("22a", list.get(1));
        Assert.assertEquals("333", list.get(2));

        String str2 = "_---2";
        List<String> list2 = ParseStringUtils.parseConfigValueAsList(str2);
        Assert.assertEquals(str2, list2.get(0));
    }

    @Test
    public void test_parse_map() {
        String str = "asd.2 = lol, com.roo = 2 , 22=a";
        Map<String, String> map = ParseStringUtils.parseConfigValueAsMap(str);
        Assert.assertEquals("lol", map.get("asd.2"));
        Assert.assertEquals("2", map.get("com.roo"));
        Assert.assertEquals("a", map.get("22"));
        Assert.assertNull(map.get(" 22"));
    }
}
