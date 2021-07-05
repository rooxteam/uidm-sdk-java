package com.rooxteam.uidm.sdk.spring.authentication;

/**
 * Настройки для фильтра {@link com.rooxteam.uidm.sdk.spring.authentication.UidmUserPreAuthenticationFilter}.
 */
public interface UserPreAuthFilterSettings {
    
    /**
     * Получить имя куки, в которой может находиться искомый токен для валидации.
     *
     * @return Cookie name
     */
    String getCookieName();

    /**
     * Получить имена свойств Принципала, которые будут сохранены в MDC Контексте (и удалены при завершении запроса).
     *
     * @return список свойств Принципала
     */
    String[] getPrincipalAttributesExposedToMDC();
}
