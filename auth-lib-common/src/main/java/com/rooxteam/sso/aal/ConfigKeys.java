package com.rooxteam.sso.aal;

/**
 * Справочник ключей конфигурации.
 */
public interface ConfigKeys {

    /**
     * Обязательные ключи конфигурации
     */


    /**
     * {@value} - Внешний базовый URL SSO
     */
    String SSO_URL = "com.rooxteam.aal.sso.endpoint";

    /**
     * {@value} - Имя OAuth клиента для аутентификации в Customer SSO из AAL
     */
    String CLIENT_ID = "com.rooxteam.aal.auth.client";

    /**
     * {@value} - Пароль OAuth клиента для аутентификации в Customer SSO из AAL
     */
    String CLIENT_SECRET = "com.rooxteam.aal.auth.password";

    /**
     * {@value} - JWT shared key
     */
    String SHARED_KEY = "com.rooxteam.aal.sso.shared_key";

    /**
     * {@value} - Идентификатор SSO-сервиса, выдавшего удостоверение.
     */
    String JWT_ISSUER = "com.rooxteam.aal.jwt.issuer";


    /**
     * Опциональные ключи конфигурации
     */

    /**
     * {@value} - Realm для аутентификации в Customer SSO из AAL.
     * По умолчанию: {@value #REALM_DEFAULT}
     */
    String REALM = "com.rooxteam.aal.auth.realm";

    /**
     * Значение realm для аутентификации в Customer SSO из AAL по умолчпнию
     */
    String REALM_DEFAULT = "/customer";

    /**
     * {@value} - Имя цепочки аутентификации в Customer SSO для AAL.
     * По умолчанию: {@value #AUTH_SERVICE_DEFAULT}
     */
    String AUTH_SERVICE = "com.rooxteam.aal.auth.service";

    /**
     * Значение имени цепочки аутентификации в Customer SSO для AAL по умолчанию
     */
    String AUTH_SERVICE_DEFAULT = "dispatcher";

    /**
     * {@value} - Имя сценария в Customer SSO для AAL.
     * По умолчанию: {@value #OTP_SERVICE_DEFAULT}
     */
    String OTP_SERVICE = "com.rooxteam.aal.otp.service";

    /**
     * Имя сценария в Customer SSO для AAL по умолчанию
     */
    String OTP_SERVICE_DEFAULT = "otp-sms";

    /**
     * {@value} - Имя параметра, в котором в запросе M2M будет передан текущий токен пользователя, запросившего OTP.
     * По умолчанию: {@value #OTP_CURRENT_TOKEN_PARAM_NAME_DEFAULT}
     */
    String OTP_CURRENT_TOKEN_PARAM_NAME = "com.rooxteam.aal.otp.token.current.name";

    /**
     * Имя параметра, в котором в запросе M2M будет передан текущий токен пользователя, запросившего OTP. (по умолчанию)
     */
    String OTP_CURRENT_TOKEN_PARAM_NAME_DEFAULT = "jwt";

    /**
     * {@value} - Разрешать доступ к ресурсу при отсутствии в SSO подходящих политик.
     * По умолчанию: {@value #ALLOW_ACCESS_WITHOUT_POLICY_DEFAULT}
     */
    String ALLOW_ACCESS_WITHOUT_POLICY = "com.rooxteam.aal.allow_access_without_policy";

    /**
     * Значение по умолчанию для разрешния доступа к ресурсу при отсутствии в SSO подходящих политик
     */
    boolean ALLOW_ACCESS_WITHOUT_POLICY_DEFAULT = false;

    /**
     * {@value} - Должен ли быть включен поллинг при создании AAL.
     * По умолчанию: {@value #POLLING_ENABLED_DEFAULT}
     */
    String POLLING_ENABLED = "com.rooxteam.aal.polling.enabled";

    /**
     * Значение по умолчанию для включения поллинга при создании AAL
     */
    boolean POLLING_ENABLED_DEFAULT = false;

    /**
     * {@value} - Периодичность запуска поллинга в секундах.
     * По умолчанию: {@value #POLLING_PERIOD_DEFAULT}
     */
    String POLLING_PERIOD = "com.rooxteam.aal.polling.period";

    /**
     * Значение периодичности запуска поллинга по умолчанию в секундах
     */
    int POLLING_PERIOD_DEFAULT = 10;

    /**
     * {@value} - Максимальный размер кеша авторизаций.
     * По умолчанию: {@value #POLICY_CACHE_LIMIT_DEFAULT}
     */
    String POLICY_CACHE_LIMIT = "com.rooxteam.aal.policy.cache.size";

    /**
     * Значение по умолчанию для максимального размера кеша авторизаций
     */
    int POLICY_CACHE_LIMIT_DEFAULT = 10;

    /**
     * {@value} - Максимальное время жизни элементов в кеше авторизаций в секундах.
     * По умолчанию: {@value #POLICY_CACHE_EXPIRE_AFTER_WRITE_DEFAULT}
     */
    String POLICY_CACHE_EXPIRE_AFTER_WRITE = "com.rooxteam.aal.policy.cache.expire_after_write";

    /**
     * Значение по умолчанию для максимального времени жизни элементов в кеше авторизаций в секундах
     */
    int POLICY_CACHE_EXPIRE_AFTER_WRITE_DEFAULT = 60;

    /**
     * {@value} - Должны ли применяться политики для систем.
     * По умолчанию: {@value #POLICIES_FOR_SYSTEM_DEFAULT}
     */
    String POLICIES_FOR_SYSTEM = "com.rooxteam.aal.policy.enabledForSystem";

    /**
     * Значение по умолчанию для включения политик для систем.
     */
    boolean POLICIES_FOR_SYSTEM_DEFAULT = false;

    /**
     * {@value} - Максимальный размер кеша аутентификаций.
     * По умолчанию: {@value #PRINCIPAL_CACHE_LIMIT_DEFAULT}
     */
    String PRINCIPAL_CACHE_LIMIT = "com.rooxteam.aal.principal.cache.size";

    /**
     * Значение по умолчанию для максимального размера кеша аутентификаций
     */
    int PRINCIPAL_CACHE_LIMIT_DEFAULT = 10;

    /**
     * {@value} - Максимальное время жизни элементов в кеше аутентификаций в секундах.
     * По умолчанию: {@value #PRINCIPAL_CACHE_EXPIRE_AFTER_WRITE_DEFAULT}
     */
    String PRINCIPAL_CACHE_EXPIRE_AFTER_WRITE = "com.rooxteam.aal.principal.cache.expire_after_write";

    /**
     * Значение по умолчанию для максимального времени жизни элементов в кеше аутентификаций в секундах
     */
    int PRINCIPAL_CACHE_EXPIRE_AFTER_WRITE_DEFAULT = 60;

    /**
     * {@value} - Размер connection пула
     * По умолчанию: {@value #HTTP_CONNECTION_POOL_SIZE_DEFAULT}
     */
    String HTTP_CONNECTION_POOL_SIZE = "com.rooxteam.aal.http.connection_pool_size";

    /**
     * Значение по умолчанию для размер connection пула
     */
    int HTTP_CONNECTION_POOL_SIZE_DEFAULT = 2147483647;

    /**
     * {@value} - Количество соединений на маршрут
     * По умолчанию: {@value #HTTP_CONNECTION_POOL_SIZE_PER_ROUTE_DEFAULT}
     */
    String HTTP_CONNECTION_POOL_SIZE_PER_ROUTE = "com.rooxteam.aal.http.connection_pool_size.per_route";

    /**
     * Значение по умолчанию для кол-ва соединений на маршрут
     */
    int HTTP_CONNECTION_POOL_SIZE_PER_ROUTE_DEFAULT = 256;

    /**
     * {@value} - Время ожидания ответа для сокета
     * По умолчанию: {@value #HTTP_SOCKET_TIMEOUT_DEFAULT}
     */
    String HTTP_SOCKET_TIMEOUT = "com.rooxteam.aal.http.socket_timeout";

    /**
     * Значение по умолчанию для времени ожидания ответа для сокета в миллисекундах
     */
    int HTTP_SOCKET_TIMEOUT_DEFAULT = 3000;

    /**
     * {@value} - Время ожидания установки соединения
     * По умолчанию: {@value #HTTP_CONNECTION_TIMEOUT_DEFAULT}
     */
    String HTTP_CONNECTION_TIMEOUT = "com.rooxteam.aal.http.connection_timeout";

    int HTTP_CONNECTION_TIMEOUT_DEFAULT = 10000;

    /**
     * Authentication type can be:
     * <li>
     * <ul>SSO_TOKEN - aal will use policy SDK for policy evaluation </ul>
     * <ul>JWT  - aal will use RooX Solutions REST policy service </ul>
     * <ul>CONFIG - aal will use config-based, local policy evaluation by {@code com.rooxteam.aal.policies} property</ul>
     * </li>
     */
    String AUTHORIZATION_TYPE = "com.rooxteam.aal.authorization_type";

    String AUTHORIZATION_TYPE_DEFAULT = AuthorizationType.JWT.toString();


    String CONNECTION_REUSE_STRATEGY = "com.rooxteam.aal.connection_reuse_strategy";

    String CONNECTION_REUSE_STRATEGY_DEFAULT = ConnectionReuseStrategy.KEEP_ALIVE.name();

    /**
     * Local policies which will be used by aal when {@code com.rooxteam.aal.authentication_type} is CONFIG
     * format:
     * {"<resourceName1>": {"<actionName1.1>": {"authLevel": 2},"<actionName1.2>": {"authLevel": 2}},"<resourceName2>":  {"<actionName2.1>": {"authLevel": 2}}}
     */
    String LOCAL_POLICIES = "com.rooxteam.aal.policies";

    /**
     * Список атрибутов из информации о токене, которые необходимо пробросить в Principal.sharedIdentityProperties.
     */
    String TOKEN_INFO_ATTRIBUTES_FORWARD = "com.rooxteam.aal.token.info.forward.attributes";
}
