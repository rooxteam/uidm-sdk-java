package com.rooxteam.sso.aal.otp;

import com.rooxteam.sso.aal.client.EvaluationContext;
import lombok.Getter;
import lombok.experimental.Builder;

/**
 * Параметры для отправки OTP
 */
@Getter
@Builder
public class SendOtpParameter {
    /**
     * jwt token принципала, которому отправляется уведомление
     */
    private String jwt;
    /**
     * Номер телефона, по которому отправляется уведомление
     */
    private String msisdn;
    /**
     * Категория OTP. Позволяет кастомизировать шаблон, транспорт и т. д.
     */
    private String category;
    /**
     * Имя сценария в Customer SSO для AAL
     */
    private String service;
    /**
     * Контекст операции, для которой требуется выдать токен по результатам OTP
     */
    private EvaluationContext evaluationContext;
}
