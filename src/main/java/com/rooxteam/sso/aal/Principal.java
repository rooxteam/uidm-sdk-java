package com.rooxteam.sso.aal;

import java.util.Calendar;
import java.util.Map;

/**
 * Представляет авторизационную информацию о пользователе.
 */
public interface Principal {

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
     * Получить свойства принципала.
     *
     * @param propertyScope уровень видимости свойств
     * @return пары имя-значение для всех свойств с данным уровнем видимости
     */
    Map<String, Object> getProperties(PropertyScope propertyScope);

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
     * @return свертка JWT-токена.
     */
    String getJwtToken();

    boolean isAnonymous();

}
