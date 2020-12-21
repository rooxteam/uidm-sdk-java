
# Как начать использовать

## 1. Подключить репозиторий RooX Solutions

Gradle
```
repositories {
	maven {
		url  "https://dl.bintray.com/roox/uidm-sdk"
	}
}
```

Maven
```
<settings xmlns='http://maven.apache.org/SETTINGS/1.0.0' xsi:schemaLocation='http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'>
  <profiles>
    <profile>
      <repositories>
        <repository>
          <snapshots>
            <enabled>
              false
            </enabled>
          </snapshots>
          <id>
            bintray-roox-uidm-sdk
          </id>
          <name>
            bintray
          </name>
          <url>
            https://dl.bintray.com/roox/uidm-sdk
          </url>
        </repository>
      </repositories>     
      <id>
        bintray
      </id>
    </profile>
  </profiles>
  <activeProfiles>
    <activeProfile>
      bintray
    </activeProfile>
  </activeProfiles>
</settings>
```

## 2. Подключить библиотеку

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
