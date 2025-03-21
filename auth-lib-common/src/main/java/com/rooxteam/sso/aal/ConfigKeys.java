package com.rooxteam.sso.aal;

import com.rooxteam.sso.clientcredentials.ValidationType;

/**
 * Справочник ключей конфигурации.
 */
public interface ConfigKeys {

    /* Обязательные ключи конфигурации */

    /**
     * {@value} - Внешний базовый URL SSO
     */
    String SSO_URL = "com.rooxteam.aal.sso.endpoint";

    /**
     * {@value} - Путь к oauth2 access token endpoint
     */
    String ACCESS_TOKEN_PATH = "com.rooxteam.aal.sso.access_token.endpoint";

    String ACCESS_TOKEN_PATH_DEFAULT = "/oauth2/access_token";

    /**
     * {@value} - Путь к oauth2 access token exchange endpoint
     */
    String TOKEN_EXCHANGE_PATH = "com.rooxteam.aal.sso.token_exchange.endpoint";

    String TOKEN_EXCHANGE_PATH_DEFAULT = "/oauth2/access_token";

    /**
     * {@value} - Путь к oauth2 token info endpoint
     */
    String TOKEN_INFO_PATH = "com.rooxteam.aal.sso.token_info.endpoint";

    String TOKEN_INFO_PATH_DEFAULT = "/oauth2/tokeninfo";

    /**
     * {@value} - Имя OAuth клиента для аутентификации в Customer SSO из AAL
     */
    String CLIENT_ID = "com.rooxteam.aal.auth.client";

    /**
     * {@value} - Пароль OAuth клиента для аутентификации в Customer SSO из AAL
     */
    String CLIENT_SECRET = "com.rooxteam.aal.auth.password";

    /**
     * {@value} - Имя OAuth клиента для аутентификации в Customer SSO из AAL
     */
    String ALLOWED_CLIENT_IDS = "com.rooxteam.aal.auth.allowed-clients";

    /**
     * {@value} - JWT shared key
     */
    String SHARED_KEY = "com.rooxteam.aal.sso.shared_key";

    /**
     * {@value} - Идентификатор SSO-сервиса, выдавшего удостоверение.
     */
    String JWT_ISSUER = "com.rooxteam.aal.jwt.issuer";


    /* Опциональные ключи конфигурации */

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

    String COOKIE_STORE_ENABLE_PER_REQUEST = "com.rooxteam.sso.aal.client.cookie.store.enablePerRequest";

    boolean COOKIE_STORE_ENABLE_PER_REQUEST_DEFAULT = true;

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
    String AAL_VALIDATION_TYPE = "com.rooxteam.aal.validation_type";

    String PRINCIPAL_PROVIDER_TYPE = "com.rooxteam.aal.filter.principal_provider_type";

    String PRINCIPAL_PROVIDER_TYPE_DEFAULT = ProviderType.TOKENINFO.toString();

    String CLIENT_CREDENTIALS_VALIDATION_TYPE = "com.rooxteam.client_credentials.validation_type";

    String CLIENT_CREDENTIALS_VALIDATION_TYPE_DEFAULT = ValidationType.TOKENINFO.toString();
    
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
     * Имя куки с шаблонным {clientId}. {@link #TOKEN_COOKIE_NAME_KEY} оставлено для обратной совместимости/
     */
    String TOKEN_COOKIE_NAME_PER_CLIENT_KEY = "com.rooxteam.aal.sso.{clientId}.token.cookie.name";

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
     * OPA data API URL, like <a href="http://opa.example.com/v1/data">http://opa.example.com/v1/data</a>.
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

    /**
     * Список классов валидаторов для JWT токенов через запятую.
     *
     * Неотключаемые валидаторы (используются всегда, нельзя отключить)
     *  - com.rooxteam.sso.aal.validation.jwt.impl.AlgNoneValidator - проверка что не используется JWT токен без подписи
     *  - com.rooxteam.sso.aal.validation.jwt.impl.TimeIntervalValidator - валидация клеймов nbf и exp
     *
     * Возможные значения:
     *  - com.rooxteam.sso.aal.validation.jwt.impl.AudValidator - проверка клейма aud - включен по-умолчанию
     *  - com.rooxteam.sso.aal.validation.jwt.impl.AlgValidator - проверка алгоритма
     *  - com.rooxteam.sso.aal.validation.jwt.impl.SignatureValidator - @deprecated проверка подписи ECDSA и RSASSA
     *  - com.rooxteam.sso.aal.validation.jwt.impl.EsSignatureValidator - проверка подписи ECDSA
     *  - com.rooxteam.sso.aal.validation.jwt.impl.HsSignatureValidator - проверка подписи HMAC
     *  - com.rooxteam.sso.aal.validation.jwt.impl.RsSignatureValidator - проверка подписи RSASSA
     *  - com.rooxteam.sso.aal.validation.jwt.impl.IssuerValidator - проверка клейма iss
     *  - com.rooxteam.sso.aal.validation.jwt.impl.IssueTimeValidator - проверка клейма iat
     *  - com.rooxteam.sso.aal.validation.jwt.impl.SubNotEmptyValidator - проверка на не пустой клейм sub
     */
    String JWT_VALIDATORS = "com.rooxteam.aal.jwt.validators";
    String JWT_VALIDATORS_DEFAULT = "com.rooxteam.sso.aal.validation.jwt.impl.AudValidator";

    String REQUEST_SIGNATURE_HEADER = "X-Request-Signature";

    /**
     * Список валидаторов для клеймов токена через запятую.
     * Используется при валидации через TOKENINFO.
     *
     * Возможные значения:
     *  - com.rooxteam.sso.aal.validation.claims.impl.ClientIdValidator - валидация клейма 'client_id'
     */
    String CLAIM_VALIDATORS = "com.rooxteam.aal.claims.validators";
    String CLAIM_VALIDATORS_DEFAULT = "com.rooxteam.sso.aal.validation.claims.impl.ClientIdValidator";

    /**
     * Время жизни JWKS в кэше (в минутах). В случае значения -1 время жизни не лимитируется.
     * Значение по умолчанию берется из {@value com.nimbusds.jose.jwk.source.DefaultJWKSetCache#DEFAULT_LIFESPAN_MINUTES}
     */
    String JWKS_CACHE_LIFESPAN = "com.rooxteam.aal.jwks.cache.lifespan";

    /**
     * Период обновления JWKS в кэше (в минутах). Должен быть меньше либо равен времени жизни {@value #JWKS_CACHE_LIFESPAN}
     * В случае значения -1 обновление происходить не будет.
     * Значение по умолчанию берется из {@value com.nimbusds.jose.jwk.source.DefaultJWKSetCache#DEFAULT_REFRESH_TIME_MINUTES}
     */
    String JWKS_CACHE_REFRESH_TIME = "com.rooxteam.aal.jwks.cache.refreshTime";

    /**
     * Таймаут подключения к HTTP сервису JWKS (в милисекундах). В случае значения 0 таймаут не лимитируется.
     * Значение по умолчанию берется из {@value com.nimbusds.jose.jwk.source.RemoteJWKSet#DEFAULT_HTTP_CONNECT_TIMEOUT}
     */
    String JWKS_HTTP_CONNECT_TIMEOUT = "com.rooxteam.aal.jwks.connect.timeout";

    /**
     * Таймаут чтения из HTTP сервиса JWKS (в милисекундах). В случае значения 0 таймаут не лимитируется.
     * Значение по умолчанию берется из {@value com.nimbusds.jose.jwk.source.RemoteJWKSet#DEFAULT_HTTP_READ_TIMEOUT}
     */
    String JWKS_HTTP_READ_TIMEOUT = "com.rooxteam.aal.jwks.read.timeout";

    /**
     * Настройка включения/выключения старого способа маскирования чувствительных данных. 
     * Значение по умолчанию берется из {@value #LEGACY_MASKING_ENABLED_DEFAULT}
     */
    String LEGACY_MASKING_ENABLED = "com.rooxteam.aal.legacyMaskingEnabled";

    /**
     * Значение по-умолчанию для настройки включения/выключения старого способа маскирования чувствительных данных.
     */
    boolean LEGACY_MASKING_ENABLED_DEFAULT = true;

    /**
     * {@value} - допустимый интервал времени в секундах, в пределах которого допускается небольшое расхождение между
     * временем на сервере, создавшем токен, и временем на сервере, который проверяет токен.
     */
    String JWT_VALIDATION_CLOCK_SKEW = "com.rooxteam.aal.jwt.validation.clockSkew";

    /**
     * {@value} - допустимые алгоритмы цифровой подписи токена.
     */
    String JWT_VALIDATION_ALLOWED_ALGORITHMS = "com.rooxteam.aal.jwt.validation.allowedAlgorithms";

    /**
     *  Внешний URL для userinfo (например, используется в PrincipalUserInfoProviderImpl)
     */
    String USERINFO_URL = "com.rooxteam.aal.userinfo.endpoint";

    /**
     * Secret для валидации HS алгоритмов у JWS
     */
    String JWT_VALIDATION_HS_SHARED_SECRET = "com.rooxteam.aal.jwt.validation.HSsharedSecret";
}
