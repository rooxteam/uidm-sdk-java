package com.rooxteam.sso.aal.otp;

import lombok.Getter;
import lombok.Builder;
import org.springframework.http.HttpInputMessage;

/**
 * Параметры для валидации OTP
 */
@Getter
@Builder
public class ValidateOtpParameter {

    /**
     * Объект представляющий текущее состояние сценария OTP.
     */
    private OtpFlowState otpFlowState;
    /**
     * Введенный пользователем код
     */
    private String otpCode;
    /**
     * Номер телефона, по которому отправляется уведомление
     */
    private String msisdn;
    /**
     * Имя сценария в Customer SSO для AAL
     */
    private String service;
    /**
     * Реалм в котором зарегистрирован пользователь (из токена)
     */
    private String realm;

    /**
     * Исходный HTTP-запрос (тело и заголовки)
     */
    private HttpInputMessage inputMessage;

}
