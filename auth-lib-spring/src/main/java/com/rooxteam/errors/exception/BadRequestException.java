package com.rooxteam.errors.exception;

/**
 * {@code BadRequestException} выбрасывается если запрос не может быть обработан c HTTP статусом 400
 */
public class BadRequestException extends ApiException {

    private static final int errorCode = 400;
    private static final long serialVersionUID = -1538156953651882033L;

    /**
     * Конструктор {@code BadRequestException} по-умолчанию
     */
    public BadRequestException() {
        this(null, null);
    }

    /**
     * Конструктор {@code BadRequestException} с описанием ошибки
     *
     * @param message Подробное описание ошибки
     */
    public BadRequestException(String message) {
        this(message, null);
    }

    /**
     * Конструктор {@code BadRequestException} с причиной возникновения ошибки
     *
     * @param cause Причина возникновения
     */
    public BadRequestException(Throwable cause) {
        this(null, cause);
    }

    /**
     * Конструктор {@code BadRequestException} с описанием и причиной возникновения ошибки
     *
     * @param message Подробное описание ошибки
     * @param cause   Причина возникновения
     */
    public BadRequestException(String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}
