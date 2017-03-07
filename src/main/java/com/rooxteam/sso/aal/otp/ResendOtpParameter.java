package com.rooxteam.sso.aal.otp;

import lombok.Getter;
import lombok.experimental.Builder;

/**
 * Параметры для повторного запроса OTP.
 */
@Getter
@Builder
public class ResendOtpParameter {

    /**
     * Объект представляющий текущее состояние сценария повышения уровня авторизации.
     */
    private OtpFlowState otpFlowState;
    /**
     * Имя сценария в Customer SSO для AAL
     */
    private String service;

}
