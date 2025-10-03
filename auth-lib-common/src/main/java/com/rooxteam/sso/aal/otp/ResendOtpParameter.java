package com.rooxteam.sso.aal.otp;

import lombok.Getter;
import lombok.Builder;

/**
 * Параметры для повторного запроса OTP.
 */
@Getter
@Builder
public class ResendOtpParameter {

    /**
     * Объект представляющий текущее состояние сценария OTP.
     */
    private OtpFlowState otpFlowState;
    /**
     * Имя сценария в Customer SSO для AAL
     */
    private String service;
    /**
     * Реалм в котором зарегистрирован пользователь (из токена)
     */
    private String realm;
}
