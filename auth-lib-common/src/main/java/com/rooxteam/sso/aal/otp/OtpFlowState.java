package com.rooxteam.sso.aal.otp;

/**
 * Объект представляющий текущее состояние сценария повышения уровня авторизации.
 */
public interface OtpFlowState {

    /**
     * @return Текущая фаза сценария.
     */
    String getExecution();

    /**
     * В текущей версии не используется.
     * @return CSRF токен.
     */
    String getCsrf();

    /**
     * @return URL текущего сценария.
     */
    String getServerUrl();

    /**
     * @return Идентификатор сессии.
     */
    String getSessionId();
}
