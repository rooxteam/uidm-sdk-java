package com.rooxteam.uidm.sdk.servlet;

import org.junit.Assert;
import org.junit.Test;

public class ConfigurationTest {

    @Test
    public void test_config() {
        ServletFilterConfigurationForTesting servletFilterConfigurationForTesting = new ServletFilterConfigurationForTesting();
        Assert.assertEquals("test", servletFilterConfigurationForTesting.getString("testprop"));
    }
}

