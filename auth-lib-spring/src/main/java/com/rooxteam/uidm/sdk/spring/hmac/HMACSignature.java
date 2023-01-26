package com.rooxteam.uidm.sdk.spring.hmac;

import lombok.Getter;
import lombok.Setter;

/**
 * Модель данных, описывающая заголовок, содержащий HMAC-подпись.
 * TODO копия содержится в sso-server, вынести в общую библиотеку
 */
@Getter
@Setter
public class HMACSignature {

    /**
     * Версия
     */
    private String version;

    /**
     * unix-time timestamp
     */
    private Long timestamp;

    /**
     * Алгоритм (поддерживается только значение "hmac-sha256").
     */
    private String alg;

    /**
     * Base64-кодированное значение рассчитанной имитовставки
     */
    private String signature;
}
