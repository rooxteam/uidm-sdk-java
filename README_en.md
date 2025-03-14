
# How to use

## 1. Configure maven repository

Since May 2021 the SDK has been published to Maven Central. No any additional repositories needed.

## 2. Add the library to your project

1. Add dependencies

```
repositories {
	mavenCentral()
}

dependencies {
	implementation "com.rooxteam.uidm.sdk:auth-lib-spring:$uidmSdkVersion"
}
```

where `$uidmSdkVersion` should be set to the library version  

2. Import configuration class

```java
@SpringBootApplication
@Import(UidmSpringSecurityFilterConfiguration.class)
public class DemoApplication {
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
}
```
3. Add filter to SpringSecurity configuration

```java
@Configuration
@EnableGlobalMethodSecurity(
        prePostEnabled = true,
        securedEnabled = true,
        jsr250Enabled = true)
public class CustomWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

    @Autowired
    UidmUserPreAuthenticationFilter uidmUserPreAuthenticationFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterAfter(uidmUserPreAuthenticationFilter, BasicAuthenticationFilter.class);
    }
}
```

See full source code of sample application at https://bitbucket.org/rooxteam/uidm-sdk-java-samples/src/master/spring/

## Known issues

With SpringBoot 2.0 or older there's an issue with Spring Security initialization.

Spring Boot versions confirmed to have the problem:
* 1.5.10.RELEASE
* 1.3.5.RELEASE

#### The problem appearance:

The application fails to start. Log-files contain the following messages:

```
Initialization of bean failed; nested exception is org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'methodSecurityInterceptor' defined in class path resource [org/springframework/security/config/annotation/method/configuration/GlobalMethodSecurityConfiguration.class]: Invocation of init method failed; nested exception is java.lang.IllegalArgumentException: An AuthenticationManager is required
```

#### The solution:

Add to Spring Security configuration the following methods:

```java
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {@Override

  ...
  
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(new AuthenticationProvider() {
            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                return authentication;
            }
    
            @Override
            public boolean supports(Class<?> authentication) {
                return true;
            }
        });
    }
    
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
      return super.authenticationManagerBean();
    }
  
  ...

}
```

# Version history

## 24.2
- Versioning Policy was updated
- Spring RestTemplate was replaced by Apache HttpClient in auth-lib-common
- Apache HttpClient updated to version 5.4 in auth-lib-common
- Cookie management disabled in HTTP client
- Added an optional ream parameter in Token Exchange request
- Added config key for allowed client ids for token validators ClientIdValidator, AudValidator

## 3.37.0
Added TokenExchangeClient

## 3.36.0
Added the ability to specify AuthorizationType and ProviderType when configuring the library

## 3.35.0
Added M2MClient

## 3.34.1
- AudValidator has been added by default in JWT-validators list,
- Fixed type in config key for JWT-validators list,
  Before: 'com.rooxteam.all.jwt.validators', after: 'com.rooxteam.aal.jwt.validators'.
  Old key was kept for backward compatibility with @deprecated mark.
- Fixed type in config key for token claims validators list,
  Before: 'com.rooxteam.all.claims.validators', after: 'com.rooxteam.aal.claims.validators'
  The old key value cannot be used.


IMPORTANT! Read it if you use config 'com.rooxteam.aal.filter.principal_provider_type=JWT' or
'com.rooxteam.aal.filter.principal_provider_type=USERINFO'!
After upgrade to SDK version >= 3.34.1 and if you don't use config 'com.rooxteam.aal.jwt.validators', 
the AudValidator will be used by default, which check 'aud' JWT token claim with the value from config 
'com.rooxteam.aal.auth.client', if they not match - it will be token validation error.
Reverting to the previous behavior is possible by adding the configuration 'com.rooxteam.aal.jwt.validators=' (empty value).


IMPORTANT! Read it if you use config 'com.rooxteam.aal.filter.principal_provider_type=TOKENINFO' or you don't use this config!
After upgrade to SDK version >= 3.34.1 and if you don't use config 'com.rooxteam.aal.claims.validators'
the ClientIdValidator will be used by default, which check claim 'client_id' from tokeninfo response
with the value from config 'com.rooxteam.aal.auth.client', if they not match - it will be token validation error.
Reverting to the previous behavior is possible by adding the configuration 'com.rooxteam.aal.claims.validators=' (empty value).

## 3.34.0

- AudValidator has been added to validate `aud` claim in JWT tokens.
- ClientIdValidator has been added to validate `client_id` claim in tokens provided by tokeninfo-endpoint.
- Added JWT token validators to tokens provided by OIDC userinfo-endpoint.
- In com.rooxteam.sso.clientcredentials.ClientCredentialsClient were added new versions of methods getAuthHeaderValue
  and getToken. New methods use java.util.Map instead of org.springframework.util.MultiValueMap. Old versions of methods
  marked as deprecated. Quick migration can be done by initializing a new Map implementation object around previously
  created parameters.

## 3.33.0
SignatureValidator is made deprecated. Added HsSignatureValidator, EsSignatureValidator, RsSignatureValidator instead

## 3.32.0

Added Verifier for HS like algorithms (HS256, HS384, HS512) in SignatureValidator

## 3.31.1
Fixed NullPointerException for invalid JWT

_Not yet translated._

## 3.31.0
Добавлена поддержали UserInfo OpenID Connect Core 1.0 (реализация PrincipalProvider)

## 3.30.0
1. Добавлены валидаторы: SubNotEmpty, Issuer, IssueTime
   Добавлена возможность:
2. при валидации iat, exp, настраивать clock skew(погрешность) в секундах
3. отключить валидацию токена (ValidationType.None) в механизме кэширования при ClientCredentials авторизации в Configuration клиента
```properties
# допустимый интервал времени в секундах, в пределах которого допускается небольшое расхождение между
# временем на сервере, создавшем токен, и временем на сервере, который проверяет токен
com.rooxteam.aal.jwt.validation.clockSkew=5
```
4. Добавлен AlgValidator с возможностью настроить допустимые алгоритмы цифровой подписи токена
```properties
# допустимые алгоритмы цифровой подписи токена
com.rooxteam.aal.jwt.validation.allowedAlgorithms=RS256,ES256,ES512
```

## 3.29.3
Версия com.nimbusds:nimbus-jose-jwt поднята до 9.32

## 3.29.2
1. Удалена настройка `com.rooxteam.auth.client_credentials.validation.enabledSendingTokenInHeader`
2. Добавлена настройка по типу валидации клиентского токена `com.rooxteam.client_credentials.validation_type`

## 3.29.0
Добавлен новый механизм маскирования чувствительных данных (через '*'). Настройки можно задавать через logback.xml в самом приложении, использующем SDK. Пример appender'а:
```xml
<appender name="STDOUT_MASKED" class="ch.qos.logback.core.ConsoleAppender">
  <encoder class="ch.qos.logback.core.encoder.LayoutWrappingEncoder">
    <layout class="com.rooxteam.util.MaskingPatternLayout">
      <maskPattern>Authorization\s*:\s*(.*?)</maskPattern>
      <maskPattern>"access_token"\s*:\s*(.*?)",</maskPattern>
      <maskPattern>client_secret=\[\s*(.*?)\],</maskPattern>
      <maskPattern>Token:\s*(.*)\.</maskPattern>
      <maskPattern>Token:\s*(.*),</maskPattern>
      <maskPattern>&amp;client_secret=\s*(.*?)&amp;</maskPattern>
      <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
    </layout>
  </encoder>
</appender>
```
**Замечание**: после настройки нового механизма необходимо отключить старый способ маскирования - выставить в настройках параметр `com.rooxteam.aal.legacyMaskingEnabled=false`

## 3.28.0
Добавлена возможность настраивать таймауты для запросов JWKS

## 3.27.0
1. Исправлена ошибка в AbstractUserPreAuthenticatedProcessingFilter, приводящая к многократному обращению к ssо-server для валидации access_token, что сказывается на производительности.
2. В сервлет-фильтре UidmUserPreAuthenticationFilter для названия куки используется шаблонизированный конфигурационный параметр.
3. AbstractUserPreAuthenticatedProcessingFilter - deprecated, используем BaseUserPreAuthenticatedProcessingFilter вместо него.

## 3.26.0
Добавлена возможность настраивать кэш JWKS

## 3.25.0
1. Добавлен метод валидации JWT в AalAuthorizationClient и RooxAuthenticationAuthorizationLibrary с ValidationResult-ом
2. Переименована системная настройка com.rooxteam.aal.validation_type -> com.rooxteam.aal.filter.principal_provider_type   

## 3.24.0

1. Возможность отключения кэширования токенов в SDK при получении системного токена по client credentials аутентификации
2. Возможность настройки - за сколько до истечения срока жизни токена, его необходимо обновить
3. Возможность отправлять access_token при валидации в механизме кэширования в Authorization заголовке

```properties
# Отключаем кэширование токенов
com.rooxteam.auth.client_credentials.cacheEnabled=false

# Задаем в секундах (с) время за сколько до ExpirationTime, следует обновить токен в кэше
com.rooxteam.auth.client_credentials.updateTimeBeforeTokenExpiration=60

# Отправлять access_token в Authorization header-е
com.rooxteam.auth.client_credentials.validation.enabledSendingTokenInHeader=true
```

## 3.23.1

Реализован механизм имитозащиты на основе HMAC-подписи.

## 3.22.0

Удалена зависимость на Guava, так как в ней не было пользы, и она тянула много вредных зависимостей.

Удален плагин roox-doc, так как он более не поддерживается.

## 3.21.1

Исправлен баг с системной аутентификацией.

## 3.21.0

Минимальная версия Java теперь 8.

Удален код, связанный с поддержкой API аутентификации (будет создан отдельный клиент к M2M протоколу).

Переработана локальная валидация JWT-токенов.

Валидаторы JWT-токенов теперь кастомизируемы.

Убрана поддержка разных скоупов атрибутов принципалов. Теперь атрибуты это просто клеймы токена.

Убрана поддержка кеша принципалов, так как это никогда хорошо не работало и не востребовано.




## 3.20.0

Частичная поддержка нескольких реалмов для одного приложения.

Реалм запроса выделяется из токена для авторизованных запросов и используется для определения агента OAuth для 
последующей передачи в запросы к SSO-server.

```properties
# Параметры аутентификации OAuth2 для приложения для заданного реалма
# Вместо placeholder `{realm}` подставить значение реалма.
com.rooxteam.realms.{realm}.aal.auth.client=test_client
com.rooxteam.realms.{realm}.aal.auth.password=secure_password


```

Доступно для следующего функционала:

* OTP операции (отправка, валидация, повторная отправка). Методы `/otp/*`
* Подписание через OTP. Методы `/sign-operation/*`

## 3.19.0

Поддержана авторизация через OPA. Для включения надо настроить

```properties
# Эндпойнт вычислителя
com.rooxteam.aal.opa.data_api.endpoint=http://localhost:8181/v1/data

# Переключаем вызовы isAllowed на OPA с Legacy OpenAM
com.rooxteam.aal.authorization_type=OPA

# Опционально указать OPA-пакет с политиками
com.rooxteam.aal.opa.package=authz

```

## 3.18.0

Вызов Evaluate Policy возможен теперь и из неавторизованной зоны тоже.

## 3.17.1

По соображениям безопасности изменено значение по-умолчанию для конфигурационного параметра 
`com.rooxteam.aal.policy.cache.expire_after_write`. Новое значение: 3 (секунды). 

## 3.17.0

Фильтр аутентификации по токену:

- вынесли конфигурируемые настройки:
  - для задания имени куки, в которой ищется токен;
  - список свойств Принципала, которые сохраняются в MDC;

## 3.16.1

Поддерживаются дополнительные атрибуты в результатах сценария подписания операций через контроллер /sign-operation

## 3.16.0

Поддерживается улучшенный сценарий подписания операций через контроллер /sign-operation


## 3.15.3

SDK поддерживает токены с и без легаси-префикса `sso_1.0_`.


## 3.15.2

- Результат последнего вызова Evaluate Policy содержащий эдвайсы политики автоматически добавляется в атрибуты аутентификации. 
  
  Чтобы получить информацию, которая возвращается в эдвайсах, нужно инжектировать AuthenticationState 
  в метод контроллера и получить значение атрибута `evaluationAdvices`.
  
```java
@RequestMapping(method = RequestMethod.POST, value = "/payment")
@PreAuthorize("@uidmAuthz.isAllowed('/payment', 'POST')")
public PaymentResponseDto makePayment(@RequestBody PaymentRequestDto requestDto, 
        AuthenticationState authenticationState) {

    Map<String, Object> evaluationAdvices = (Map<String, Object>) authenticationState.getAttributes()
        .get("evaluationAdvices");

    // your code here
}
```

## 3.15.1

- Результат последнего вызова Evaluate Policy содержащий клеймы автоматически добавляется в атрибуты аутентификации. 
  
  Чтобы получить информацию, которая возвращается в клеймах Policy Evaluation, нужно инжектировать AuthenticationState 
  в метод контроллера и получить значение атрибута `evaluationClaims`.
  
```java
@RequestMapping(method = RequestMethod.POST, value = "/payment")
@PreAuthorize("@uidmAuthz.isAllowed('/payment', 'POST')")
public PaymentResponseDto makePayment(@RequestBody PaymentRequestDto requestDto, 
        AuthenticationState authenticationState) {

    Map<String, Object> evaluationClaims = (Map<String, Object>) authenticationState.getAttributes()
        .get("evaluationClaims");

    // your code here
}
```

## 3.15.0

- Добавлен сервис PermissionsEvaluationService.
  Метод `evaluate` сервиса возвращает список разрешенных операций для заданного пользователя.
  
## 3.14.0

- Успешный ответ на запрос Evaluate Policy поддерживает передачу клеймов токена. 
  Sso-server должен быть сконфигурирован на формирование списка клеймов для передачи в ответе.
  
  см. конфигурационный параметр `com.rooxteam.sso.policy-evaluation.response.claims`

- Поддержка передачи IP-адреса пользователя в запросы на получение/верификацию OTP под операцию.
  
  Источник IP-адреса пользователя определяется конфигурационными параметрами: 
  `com.rooxteam.aal.user-context.ip-source` и `com.rooxteam.aal.user-context.ip-header`

## 3.13.0

- Запрос на Evaluate Policy поддерживает передачу контекста исходного запроса.
- Конфигурируемый источник IP-адреса пользователя. Конфигурационные параметры `com.rooxteam.aal.user-context.ip-source` 
и `com.rooxteam.aal.user-context.ip-header`

## 3.12.2

- ClientCredentialsClientFactory не выбрасывает исключение если в RestTemplate установлен дефолтный ErrorHandler

## 3.12.1

- ClientCredentialsClientFactory теперь выбрасывает исключение, если в него передается RestTemplate с настроенным обработчиком ошибок,
поскольку это ломает логику обработки ответов по UIDM, и в результате токен может не обновляться вовремя

## 3.12.0

- Библиотека совместима с Java6 
- Убрана зависимость на request-cookie-store, классы перенесены в библиотеку UIDM SDK
- Зависимость на Micrometer теперь опциональна. Если Micrometer нет в classpath - никакие метрики не пишутся

## 3.11.0

- Упрощена работа фильтров, они теперь не подвержены сессионным оптимизациям Spring.
- Улучшено логирование в фильтрах
- Убраны методы setContinueFilterChainOnUnsuccessfulAuthentication, setCheckForPrincipalChanges, setInvalidateSessionOnPrincipalChange поскольку фильтр теперь работает более прямолинейно, всегда пытаясь аутентифицировать запрос (если один из предыдущих фильтров в цепочке его еще не аутентифицировал)

### Migration Guide

- Если использовались методы setContinueFilterChainOnUnsuccessfulAuthentication, setCheckForPrincipalChanges, setInvalidateSessionOnPrincipalChange на фильтрах из библиотеки, то убрать их использование.
Правильная работа приложения не пострадает.    

## 3.10.0

- Добавлен NetworkErrorException для ошибок при работе с сетевыми запросами;
- Методы ClientCredentialsClient генерируют AalException вместо ClientAuthenticationException;
- Удален ClientAuthenticationException.

### Migration Guide

При работе с ClientCredentialsClient для обработки исключений следует использовать следующие классы: 
- AuthenticationException - ошибки аутентификации
- NetworkErrorException - сетевые ошибки или ошибки SSO-server

При работе с SsoAuthenticationClient и SsoAuthorizationClient 
- если для обработки исключений использовались классы AuthenticationException и AuthorizationException, 
то необходимо добавить обработку исключения NetworkErrorException;
- если для обработки исключений использовался класс AalException, дополнительных действий не требуется. 

## 3.9.1

- Ошибки при I/O таймауте получили более friendly описание. 

## 3.9.x

- Добавлена поддержка API /policyEvaluation, необходимого для сценария 2fa под операцию (портирован из старой AAL)

## 3.8.x

- Добавлена поддержка Legacy варианта системной аутентификации через shared secret в конфигурации. По умолчанию выключена.
- `com.rooxteam.aal.jwt.issuer` теперь задавать не обязательно при работе без локальной валидации
- Удалено конфигурационное свойство `com.rooxteam.aal.token.info.forward.attributes`. 
Теперь все клеймы, которые были возвращены из UIDM в /tokeninfo будут выставляться в атрибуты. 


## 3.7.x

- Добавлена поддержка OAuth2.0 Client Credentials Flow, смотреть пакет `com.rooxteam.sso.clientcredentials`
- Токен теперь берется и из cookie, если настроено свойство `com.rooxteam.aal.sso.token.cookie.name`

## 3.6.x

- Удалена поддержка протокола OpenAM Policy Agent
- Удалена обязательная зависимость на Apache Commons Configuration
- Домен метрик изменен на com.rooxteam

### Migration Guide

Инициализацию AAL следует производить через билдер конфига:
- ConfigurationBuilder.fromApacheCommonsConfiguration(config)
- ConfigurationBuilder.fromMap(map)

## 3.5.24

Последняя версия с поддержкой протокола OpenAM Policy Agent

История изменений не велась.
