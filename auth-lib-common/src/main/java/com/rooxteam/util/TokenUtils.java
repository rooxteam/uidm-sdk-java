package com.rooxteam.util;

/**
 * Вспомогательный класс для работы с токенами
 * Может использоваться для манипуляций над токеном и его видоизменении
 */
public final class TokenUtils {

    private TokenUtils() {
    }

    /**
     * @param token - строка с токеном
     * @return Строку с префиксом "Bearer " префиксом, в случае пустого значения token - пустую строку.
     */
    public static String wrapBearerToken(String token) {
        return (token != null && !token.isEmpty()) ? "Bearer " + token : "";
    }
}
