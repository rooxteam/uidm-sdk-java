package com.rooxteam.sso.clientcredentials;

import org.jboss.logging.Logger;
import org.jboss.logging.annotations.Cause;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;
import org.jboss.logging.annotations.ValidIdRange;
import org.jboss.logging.annotations.ValidIdRanges;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;

import static org.jboss.logging.Logger.Level.ERROR;
import static org.jboss.logging.Logger.Level.TRACE;
import static org.jboss.logging.annotations.Message.Format.MESSAGE_FORMAT;

/**
 * Logger used by package.
 * Logs over any well known logging library: slf4j, log4j2, jul.
 * Default logger name is 'com.rooxteam.sso.clientcredentials.ClientCredentialsClientLogger'
 */
@MessageLogger(projectCode = "RX_OAUTH_CLCRD", length = 4)
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
interface ClientCredentialsClientLogger {

    ClientCredentialsClientLogger LOG = Logger.getMessageLogger(ClientCredentialsClientLogger.class, ClientCredentialsClientLogger.class.getPackage().getName());

    @LogMessage(level = ERROR)
    @Message(id = 3001, format = MESSAGE_FORMAT,
            value = "An error during getting new token. {0}")
    void errorOnGetToken(MultiValueMap<String, String> params, @Cause Exception e);

    @LogMessage(level = ERROR)
    @Message(id = 3002, format = MESSAGE_FORMAT,
            value = "An error during validating token. {0}")
    void errorOnValidatingToken(String token, @Cause RestClientException e);


    @LogMessage(level = TRACE)
    @Message(id = 9001, format = MESSAGE_FORMAT,
            value = "Requesting new token from server. {0}")
    void traceRequestNewToken(MultiValueMap<String, String> params);

    @LogMessage(level = TRACE)
    @Message(id = 9002, format = MESSAGE_FORMAT,
            value = "Got new token. {0}. Token: {1}")
    void traceGotToken(MultiValueMap<String, String> params, String token);

    @LogMessage(level = TRACE)
    @Message(id = 9003, format = MESSAGE_FORMAT,
            value = "Put token into store. {0}. Token: {1}")
    void tracePutTokenInStore(MultiValueMap<String, String> params, String token);

    @LogMessage(level = TRACE)
    @Message(id = 9004, format = MESSAGE_FORMAT,
            value = "Got valid token into store. {0}. Token: {1}")
    void traceGotTokenFromStore(MultiValueMap<String, String> params, String token);

    @LogMessage(level = TRACE)
    @Message(id = 9005, format = MESSAGE_FORMAT,
            value = "Removed token from store. {0}.")
    void traceRemovedTokenFromStore(MultiValueMap<String, String> params);

    @LogMessage(level = TRACE)
    @Message(id = 9006, format = MESSAGE_FORMAT,
            value = "No previous token in store. {0}.")
    void traceNoTokenInStore(MultiValueMap<String, String> params);

    @LogMessage(level = TRACE)
    @Message(id = 9007, format = MESSAGE_FORMAT,
            value = "Previous token expired. {0}. Token: {1}")
    void traceTokenExpired(MultiValueMap<String, String> params, String token);

    @LogMessage(level = TRACE)
    @Message(id = 9008, format = MESSAGE_FORMAT,
            value = "Application requested token (from cache or new). {0}")
    void traceGetToken(MultiValueMap<String, String> params);

    @LogMessage(level = TRACE)
    @Message(id = 9009, format = MESSAGE_FORMAT,
            value = "Got 401 response code, meaning token expired. {0}")
    void debugOnValidatingTokenTokenExpired(String token);

    @LogMessage(level = TRACE)
    @Message(id = 9010, format = MESSAGE_FORMAT,
            value = "Got 403 response code, meaning token not valid, but not expired. {0}")
    void debugOnValidatingTokenTokenForbidden(String tokenForLogging);
}
