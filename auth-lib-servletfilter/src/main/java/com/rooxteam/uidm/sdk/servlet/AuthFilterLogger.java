package com.rooxteam.uidm.sdk.servlet;

import org.jboss.logging.BasicLogger;
import org.jboss.logging.Logger;
import org.jboss.logging.annotations.*;

import org.jboss.logging.Logger.Level;
import org.jboss.logging.annotations.Message.Format;

@MessageLogger(projectCode = "RX_UIDM_AUTH_FILTER_", length = 4)
@ValidIdRanges({
        // FATAL
        @ValidIdRange(min = 1, max = 999),
        // CRITICAL
        @ValidIdRange(min = 2001, max = 2999),
        // ERROR
        @ValidIdRange(min = 3001, max = 3999),
        // WARN
        @ValidIdRange(min = 4001, max = 4999),
        // INFO
        @ValidIdRange(min = 6001, max = 6999),
        // DEBUG
        @ValidIdRange(min = 7001, max = 7999),
        // TRACE
        @ValidIdRange(min = 9001, max = 9999)
})
public interface AuthFilterLogger extends BasicLogger {

    AuthFilterLogger LOG = Logger.getMessageLogger(AuthFilterLogger.class, AuthFilterLogger.class.getPackage().getName());

    @LogMessage(level = Level.ERROR)
    @Message(id = 3001, format = Format.MESSAGE_FORMAT,
            value = "Authentication by access token failed. Token: {0}.")
    void errorAuthentication(String token, @Cause Exception e);

    @LogMessage(level = Level.INFO)
    @Message(id = 6001, format = Format.MESSAGE_FORMAT,
            value = "The user with access token is being redirected to authentication endpoint due to authentication failure. Token: {0}")
    void infoRedirectDueToBadToken(String token);

    @LogMessage(level = Level.INFO)
    @Message(id = 6002, format = Format.MESSAGE_FORMAT,
            value = "The user with access token has been successfully authenticated. Token claims have been placed in http request. Token: {0}")
    void infoSuccessAuthentication(String token);


    @LogMessage(level = Level.INFO)
    @Message(id = 6003, format = Format.MESSAGE_FORMAT,
            value = "The user from remote address {0} is being redirected to authentication endpoint as no access token has been provided.")
    void infoRedirectDueToNoToken(String addr);
}
