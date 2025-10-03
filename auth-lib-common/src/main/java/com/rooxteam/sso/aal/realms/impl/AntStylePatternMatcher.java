package com.rooxteam.sso.aal.realms.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sergey.syroezhkin
 * @since 13.08.2025
 */
public final class AntStylePatternMatcher {

    private AntStylePatternMatcher() {
    }

    public static Map<String, String> matchUrlToPattern(String pattern, String url) {
        List<String> patternSegments = tokenize(pattern);
        List<String> urlSegments = tokenize(url);

        Map<String, String> params = new HashMap<>();
        int pIndex = 0;
        int uIndex = 0;

        while (pIndex < patternSegments.size() && uIndex < urlSegments.size()) {
            String pSeg = patternSegments.get(pIndex);
            String uSeg = urlSegments.get(uIndex);

            if ("**".equals(pSeg)) {
                // Если ** в конце шаблона — игнорим все оставшиеся сегменты из урла
                if (pIndex == patternSegments.size() - 1) {
                    return params;
                }
                // Если после ** есть сегменты — ищем совпадение для следующего
                pIndex++;
                String nextPatternSeg = patternSegments.get(pIndex);

                while (uIndex < urlSegments.size()) {
                    if (segmentMatches(nextPatternSeg, urlSegments.get(uIndex), params)) {
                        break;
                    }
                    uIndex++;
                }

                if (uIndex == urlSegments.size()) {
                    // Не нашли подходящий сегмент после ** - урл не матчится с шаблоном
                    return null;
                }
            } else if (!segmentMatches(pSeg, uSeg, params)) {
                return null;
            } else {
                pIndex++;
                uIndex++;
            }
        }

        // Обработка оставшихся ** в шаблоне
        while (pIndex < patternSegments.size() && "**".equals(patternSegments.get(pIndex))) {
            pIndex++;
        }

        // Успешный успех, если оба списка сегментов пройдены до конца
        if (pIndex == patternSegments.size() && uIndex == urlSegments.size()) {
            return params;
        }

        return null;
    }

    private static boolean segmentMatches(String patternSegment, String urlSegment, Map<String, String> params) {
        if (patternSegment.startsWith("{") && patternSegment.endsWith("}")) {
            String paramName = patternSegment.substring(1, patternSegment.length() - 1);
            params.put(paramName, urlSegment);
            return true;
        }
        if ("*".equals(patternSegment)) {
            return true;
        }
        return patternSegment.equals(urlSegment);
    }

    private static List<String> tokenize(String path) {
        String[] split = path.split("/");
        List<String> segments = new ArrayList<>();
        for (String s : split) {
            if (!s.isEmpty()) {
                segments.add(s);
            }
        }
        return segments;
    }


}
