package com.rooxteam.sso.aal;

/**
 * Обработчик событий резолвинга принципала. Данное событие необходимо для того, чтобы сервис провайдер смог
 * обогатить информацию о принципале из иных источников
 * (т.е. заполнить Session Params или Private Identity Params).
 * <p>
 * Событие возникает в следующих случаях:
 * <ul>
 * <li>Когда произвелась аутентификация и по её результатам был создан объект Principal</li>
 * <li>Когда произвелась инвалидация принципала</li>
 * <li>Повторный запрос сведений о принципале из OpenAM</li>
 * </ul>
 */
public interface PrincipalEventListener {
    /**
     * Обработка события, возникающего при аутентификации, когда был создан объект Principal.
     *
     * @param principal созданный после аутентификации Principal.
     */
    void onAuthenticate(Principal principal);

    /**
     * Обработка события, возникающего при инвалидации Principal
     *
     * @param principal инвалидированный Principal
     */
    void onInvalidate(Principal principal);

    /**
     * Обработка события, возникающего при повторном запросе сведений Principal
     *
     * @param principal обновленный Principal
     */
    void onRequestPrincipal(Principal principal);
}
