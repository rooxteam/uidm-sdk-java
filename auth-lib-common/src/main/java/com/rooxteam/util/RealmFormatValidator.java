package com.rooxteam.util;

import com.rooxteam.sso.aal.utils.StringUtils;

import java.util.regex.Pattern;

public final class RealmFormatValidator {
    public static final Pattern REALM_VALIDATION_PATTERN
            = Pattern.compile("^[a-zA-Z][a-zA-Z0-9_-]{0,63}$");

    public static boolean isRealmFormatValid(String realm) {
        return StringUtils.isNotBlank(realm) && REALM_VALIDATION_PATTERN.matcher(realm).matches();
    }
}
