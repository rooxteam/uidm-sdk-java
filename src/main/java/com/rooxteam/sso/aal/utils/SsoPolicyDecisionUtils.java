package com.rooxteam.sso.aal.utils;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class SsoPolicyDecisionUtils {


    @SuppressWarnings("unchecked")
    protected static Map<String, String> parseAdvices(Map advices) {
        if (advices == null || advices.isEmpty()) {
            return Collections.emptyMap();
        }
        ImmutableMap.Builder<String, String> result = ImmutableMap.builder();
        for (Map.Entry entry : (Set<Map.Entry>) advices.entrySet()) {
            String name = (String) entry.getKey();
            Set values = (Set) entry.getValue();
            result.put(name, (String) Iterables.getFirst(values, null));
        }
        return result.build();
    }
}
