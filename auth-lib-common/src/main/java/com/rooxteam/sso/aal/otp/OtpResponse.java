package com.rooxteam.sso.aal.otp;

import com.rooxteam.sso.aal.Principal;
import com.rooxteam.sso.aal.client.model.ResponseError;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Результат отправки одноразового пароля OTP.
 * При успешном подтверждении OTP будет сожержать Principal с повышенным уровнем авторизации.
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
     * @return Principal с повышенным уровнем авторизации. Заполнен в случае успешного подтверждения OTP.
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

    /**
     * @return Номер OTP кода
     */
    Long getOtpCodeNumber();

    /**
     * @return OTP method (SMS, IQDS, ...)
     */
    String getMethod();

    /**
     * @return Расширенные атрибуты при использовании кастомных реализаций функций OTP в UIDM
     */
    Map<String, Object> getExtendedAttributes();

    /**
     * @return Ошибки выполнения операции
     */
    List<ResponseError> getErrors();

}
