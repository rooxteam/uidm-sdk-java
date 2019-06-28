package com.rooxteam.uidm.sdk.servlet;

import com.rooxteam.udim.sdk.servlet.configuration.ServletFilterConfiguration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServletFilterConfigurationForTesting implements ServletFilterConfiguration {
    private Map<String, Object> map = new TreeMap<>();
    private Pattern pattern = Pattern.compile("([a-zA-Z\\._]+)=(.+)");

    private Object parse(String str) {
        int i = str.indexOf(",");
        if (i < 0) {
            return str;
        } else {
            ArrayList<String> arr = new ArrayList<>();
            int prev = 0;
            while (prev < str.length()) {
                arr.add(str.substring(prev, i));
                prev = i + 1;
                i = str.indexOf(",", i+1) > 0 ? str.indexOf(",", i+1) : str.length();
            }
            return arr;
        }
    }

    public ServletFilterConfigurationForTesting() {
        InputStream inputStream = getClass()
                .getClassLoader().getResourceAsStream("app.properties");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader
                (inputStream, Charset.forName(StandardCharsets.UTF_8.name())))) {
            reader.lines().forEach( (str) -> {
                Matcher matcher = pattern.matcher(str);
                if (matcher.matches()) {
                    map.put(matcher.group(1), parse(matcher.group(2)));
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Set<String> getAuthorizationCookieNames() {
        Set<String> set = new TreeSet<>();
        set.add("at");
        return set;
    }

    @Override
    public String getString(String property) {
        return (String) map.get(property);
    }

    @Override
    public List<String> getList(String property) {
        return (List<String>) map.get(property);
    }
}
