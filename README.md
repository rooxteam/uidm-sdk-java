
# Как начать использовать

1. Подключить репозиторий RooX Solutions

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

2. Подключить библиотеку

Смотреть пример https://bitbucket.org/rooxteam/uidm-sdk-java-samples/src/master/spring/


# История изменений

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
