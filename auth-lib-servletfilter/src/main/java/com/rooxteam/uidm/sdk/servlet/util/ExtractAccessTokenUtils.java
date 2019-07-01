package com.rooxteam.uidm.sdk.servlet.util;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtractAccessTokenUtils {
    private final static Pattern headerTokenValidationPattern = Pattern.compile("Bearer (([a-zA-Z]+)_([.\\d]+)_)?(.+)");
    public static Optional<String> extractFromHeader(String headerValue) {
        Matcher matcher = headerTokenValidationPattern.matcher(headerValue);
        if (matcher.matches()) {
            return Optional.of(matcher.group(4));
        }
        return Optional.empty();
    }
}
