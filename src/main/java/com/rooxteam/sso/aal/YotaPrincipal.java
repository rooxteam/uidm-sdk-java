package com.rooxteam.sso.aal;

import java.util.Calendar;

/**
 * Представляет авторизационную информацию о пользователе.
 *
 * @deprecated use {@link Principal} instead.
 */
@Deprecated
public interface YotaPrincipal {

    /**
     * Имя параметра для хранения внутренней сессии SSO-сервера
     */
    String SESSION_PARAM = "session";

    /**
     * Получить свойство принципала.
     *
     * @param propertyScope уровень видимости свойства
     * @param name          имя свойтства
     * @return значение свойства
     */
    Object getProperty(PropertyScope propertyScope, String name);

    /**
     * Установить свойство принципала (значения остаются только в клиентском кеше)
     *
     * @param propertyScope уровень видимости свойства
     * @param name          имя свойтства
     * @param value         значение свойства
     */
    void setProperty(PropertyScope propertyScope, String name, Object value);

    /**
     * Получить дату и время, с которой сведения о принципале станут недействительными.
     *
     * @return Время истечения срока действия токена.
     */
    Calendar getExpirationTime();

    /**
     * Получить свёртку JWT-токена для отдачи клиенту.
     * Если текущий принципал был получен по ранее выданному токену, возвращается "ранее выданный токен".
     * Для остальных случаев возвращается текущий JWT-токен, созданный SSO как результат текущей аутентификации.*
     *
     * @return свертка JWT-токена.
     */
    String getJwtToken();

}
