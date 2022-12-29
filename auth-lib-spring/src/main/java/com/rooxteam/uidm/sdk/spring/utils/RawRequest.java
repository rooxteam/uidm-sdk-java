package com.rooxteam.uidm.sdk.spring.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Методу, помеченного данной аннотацией становится доступным тело исходного HTTP-запроса до преобразования в объект.
 * Результат доступен через request-scoped {@link RequestData}
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RawRequest {
}
