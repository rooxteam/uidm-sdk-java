package com.rooxteam.sso.aal;

import java.util.Calendar;
import java.util.Map;

/**
 * Представляет авторизационную информацию о пользователе.
 */
public interface Principal {

    /**
     * Получить свойство принципала.
     *
     * @param propertyScope уровень видимости свойства
     * @param name          имя свойства
     * @return значение свойства
     */
    @Deprecated
    Object getProperty(PropertyScope propertyScope, String name);

    /**
     * Получить свойство принципала.
     *
     * @param name          имя свойства
     * @return значение свойства
     */
    Object getProperty(String name);

    /**
     * Получить свойства принципала.
     *
     * @param propertyScope уровень видимости свойств
     * @return пары имя-значение для всех свойств с данным уровнем видимости
     */
    @Deprecated
    Map<String, Object> getProperties(PropertyScope propertyScope);

    /**
     * Получить свойства принципала.
     *
     * @return пары имя-значение для всех свойств с данным уровнем видимости
     */
    Map<String, Object> getProperties();


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
