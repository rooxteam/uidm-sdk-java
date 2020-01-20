package com.rooxteam.sso.clientcredentials;

import org.jboss.logging.Logger;
import org.jboss.logging.annotations.Cause;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;
import org.jboss.logging.annotations.ValidIdRange;
import org.jboss.logging.annotations.ValidIdRanges;
import org.springframework.http.HttpStatus;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;

import java.net.URI;

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
            value = "An error during getting new token. {0} {1}")
    void errorOnGetToken(final URI accessTokenEndpoint,
                         final MultiValueMap<String, String> params,
                         final @Cause Exception e);

    @LogMessage(level = ERROR)
    @Message(id = 3002, format = MESSAGE_FORMAT,
            value = "HTTP error during getting new token. {0} {1} {2} {3}")
    void errorOnGetTokenHttp(final URI accessTokenEndpoint,
                             final MultiValueMap<String, String> paramsForLogging,
                             final HttpStatus statusCode,
                             final String trimmedBody,
                             final @Cause HttpStatusCodeException e);

    @LogMessage(level = ERROR)
    @Message(id = 3003, format = MESSAGE_FORMAT,
            value = "IO error during getting new token. " +
                    "URL: {0}, " +
                    "Params: {1}." +
                    "You can tune I/O timeouts if current are too low and service or network are not able to serve requests." +
                    "Connect timeout (affects connection establishment phase) `{2}` = {3} ms." +
                    "Read timeout (affects waiting for each inbound packet) `{4}` = {5} ms.")
    void errorOnGetTokenIO(final URI accessTokenEndpoint,
                           final MultiValueMap<String, String> paramsForLogging,
                           final String connectTimeoutKey,
                           final int connectTimeoutValue,
                           final String readTimeoutKey,
                           final int readTimeoutValue,
                           final @Cause ResourceAccessException e);

    @LogMessage(level = ERROR)
    @Message(id = 3010, format = MESSAGE_FORMAT,
            value = "An error during validating token. {0} {1}")
    void errorOnValidatingToken(final URI accessTokenEndpoint,
                                final String token,
                                final @Cause Exception e);

    @LogMessage(level = ERROR)
    @Message(id = 3011, format = MESSAGE_FORMAT,
            value = "HTTP error during validating token. {0} {1} {2} {3}")
    void errorOnValidatingTokenHttp(final URI accessTokenEndpoint,
                                    final String token,
                                    final HttpStatus statusCode,
                                    final String trimmedBody,
                                    final @Cause Exception e);

    @LogMessage(level = ERROR)
    @Message(id = 3013, format = MESSAGE_FORMAT,
            value = "IO error during validating token. " +
                    "URL: {0}, " +
                    "Token: {1}." +
                    "You can tune I/O timeouts if current are too low and service or network are not able to serve requests." +
                    "Connect timeout (affects connection establishment phase) `{2}` = {3} ms." +
                    "Read timeout (affects waiting for each inbound packet) `{4}` = {5} ms.")
    void errorOnValidatingTokenIO(final URI accessTokenEndpoint,
                                  final String token,
                           final String connectTimeoutKey,
                           final int connectTimeoutValue,
                           final String readTimeoutKey,
                           final int readTimeoutValue,
                           final @Cause ResourceAccessException e);

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
    void traceOnValidatingTokenTokenExpired(String tokenForLogging);

    @LogMessage(level = TRACE)
    @Message(id = 9010, format = MESSAGE_FORMAT,
            value = "Got 403 response code, meaning token not valid, but not expired. {0}")
    void traceOnValidatingTokenTokenForbidden(String tokenForLogging);

}
