
# Как начать использовать

## 1. Подключить репозиторий RooX Solutions

С мая 2021 года SDK располагается в Maven Central. Никакие дополнительные репозитории подключать не надо.

## 2. Подключить библиотеку

```
repositories {
	mavenCentral()
}

dependencies {
	implementation "com.rooxteam.uidm.sdk:auth-lib-spring:$uidmSdkVersion"
}

```

1. Добавить импорт класса конфигурации

```java
@SpringBootApplication
@Import(UidmSpringSecurityFilterConfiguration.class)
public class DemoApplication {
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
}
```
2. Добавить фильтр в конфигурацию SpringSecurity

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

Смотреть полный пример https://bitbucket.org/rooxteam/uidm-sdk-java-samples/src/master/spring/

## Известные проблемы

Для Spring Boot версии ниже 2.0 выявлена проблема инициализации Spring Security.

Версии Spring Boot для которых подтвердилась проблема:
* 1.5.10.RELEASE
* 1.3.5.RELEASE

#### Как проявляется:

Приложение не стартует, в логах присутствуют следующие записи:

```
Initialization of bean failed; nested exception is org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'methodSecurityInterceptor' defined in class path resource [org/springframework/security/config/annotation/method/configuration/GlobalMethodSecurityConfiguration.class]: Invocation of init method failed; nested exception is java.lang.IllegalArgumentException: An AuthenticationManager is required
```

#### Решение

Добавить в конфигурацию Spring Security следующие методы:

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

# История изменений

## 3.26.1
Исправлен баг с невалидным токеном из кэша

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
