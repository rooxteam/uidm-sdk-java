package com.rooxteam.sso.aal;

import com.rooxteam.sso.aal.client.model.EvaluationResponse;
import com.rooxteam.sso.aal.otp.OtpFlowState;
import com.rooxteam.sso.aal.otp.OtpResponse;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Библиотека, представляющая модель аутентификации и авторизации.
 */
public interface AuthenticationAuthorizationLibrary extends AutoCloseable {

    /**
     * Аутентифицировать по набору параметров и вернуть Principal.
     * Набор параметров представлен словарём, потому что аутентификация может производится по различным факторам – пара username/password, IP-адрес, OpenID и т.д.
     * Эти параметры должны передаваться в движок оркестрации OpenAM, который по имеющимся параметрам собирает Identity-параметры из бэкэнда (из PCRF, например).
     * Если по этим параметрам аутентификация уже была пройдена, то повторный вызов authenticate не будет обращаться в OpenAM, а вернёт принципала из кеша.
     * Чтобы переаутентифицировать запрос, нужно сначала вызвать invalidate, потом authenticate
     *
     * @param params   параметры аутентификации. Для аутентификации по IP-адресу необходимо передать параметр "ip",
     *                 для аутентификации по ранее выданному JWT необходимо передать параметр "jwt".
     *                 Опционально передать список клиентских IP-адресов в параметре clientIps (формат, IP-адреса в dot-представлении разделенные между собой запятой).
     * @param timeOut  таймаут выполнения.
     * @param timeUnit единица измерения таймаута.
     * @return Principal с заполненными атрибутами при успешной аутентификации
     * @throws IllegalArgumentException Если параметр {@code params} не содержит параметер "ip"
     * @throws com.rooxteam.sso.aal.exception.AuthenticationException В случае неуспеха аутентификации
     */
    @Deprecated
    Principal authenticate(Map<String, ?> params, long timeOut, TimeUnit timeUnit);

    /**
     * Аутентифицировать по набору параметров и вернуть Principal.
     * Набор параметров представлен словарём, потому что аутентификация может производится по различным факторам – пара username/password, IP-адрес, OpenID и т.д.
     * Эти параметры должны передаваться в движок оркестрации OpenAM, который по имеющимся параметрам собирает Identity-параметры из бэкэнда (из PCRF, например).
     * Если по этим параметрам аутентификация уже была пройдена, то повторный вызов authenticate не будет обращаться в OpenAM, а вернёт принципала из кеша.
     * Чтобы переаутентифицировать запрос, нужно сначала вызвать invalidate, потом authenticate
     *
     * @param params параметры аутентификации. Для аутентификации по IP-адресу необходимо передать параметр "ip",
     *               для аутентификации по ранее выданному JWT необходимо передать параметр "jwt".
     *               Опционально передать список клиентских IP-адресов в параметре clientIps (формат, IP-адреса в dot-представлении разделенные между собой запятой).
     * @return Principal с заполненными атрибутами при успешной аутентификации
     * @throws IllegalArgumentException Если параметр {@code params} не содержит параметр "ip"
     * @throws com.rooxteam.sso.aal.exception.AuthenticationException В случае неуспеха аутентификации
     */
    Principal authenticate(Map<String, ?> params);

    /**
     * Запросить у OpenAM обновление сведений о принципале.
     * Если updateLifeTime = true, то обновить ещё и expirationTime с соответствующим перевыпуском JWT-токена
     *
     * @param principal      Principal полученный ранее при аутентификации либо обновлении.
     * @param updateLifeTime требуется ли обновить время истечения действия токена.
     * @param timeOut        таймаут выполнения.
     * @param timeUnit       единица измерения таймаута.
     * @return обновленный Principal
     */
    @Deprecated
    Principal renew(Principal principal, boolean updateLifeTime, long timeOut, TimeUnit timeUnit);

    /**
     * Запросить у OpenAM обновление сведений о принципале.
     * Если updateLifeTime = true, то обновить ещё и expirationTime с соответствующим перевыпуском JWT-токена
     *
     * @param principal      Principal полученный ранее при аутентификации либо обновлении.
     * @param updateLifeTime требуется ли обновить время истечения действия токена.
     * @return обновленный Principal
     */
    Principal renew(Principal principal, boolean updateLifeTime);

    /**
     * Объявить сведения о принципале недействительными и удалить их из кеша.
     *
     * @param principal Principal который должен быть объявлен недействительным.
     * @throws IllegalArgumentException Если параметр {@code principal} равен null
     */
    void invalidate(Principal principal);

    /**
     * Объявить все закешированные принципалы недействительными.
     */
    void invalidate();

    /**
     * Объявить сведения о принципале с заданным IMSI не действительными и удалить их из кеша.
     *
     * @param imsi IMSI абонента, принципал которого требуется объявить недействительным.
     */
    void invalidateByImsi(String imsi);

    /**
     * Разрешено ли выполнение действия (actionName) над ресурсом (actionName) в контексте (envParameters).
     *
     * @param subject       Principal для которого запрашивается доступ
     * @param resourceName  имя запрашиваемого ресурса
     * @param actionName    имя дейтсвия
     * @param envParameters контекст
     * @param timeOut       таймаут выполнения.
     * @param timeUnit      единица измерения таймаута.
     * @return true если действие разрешено, false если не разрешено.
     * @throws IllegalArgumentException Если параметры {@code subject}, {@code resourceName} или {@code actionName} равны null
     * @deprecated Use evaluatePolicy instead.
     */
    @Deprecated
    boolean isAllowed(Principal subject, String resourceName, String actionName, Map<String, ?> envParameters, long timeOut, TimeUnit timeUnit);

    /**
     * Разрешено ли выполнение действия (actionName) над ресурсом (actionName) в контексте (envParameters).
     *
     * @param subject       Principal для которого запрашивается доступ
     * @param resourceName  имя запрашиваемого ресурса
     * @param actionName    имя дейтсвия
     * @param envParameters контекст
     * @return true если действие разрешено, false если не разрешено.
     * @throws IllegalArgumentException Если параметры {@code subject}, {@code resourceName} или {@code actionName} равны null
     * @deprecated Use evaluatePolicy instead.
     */
    @Deprecated
    boolean isAllowed(Principal subject, String resourceName, String actionName, Map<String, ?> envParameters);

    /**
     * Вычислить политики относительно действий (actionName) над ресурсом (actionName) в контексте (envParameters).
     *
     * @param subject       Principal для которого запрашивается доступ
     * @param resourceName  имя запрашиваемого ресурса
     * @param actionName    имя дейтсвия
     * @param envParameters контекст
     * @return ответ о возможности выполнения указанных действий, а также список advices, который может содержать причины отказа.
     * @throws IllegalArgumentException Если параметры {@code subject}, {@code resourceName} или {@code actionName} равны null
     */
    EvaluationResponse evaluatePolicy(Principal subject, String resourceName, String actionName, Map<String, ?> envParameters);

    /**
     * Сбросить все авторизационные решения в кеше.
     */
    void resetPolicies();

    /**
     * Сбросить авторизационные решения в кеше для указанного Principal.
     *
     * @param principal Principal для сброса авторизационных решений.
     */
    void resetPolicies(Principal principal);

    /**
     * Преобразовать JWT токен в принципал. Созданный принципал будет иметь только свойства из скоупа Shared Identity Params.
     * @deprecated Используйте метод authenticate вместо локальной проверки.
     * @param jwt JWT токен для создания принципала
     * @return Principal с атрибутами из JWT
     */
    @Deprecated
    Principal parseToken(String jwt);

    /**
     * Проверить корректность токена без обращения к SSO.
     * @deprecated Используйте метод authenticate вместо локальной проверки.
     * @param jwt JWT токен для проверки
     * @return статус проверки
     * @throws com.rooxteam.sso.aal.exception.AalException Может сождержать в себе причины (cause):
     *                                                     {@link java.security.NoSuchAlgorithmException},
     *                                                     {@link java.security.spec.InvalidKeySpecException},
     *                                                     {@link com.nimbusds.jose.JOSEException},
     *                                                     {@link java.lang.RuntimeException}
     * @throws java.lang.IllegalArgumentException Если {@code jwt} равен null
     * @throws java.lang.IllegalStateException Если параметры iat, nbf и другие с неправильными значениями
     */
    Principal validate(String jwt);

    /**
     * Зарегистрировать PrincipalEventListener для обработки событий Principal.
     *
     * @param listener PrincipalEventListener для обработки событий.
     */
    void addPrincipalListener(PrincipalEventListener listener);

    /**
     * Удалить ранее зарегистрированный PrincipalEventListener.
     *
     * @param listener ранее зарегистрированный PrincipalEventListener.
     */
    void removePrincipalListener(PrincipalEventListener listener);

    /**
     * Включен ли поллинг для получения сведений о централизованно инвалидированных принципалах.
     *
     * @return true если поллинг включен, false если выключен.
     */
    boolean isPollingEnabled();

    /**
     * Включить поллинг.
     * Если поллинг уже включен, то сначала выполняется его выключение (disablePolling()).
     *
     * @param period период запуска операции проверки токенов
     * @param unit   единицы измерения периода операции проверки токенов
     */
    void enablePolling(int period, TimeUnit unit);

    /**
     * Выключить поллинг.
     */
    void disablePolling();

    /**
     * Запрос одноразового пароля (One-time password, OTP).
     *
     * @param principal Принципал, которому отправляется уведомление
     * @param timeOut   таймаут выполнения.
     * @param timeUnit  единица измерения таймаута.
     * @return Возвращает POJO, содержащую текущий шаг, состояние, форму отправки OTP, дополнительные параметры и результат аутентификации (Principal) в случае успеха
     * @throws IllegalArgumentException Если параметр {@code principal} равен null
     */
    @Deprecated
    OtpResponse sendOtp(Principal principal, long timeOut, TimeUnit timeUnit);

    /**
     * Запрос одноразового пароля (One-time password, OTP).
     *
     * @param principal Принципал, которому отправляется уведомление
     * @return Возвращает POJO, содержащую текущий шаг, состояние, форму отправки OTP, дополнительные параметры и результат аутентификации (Principal) в случае успеха
     * @throws IllegalArgumentException Если параметр {@code principal} равен null
     */
    OtpResponse sendOtp(Principal principal);

    /**
     * Повторный запрос OTP.
     *
     * @param otpFlowState Объект представляющий текущее состояние сценария повышения уровня авторизации.
     * @param timeOut      таймаут выполнения.
     * @param timeUnit     единица измерения таймаута.
     * @return OtpResponse POJO, содержащую текущий шаг, состояние, форму отправки OTP,
     * дополнительные параметры и результат аутентификации (Principal) в случае успеха
     */
    @Deprecated
    OtpResponse resendOtp(OtpFlowState otpFlowState, long timeOut, TimeUnit timeUnit);

    /**
     * Повторный запрос OTP.
     *
     * @param otpFlowState Объект представляющий текущее состояние сценария повышения уровня авторизации.
     * @return OtpResponse POJO, содержащую текущий шаг, состояние, форму отправки OTP,
     * дополнительные параметры и результат аутентификации (Principal) в случае успеха
     */
    OtpResponse resendOtp(OtpFlowState otpFlowState);

    /**
     * Владиция OTP
     *
     * @param otpState Состояние запроса
     * @param fields   Заполненные поля
     * @param timeOut  таймаут выполнения.
     * @param timeUnit единица измерения таймаута.
     * @return Возвращает результат валидации: или форму для заполнения (не успешная валидация) или принципала (успешная валидация)
     */
    @Deprecated
    OtpResponse validateOtp(OtpFlowState otpState, Map<String, String> fields, long timeOut, TimeUnit timeUnit);

    /**
     * Владиция OTP
     *
     * @param otpState Состояние запроса
     * @param fields   Заполненные поля
     * @return Возвращает результат валидации: или форму для заполнения (не успешная валидация) или принципала (успешная валидация)
     * @deprecated Необходимо использовать версию, принимающую код напрямую
     */
    @Deprecated
    OtpResponse validateOtp(OtpFlowState otpState, Map<String, String> fields);

    /**
     * Владиция OTP
     *
     * @param otpState Состояние запроса
     * @param otpCode   Введенный пользователем код
     * @return Возвращает результат валидации: или форму для заполнения (не успешная валидация) или принципала (успешная валидация)
     */
    OtpResponse validateOtp(OtpFlowState otpState, String otpCode);

}
