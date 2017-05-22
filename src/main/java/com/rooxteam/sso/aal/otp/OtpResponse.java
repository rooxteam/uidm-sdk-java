package com.rooxteam.sso.aal.otp;

import com.rooxteam.sso.aal.Principal;

import java.util.Set;

/**
 * Результат отправки одноразового пароля OTP.
 * При успешном подтверждении OTP будет сожержать YotaPrincipal с повышенным уровнем авторизации.
 * Сразу после отправки OTP или при неуспешном подтверждении будет содержать информацию
 * о необходимых для отправки параметров.
 */
public interface OtpResponse {

    /**
     * @return Текущий статус сценария OTP.
     */
    OtpStatus getStatus();

    /**
     * @return Текущее состояние запроса. Должно быть передано в следующем запросе сценария OTP.
     */
    OtpFlowState getOtpFlowState();

    /**
     * @return Имена параметров которые необходимо передать в следующем запросе сценария OTP.
     */
    Set<String> getRequiredFieldNames();

    /**
     * @return Количество оставшихся попыток ввода OTP.
     */
    Integer getAvailableAttempts();

    /**
     * @return YotaPrincipal с повышенным уровнем авторизации. Заполнен в случае успешного подтверждения OTP.
     */
    Principal getPrincipal();

    /**
     * @return Интервал времени в секундах, на который произошла блокирова возможности отправки OTP для данного принципала.
     */
    Long getBlockedFor();

    /**
     * @return Время в секундах через которой можно будет заказать OTP под операцию.`
     */
    Long getNextOtpCodeOperationPeriod();
}
