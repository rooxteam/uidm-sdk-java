package com.rooxteam.uidm.sdk.servlet.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    private final static String group = "[a-zA-Z_.\\-0-9]*";
    private final static Pattern dictionary = Pattern.compile("\\s*("+group+")\\s*=\\s*("+group+")\\s*,?");
    public static Map<String, String> parseConfigValueAsMap(String str) {
        TreeMap<String, String> map = new TreeMap<>();
        Matcher matcher = dictionary.matcher(str);
        while (matcher.find()) {
            String key = matcher.group(1);
            String value = matcher.group(2);
            map.put(key, value);
        }
        return map;
    }

    private final static Pattern listPattern = Pattern.compile("\\s*("+group+")\\s*,?");
    public static List<String> parseConfigValueAsList(String str) {
        ArrayList<String> list = new ArrayList<>();
        Matcher matcher = listPattern.matcher(str);
        while (matcher.find()) {
            String value = matcher.group(1);
            list.add(value);
        }
        return list;
    }

    private final static Pattern headerTokenValidationPattern = Pattern.compile("Bearer (([a-zA-Z]+)_([.\\d]+)_)?(.+)");
    public static Optional<String> parseAuthorizationHeader(String headerValue) {
        Matcher matcher = headerTokenValidationPattern.matcher(headerValue);
        if (matcher.matches()) {
            return  Optional.of(matcher.group(4));
        }
        return Optional.empty();
    }
}
