


# История изменений

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
