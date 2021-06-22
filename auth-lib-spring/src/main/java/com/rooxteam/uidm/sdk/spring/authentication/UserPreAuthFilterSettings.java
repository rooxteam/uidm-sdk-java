package com.rooxteam.uidm.sdk.spring.authentication;

import javax.servlet.ServletRequest;

/**
 * Настройки для фильтра {@link com.rooxteam.uidm.sdk.spring.authentication.UidmUserPreAuthenticationFilter}.
 */
public interface UserPreAuthFilterSettings {

    /**
     * Получить экземпляр {@link com.rooxteam.uidm.sdk.spring.authentication.SsoAuthorizationClient}.
     *
     * <br><br>
     * Свойство кешируется при инициализации сервиса ({@code UidmUserPreAuthenticationFilter} или его наследника)
     *
     * @return сервис для работы с токеном
     */
    SsoAuthorizationClient getAuthorizationClient();

    /**
     * Получить имя куки, в которой может находиться искомый токен для валидации.
     *
     * <br><br>
     * Свойство не кешируется и запрашивается каждый раз перед использованием, поэтому оно может быть вычисляемым на
     * основе, например, параметров текущего запроса.
     *
     * @param request данные HTTP запроса (на основе которых, например, можно задавать правила именования куки, в которой
     *                должен храниться токен).
     * @return Cookie name
     */
    String factoryGetCookieName(ServletRequest request);

    /**
     * Получить имя свойств Принципала, которые будут сохранены в MDC Контексте (и удалены при завершении запроса).
     *
     * <br><br>
     * Свойство не кешируется и запрашивается каждый раз перед использованием, поэтому оно может быть вычисляемым на
     * основе, например, параметров текущего запроса.
     *
     * @param request данные HTTP запроса (на основе которых, например, можно задавать правила именования куки, в которой
     *                должен храниться токен).
     *
     * @return список свойств Принципала
     */
    String[] factoryGetPrincipalAttributesExposedToMDC(ServletRequest request);
}
