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
     * {@value} - Включение/отключение механизма кэширования для clientCredentials аутентификации
     * По умолчанию: {@value #CLIENT_CREDENTIALS_CACHE_ENABLED_DEFAULT}
     */
    String CLIENT_CREDENTIALS_CACHE_ENABLED = "com.rooxteam.auth.client_credentials.cacheEnabled";

    /**
     * Значение ClientCredentialsCacheEnabled для clientCredentials аутентификации по умолчанию
     */
    boolean CLIENT_CREDENTIALS_CACHE_ENABLED_DEFAULT = true;

    /**
     * {@value} - Время за сколько до истечения срока жизни токена, его необходимо обновить
     * По умолчанию: {@value #UPDATE_TIME_BEFORE_TOKEN_EXPIRATION_DEFAULT}
     */
    String UPDATE_TIME_BEFORE_TOKEN_EXPIRATION = "com.rooxteam.auth.client_credentials.updateTimeBeforeTokenExpiration";

    /**
     * Значение UpdateTimeBeforeTokenExpiration для clientCredentials аутентификации в секундах по умолчанию
     */
    int UPDATE_TIME_BEFORE_TOKEN_EXPIRATION_DEFAULT = 60;

    /**
     * {@value} - Отправлять токен в заголовке Authorization в процессе валидации токена
     * По умолчанию: {@value #SEND_TOKEN_IN_AUTHORIZATION_HEADER_IN_VALIDATION_DEFAULT}
     */
    String SEND_TOKEN_IN_AUTHORIZATION_HEADER_IN_VALIDATION_ENABLED = "com.rooxteam.auth.client_credentials.validation.enabledSendingTokenInHeader";

    /**
     * Значение ClientCredentialsCacheEnabled для clientCredentials аутентификации по умолчанию
     */
    boolean SEND_TOKEN_IN_AUTHORIZATION_HEADER_IN_VALIDATION_DEFAULT = false;

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
    int POLICY_CACHE_EXPIRE_AFTER_WRITE_DEFAULT = 3;

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
     * <ul>JWT  - aal will use RooX Solutions REST policy service </ul>
     * <ul>CONFIG - aal will use config-based, local policy evaluation by {@code com.rooxteam.aal.policies} property</ul>
     * <ul>OPA - aal will use OPA</ul>
     * </li>
     */
    String AUTHORIZATION_TYPE = "com.rooxteam.aal.authorization_type";

    String AUTHORIZATION_TYPE_DEFAULT = AuthorizationType.JWT.toString();

    /**
     * Переменная VALIDATION_TYPE переименована в PRINCIPAL_PROVIDER_TYPE
     * Новое значение переменной com.rooxteam.aal.filter.principal_provider_type
     */
    @Deprecated
    String VALIDATION_TYPE = "com.rooxteam.aal.validation_type";

    String PRINCIPAL_PROVIDER_TYPE = "com.rooxteam.aal.filter.principal_provider_type";

    String PRINCIPAL_PROVIDER_TYPE_DEFAULT = ProviderType.TOKENINFO.toString();


    String CONNECTION_REUSE_STRATEGY = "com.rooxteam.aal.connection_reuse_strategy";

    String CONNECTION_REUSE_STRATEGY_DEFAULT = ConnectionReuseStrategy.KEEP_ALIVE.name();

    /**
     * Local policies which will be used by aal when {@code com.rooxteam.aal.authentication_type} is CONFIG
     * format:
     * {"<resourceName1>": {"<actionName1.1>": {"authLevel": 2},"<actionName1.2>": {"authLevel": 2}},"<resourceName2>":  {"<actionName2.1>": {"authLevel": 2}}}
     */
    String LOCAL_POLICIES = "com.rooxteam.aal.policies";

    /**
     * Разделяемый секрет для легаси системной аутентификации.
     * Используйте OAuth2.0 Client Credentials Flow в новых проектах
     */
    String INTERNAL_TOKEN_KEY = "com.rooxteam.webapi.auth.internal-token";

    /**
     * Имя куки, где лежит токен доступа
     */
    String TOKEN_COOKIE_NAME_KEY = "com.rooxteam.aal.sso.token.cookie.name";

    /**
     * Имя куки, где лежит токен доступа, старое имя свойства
     */
    String TOKEN_COOKIE_LEGACY_ROOX_PRODUCTS_KEY = "com.rooxteam.webapi.filters.cookieFilter.cookies.sso";

    /**
     * Источник IP-адреса пользователя для запросов в tokeninfo и policy evaluation.
     * Возможные значения: `request` (из запроса) , `header` (из заголовка запроса).
     * По-умолчанию IP-адрес не пишется.
     */
    String USER_CONTEXT_IP_SOURCE = "com.rooxteam.aal.user-context.ip-source";

    /**
     * Источник IP-адреса пользователя для запросов в tokeninfo и policy evaluation.
     * Возможные значения: request (из запроса) , header (из заголовка запроса).
     * По-умолчанию IP-адрес не пишется.
     */
    String USER_CONTEXT_IP_HEADER = "com.rooxteam.aal.user-context.ip-header";

    /**
     * Источник IP-адреса пользователя для запросов в tokeninfo и policy evaluation.
     * Возможные значения: request (из запроса) , header (из заголовка запроса).
     * По-умолчанию IP-адрес не пишется.
     */
    String USER_CONTEXT_IP_HEADER_DEFAULT = "X-Forwarded-For";

    /**
     * Список атрибутов из Principal.sharedIdentityProperties которые надо сложить в MDC
     */
    String USER_ATTRIBUTES_EXPOSE_TO_MDC = "com.rooxteam.aal.mdc.principal_attributes_to_expose";


    /**
     * OPA data API URL, like "http://opa.example.com/v1/data".
     */
    String OPA_DATA_API_URL = "com.rooxteam.aal.opa.data_api.endpoint";

    String OPA_PACKAGE = "com.rooxteam.aal.opa.package";

    String OPA_PACKAGE_DEFAULT = "authz";


    /**
     * {@value} - Имя OAuth клиента для аутентификации в Customer SSO из AAL для заданного реалма
     * Значение реалма может быть получено из токена.
     */
    String CLIENT_ID_FOR_REALM = "com.rooxteam.realms.{realm}.aal.auth.client";

    /**
     * {@value} - Пароль OAuth клиента для аутентификации в Customer SSO из AAL для заданного реалма
     * Значение реалма может быть получено из токена.
     */
    String CLIENT_SECRET_FOR_REALM = "com.rooxteam.realms.{realm}.aal.auth.password";

    String JWKS_URL = "com.rooxteam.aal.jwks.url";
    String JWT_VALIDATORS = "com.rooxteam.all.jwt.validators";
    String REQUEST_SIGNATURE_HEADER = "X-Request-Signature";
}
