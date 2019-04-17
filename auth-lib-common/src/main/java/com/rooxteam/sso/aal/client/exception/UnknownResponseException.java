package com.rooxteam.sso.aal.client.exception;

/**
 * Получен неизвестный ответ от WebSSO
 */
public class UnknownResponseException extends RuntimeException {

    public UnknownResponseException(String message) {
        super(message);
    }
}
