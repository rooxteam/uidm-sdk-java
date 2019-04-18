package com.rooxteam.uidm.sdk.spring;

import org.jboss.logging.BasicLogger;
import org.jboss.logging.Logger;
import org.jboss.logging.annotations.Cause;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;
import org.jboss.logging.annotations.ValidIdRange;
import org.jboss.logging.annotations.ValidIdRanges;

import static org.jboss.logging.Logger.Level.ERROR;
import static org.jboss.logging.annotations.Message.Format.MESSAGE_FORMAT;

@MessageLogger(projectCode = "RX_JWT_SEC____", length = 4)
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
public interface JwtSecurityPluginLogger extends BasicLogger {

    /**
     * The default logger named 'com.rooxteam.webapi'.
     */
    JwtSecurityPluginLogger LOG = Logger.getMessageLogger(JwtSecurityPluginLogger.class, JwtSecurityPluginLogger.class.getPackage().getName());

    @LogMessage(level = ERROR)
    @Message(id = 3001, format = MESSAGE_FORMAT,
            value = "Authentication login exception. For details see cause.")
    void errorAuthLogin(@Cause Exception e);

    @LogMessage(level = ERROR)
    @Message(id = 3002, format = MESSAGE_FORMAT,
            value = "Localization exception. For details see cause.")
    void errorI10n(@Cause Exception l10NMessage);

    @LogMessage(level = ERROR)
    @Message(id = 3003, format = MESSAGE_FORMAT,
            value = "Login exception. For details see cause.")
    void errorLogin(@Cause Exception loginException);

    @LogMessage(level = ERROR)
    @Message(id = 3004, format = MESSAGE_FORMAT,
            value = "Exception during getting policy evaluator for service {0}. For details see cause.")
    void errorCreateEvaluator(String webAgentServiceName, @Cause Exception e);

    @LogMessage(level = Logger.Level.WARN)
    @Message(id = 4001, format = Message.Format.MESSAGE_FORMAT, value = "Token is not valid.\nToken: ''{0}''")
    void logWarnTokenNotValid(String e);

    @LogMessage(level = Logger.Level.WARN)
    @Message(id = 4002, format = Message.Format.MESSAGE_FORMAT, value = "Token is not valid or attributes cannot be retrieved.\nToken: ''{0}''")
    void logWarnTokenNotValidOrNotRetriveAttr(String e);

    @LogMessage(level = Logger.Level.WARN)
    @Message(id = 4003, format = Message.Format.MESSAGE_FORMAT, value = "Empty token so attributes cannot be received'")
    void logWarnTokenIsEmpty();

    @LogMessage(level = Logger.Level.WARN)
    @Message(id = 4005, format = Message.Format.MESSAGE_FORMAT, value = "Attributes ''{0}'' cannot be received'")
    void logWarnUnableRetriveAttr(String key);
}
