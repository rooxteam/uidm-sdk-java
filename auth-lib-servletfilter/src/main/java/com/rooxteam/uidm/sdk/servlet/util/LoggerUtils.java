package com.rooxteam.uidm.sdk.servlet.util;

public class LoggerUtils {
    public static String trimAccessTokenForLogging(String token) {
        if (token.length() > 16) {
            return token.substring(0, 16);
        } else {
            return token;
        }
    }
}
