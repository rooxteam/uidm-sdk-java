package com.rooxteam.sso.aal;

import com.nimbusds.jwt.JWT;
import com.rooxteam.compat.AutoCloseable;
import com.rooxteam.sso.aal.client.EvaluationContext;
import com.rooxteam.sso.aal.client.model.EvaluationRequest;
import com.rooxteam.sso.aal.client.model.EvaluationResponse;
import com.rooxteam.sso.aal.configuration.Configuration;
import com.rooxteam.sso.aal.otp.OtpFlowState;
import com.rooxteam.sso.aal.otp.OtpResponse;
import com.rooxteam.sso.aal.otp.ResendOtpParameter;
import com.rooxteam.sso.aal.otp.SendOtpParameter;
import com.rooxteam.sso.aal.otp.ValidateOtpParameter;
import com.rooxteam.sso.aal.validation.jwt.ValidationResult;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Библиотека, представляющая модель аутентификации и авторизации.
 */
public interface AuthenticationAuthorizationLibrary extends AutoCloseable {


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
     * Вычислить политики относительно действий (actionName) над ресурсом (actionName) в контексте (envParameters).
     *
     * @param subject         Principal для которого запрашивается доступ
     * @param policiesToCheck политики для вычисления
     * @return ответ о возможности выполнения указанных действий, а также список advices, который может содержать причины отказа.
     * @throws IllegalArgumentException Если параметр {@code policiesToCheck} равен null
     */
    Map<EvaluationRequest, EvaluationResponse> evaluatePolicies(Principal subject, List<EvaluationRequest> policiesToCheck);

    /**
     * Обработать ответ политикой. Параметры те же самые, что и у вычисления политики (actionName) над ресурсом (actionName) в контексте (envParameters).
     *
     * @param subject       Principal для которого запрашивается доступ
     * @param resourceName  имя запрашиваемого ресурса
     * @param actionName    имя действия
     * @param envParameters контекст
     * @return отфильтрованнный политикой ответ.
     * @throws IllegalArgumentException Если параметры {@code subject}, {@code resourceName} или {@code actionName} равны null
     */
    String postprocessPolicy(Principal subject, String resourceName, String actionName, Map<String, ?> envParameters, String response);

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
     * Метод будет заменен - getPreAuthenticatedUserPrincipal(HttpServletRequest request, String accessToken)
     */
    @Deprecated
    Principal validate(HttpServletRequest request, String accessToken);

    /**
     * Проверить валидность токена и получить Principal
     *
     * @param accessToken токен для проверки
     * @return Principal данные о пользователе
     * @throws com.rooxteam.sso.aal.exception.AalException Может содержать в себе причины (cause):
     *                                                     {@link java.security.NoSuchAlgorithmException},
     *                                                     {@link java.security.spec.InvalidKeySpecException},
     *                                                     {@link com.nimbusds.jose.JOSEException},
     *                                                     {@link java.lang.RuntimeException}
     * @throws java.lang.IllegalArgumentException          Если {@code jwt} равен null
     * @throws java.lang.IllegalStateException             Если параметры iat, nbf и другие с неправильными значениями*
     */
    Principal getPreAuthenticatedUserPrincipal(HttpServletRequest request, String accessToken);

    /**
     * Проверить валидность токена
     *
     * @param jwt токен для проверки
     * @return Результат валидации
     * @throws com.rooxteam.sso.aal.exception.AalException Может содержать в себе причины (cause):
     *                                                     {@link java.security.NoSuchAlgorithmException},
     *                                                     {@link java.security.spec.InvalidKeySpecException},
     *                                                     {@link com.nimbusds.jose.JOSEException},
     *                                                     {@link java.lang.RuntimeException}
     * @throws java.lang.IllegalArgumentException          Если {@code jwt} равен null
     * @throws java.lang.IllegalStateException             Если параметры iat, nbf и другие с неправильными значениями*
     */
    ValidationResult validateJWT(JWT jwt);

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
     * Запрос одноразового пароля (One-time password, OTP).
     *
     * @param principal Принципал, которому отправляется уведомление
     * @param context   Контекст операции, для которой требуется выдать токен по результатам OTP
     * @return Возвращает POJO, содержащую текущий шаг, состояние, форму отправки OTP, дополнительные параметры и результат аутентификации (Principal) в случае успеха
     */
    OtpResponse sendOtpForOperation(Principal principal, EvaluationContext context);

    /**
     * Запрос одноразового пароля (One-time password, OTP).
     *
     * @param sendOtpParameter параметры для отправки уведомления
     * @return Возвращает POJO, содержащую текущий шаг, состояние, форму отправки OTP, дополнительные параметры и результат аутентификации (Principal) в случае успеха
     */
    OtpResponse sendOtpForOperation(SendOtpParameter sendOtpParameter);

    /**
     * Повторный запрос OTP.
     *
     * @param otpFlowState Объект представляющий текущее состояние сценария OTP.
     * @param timeOut      таймаут выполнения.
     * @param timeUnit     единица измерения таймаута.
     * @return OtpResponse POJO, содержащую текущий шаг, состояние, форму отправки OTP,
     *         дополнительные параметры и результат аутентификации (Principal) в случае успеха
     */
    @Deprecated
    OtpResponse resendOtp(OtpFlowState otpFlowState, long timeOut, TimeUnit timeUnit);

    /**
     * Повторный запрос OTP.
     * Будет использоваться реалм заданный конфигом
     *
     * @param otpFlowState Объект представляющий текущее состояние сценария OTP.
     * @return OtpResponse POJO, содержащую текущий шаг, состояние, форму отправки OTP,
     * дополнительные параметры и результат аутентификации (Principal) в случае успеха
     * @deprecated в пользу метода с передачей реалма
     * {@link AuthenticationAuthorizationLibrary#resendOtp(String realm, OtpFlowState otpFlowState)}
     */
    @Deprecated
    OtpResponse resendOtp(OtpFlowState otpFlowState);

    /**
     * Повторный запрос OTP.
     *
     * @param realm Реалм в котором зарегистрирован пользователь (определяется из токена), если null - определяется из конфига
     * @param otpFlowState Объект представляющий текущее состояние сценария OTP.
     * @return OtpResponse POJO, содержащую текущий шаг, состояние, форму отправки OTP,
     * дополнительные параметры и результат аутентификации (Principal) в случае успеха
     */
    OtpResponse resendOtp(String realm, OtpFlowState otpFlowState);

    /**
     * Повторный запрос OTP.
     *
     * @param resendOtpParameter Параметры для повторного запроса OTP.
     * @return OtpResponse POJO, содержащую текущий шаг, состояние, форму отправки OTP,
     *         дополнительные параметры и результат аутентификации (Principal) в случае успеха
     */
    OtpResponse resendOtp(ResendOtpParameter resendOtpParameter);

    /**
     * Валидация OTP
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
     * Валидация OTP
     *
     * @param otpState Состояние запроса
     * @param fields   Заполненные поля
     * @return Возвращает результат валидации: или форму для заполнения (не успешная валидация) или принципала (успешная валидация)
     * @deprecated Необходимо использовать версию, принимающую код напрямую
     */
    @Deprecated
    OtpResponse validateOtp(OtpFlowState otpState, Map<String, String> fields);

    /**
     * Валидация OTP
     * Будет использоваться реалм заданный конфигом
     *
     * @param otpState Состояние запроса
     * @param otpCode  Введенный пользователем код
     * @return Возвращает результат валидации: или форму для заполнения (не успешная валидация) или принципала (успешная валидация)
     * @deprecated в пользу метода с передачей реалма
     * {@link AuthenticationAuthorizationLibrary#validateOtp(String realm, OtpFlowState otpState, String otpCode)}
     */
    @Deprecated
    OtpResponse validateOtp(OtpFlowState otpState, String otpCode);

    /**
     * Валидация OTP
     *
     * @param realm Реалм в котором зарегистрирован пользователь (определяется из токена), если null - определяется из конфига
     * @param otpState Состояние запроса
     * @param otpCode  Введенный пользователем код
     * @return Возвращает результат валидации: или форму для заполнения (не успешная валидация) или принципала (успешная валидация)
     */
    OtpResponse validateOtp(String realm, OtpFlowState otpState, String otpCode);

    /**
     * Валидация OTP
     *
     * @param validateOtpParameter Параметры для валидации OTP
     * @return Возвращает результат валидации: или форму для заполнения (не успешная валидация) или принципала (успешная валидация)
     */
    OtpResponse validateOtp(ValidateOtpParameter validateOtpParameter);


    /**
     * Получить обьект конфигурации, с помощью которой SDK была сконфигурирована
     * @return объект конфигурации
     */
    Configuration getConfiguration();
}
