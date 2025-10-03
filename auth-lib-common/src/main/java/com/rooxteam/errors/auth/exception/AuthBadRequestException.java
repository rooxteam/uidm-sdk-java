package com.rooxteam.errors.auth.exception;

/**
 * {@code AuthBadRequestException} выбрасывается если запрос не может быть обработан c HTTP статусом 400
 */
public class AuthBadRequestException extends AuthApiException {

    private static final int errorCode = 400;
    private static final long serialVersionUID = -1538156953651882033L;

    /**
     * Конструктор {@code AuthBadRequestException} по-умолчанию
     */
    public AuthBadRequestException() {
        this(null, null);
    }

    /**
     * Конструктор {@code AuthBadRequestException} с описанием ошибки
     *
     * @param message Подробное описание ошибки
     */
    public AuthBadRequestException(String message) {
        this(message, null);
    }

    /**
     * Конструктор {@code AuthBadRequestException} с причиной возникновения ошибки
     *
     * @param cause Причина возникновения
     */
    public AuthBadRequestException(Throwable cause) {
        this(null, cause);
    }

    /**
     * Конструктор {@code AuthBadRequestException} с описанием и причиной возникновения ошибки
     *
     * @param message Подробное описание ошибки
     * @param cause   Причина возникновения
     */
    public AuthBadRequestException(String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}
