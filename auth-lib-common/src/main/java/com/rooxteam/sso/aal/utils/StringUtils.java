package com.rooxteam.sso.aal.utils;

/**
 * We are not going to depend on whole commons-lang for this utils
 */
public final class StringUtils {

    private StringUtils() {
    }

    public static boolean isEmpty(String string) {
        return string == null || string.length() == 0;
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }
}
