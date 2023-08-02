package com.rooxteam.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MaskingPatternLayoutTest {

    private final MaskingPatternLayout layout = new MaskingPatternLayout();

    @Test
    public void maskMessage() {
        String maskingString = "Authorization: 12345, \"access_token\": \"123456\", Authorization: 789, ....";
        layout.addMaskPattern("Authorization\\s*:\\s*(.*?),");
        layout.addMaskPattern("\"access_token\"\\s*:\\s*\"(.*?)\",");
        assertEquals("Authorization: *****, \"access_token\": \"******\", Authorization: ***, ....", layout.maskMessage(maskingString));
    }
}