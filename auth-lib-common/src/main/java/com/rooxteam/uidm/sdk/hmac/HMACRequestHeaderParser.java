package com.rooxteam.uidm.sdk.hmac;

import com.rooxteam.sso.aal.exception.ValidateException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.StringTokenizer;

/**
 * Парсер заголовка с HMAC-подписью.
 * TODO копия содержится в sso-server, вынести в общую библиотеку
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HMACRequestHeaderParser {

    private static final String VERSION_PARAM = "version";
    private static final String ALG_PARAM = "alg";
    private static final String SIGNATURE_PARAM = "signature";
    private static final String TIMESTAMP_PARAM = "timestamp";

    static public HMACSignature parse(String header) {
        Map<String, String> map = new HashMap<>();
        StringTokenizer tokenizer = new StringTokenizer(header, ";");
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken().trim();
            String[] split = token.split("=");
            String name = split[0].trim().toLowerCase();
            String value = split.length > 1 ? split[1].trim() : null;
            map.put(name, value);
        }

        HMACSignature result = new HMACSignature();
        result.setVersion(map.get(VERSION_PARAM));
        result.setAlg(map.get(ALG_PARAM));
        result.setSignature(map.get(SIGNATURE_PARAM));
        Long timestamp = parseTimestamp(map);
        result.setTimestamp(timestamp);
        return result;
    }

    private static Long parseTimestamp(Map<String, String> map) {
        Long timestamp;
        try {
            timestamp = Optional.ofNullable(map.get(TIMESTAMP_PARAM))
                    .map(Long::parseLong)
                    .orElse(null);
        } catch (NumberFormatException e) {
            throw new ValidateException("Invalid timestamp format in signature header", e);
        }
        return timestamp;
    }
}
