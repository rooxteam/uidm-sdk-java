package com.rooxteam.sso.aal.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SsoPolicyDecisionUtils {


    @SuppressWarnings("unchecked")
    protected static Map<String, String> parseAdvices(Map advices) {
        if (advices == null || advices.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, String> result = new HashMap<>();
        for (Map.Entry entry : (Set<Map.Entry>) advices.entrySet()) {
            String name = (String) entry.getKey();
            Set values = (Set) entry.getValue();
            if (values.size() > 0) {
                result.put(name, (String) values.iterator().next());
            }
        }
        return Collections.unmodifiableMap(result);
    }
}
