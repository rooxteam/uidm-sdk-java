package com.rooxteam.sso.aal.otp;

/**
 * Текущий статус сценария повышения уровня авторизации с помощью OTP.
 */
public enum OtpStatus {

    /**
     * Ожидание подтверждения OTP после его отправки
     */
    OTP_REQUIRED,

    /**
     * OTP Успешно подтвержден. Выдан новый Principal.
     */
    SUCCESS,

    /**
     * Ошибка при отправке OTP.
     */
    SEND_OTP_FAIL,

    /**
     * Превышен лимит запроосов на отправку OTP
     */
    TOO_MANY_OTP,

    /**
     * Количество попыток ввода OTP исчерпано, повышение уровня заблокировано.
     */
    TOO_MANY_WRONG_CODE,

    /**
     * OTP отсутствует в запросе
     */
    OTP_MISSING,

    /**
     * Необработанная ошибка.
     */
    EXCEPTION
}
