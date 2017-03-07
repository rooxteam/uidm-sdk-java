package com.rooxteam.sso.aal.otp;

import lombok.Getter;
import lombok.experimental.Builder;

/**
 * Параметры для валидации OTP
 */
@Getter
@Builder
public class ValidateOtpParameter {

    /**
     * Объект представляющий текущее состояние сценария повышения уровня авторизации.
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

}
