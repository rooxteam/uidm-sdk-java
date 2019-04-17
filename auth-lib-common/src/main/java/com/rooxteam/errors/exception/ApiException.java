package com.rooxteam.errors.exception;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

/**
 * Представляет собой основную ошибку API, сериализуемую в json.
 * <p/>
 * Класс содержит внутренний код ошибки, сообщение об ошибке, HTTP статус (по-умолчанию <em>Bad request</em> 400-ый статус), а также поле {@code data} куда сериализуется stack trace возникшего эксепшена. <br />
 * Stack trace сериализуется если проперти <em>com.rooxteam.webapi.common.exceptions.stackTraceEnable</em> равняется true
 */
@Getter
@Setter
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonPropertyOrder({"code", "message", "displayMessage", "data"})
@JsonRootName("error")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiException extends RuntimeException implements Serializable {
    private static final long serialVersionUID = 5243199917304965398L;

    @JsonProperty("code")
    private int code;
    @JsonProperty("message")
    private String message;
    @JsonProperty("displayMessage")
    private String displayMessage;
    @JsonProperty("data")
    private Object data;

    /**
     * Http статус ошибки.
     * <p>
     * <em>По-умолчанию Bad request (400)</em>
     */
    private HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

    @SuppressWarnings("unused")
    public ApiException() {
    }

    /**
     * Конструктор {@code ApiException} с кодом и сообщение об ошибке
     *
     * @param code    Код ошибки
     * @param message Подробное описание ошибки
     */
    @SuppressWarnings("unused")
    public ApiException(int code, String message) {
        this.message = message;
        this.code = code;
    }

    /**
     * Конструктор {@code ApiException} с кодом и сообщение об ошибке
     *
     * @param status  Код ошибки
     * @param message Подробное описание ошибки
     */
    public ApiException(HttpStatus status, String message) {
        this.httpStatus = status;
        this.message = message;
        this.code = status.value();
    }

    @SuppressWarnings("unused")
    public ApiException(HttpStatus httpStatus, int code, String message, Throwable cause, Object data) {
        super(message, cause);
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * Конструктор {@code ApiException} с кодом и описанием ошибки, а так же с причиной возникновения ошибки.
     *
     * @param code    Код ошибки
     * @param message Подробное описание ошибки
     * @param cause   Причина возникновения
     */
    @SuppressWarnings("WeakerAccess")
    public ApiException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }

    /**
     * Конструктор {@code ApiException} с кодом и описанием ошибки, а так же с причиной возникновения ошибки.
     *
     * @param status  Код ошибки
     * @param message Подробное описание ошибки
     * @param cause   Причина возникновения
     */
    @SuppressWarnings("unused")
    public ApiException(HttpStatus status, String message, Throwable cause) {
        super(message, cause);
        this.httpStatus = status;
        this.code = status.value();
        this.message = message;
    }

    /**
     * Конструктор {@code ApiException} с кодом ошибки и с причиной возникновения ошибки.
     *
     * @param code  Код ошибки
     * @param cause Причина возникновения
     */
    @SuppressWarnings("unused")
    public ApiException(int code, Throwable cause) {
        this(code, cause.getMessage(), cause);
    }

    /**
     * Конструктор {@code ApiException} с кодом ошибки
     *
     * @param code Код ошибки
     */
    @SuppressWarnings("unused")
    public ApiException(int code) {
        this.code = code;
    }
}
