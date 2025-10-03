package com.rooxteam.sso.aal.configuration;

import org.jboss.logging.BasicLogger;
import org.jboss.logging.Logger;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;
import org.jboss.logging.annotations.ValidIdRange;
import org.jboss.logging.annotations.ValidIdRanges;


@MessageLogger(projectCode = "RX_AAL_CFG____", length = 4)
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
public interface AalConfigLogger extends BasicLogger {

//    AalConfigLogger LOG = Logger.getMessageLogger(MethodHandles.lookup(), AalConfigLogger.class, AalConfigLogger.class.getPackage().getName());
    AalConfigLogger LOG = Logger.getMessageLogger(AalConfigLogger.class, AalConfigLogger.class.getPackage().getName());

    @LogMessage(level = Logger.Level.DEBUG)
    @Message(id = 3001, format = Message.Format.MESSAGE_FORMAT,
            value = "RealmBasedConfiguration has issues in realm format, realm = ''{0}''")
    void errorRealmConfigFormat(String realm);

    @LogMessage(level = Logger.Level.DEBUG)
    @Message(id = 7001, format = Message.Format.MESSAGE_FORMAT,
            value = "Property replaced from ''{0}'' to ''{1}''")
    void debugPropertyReplaced(String property, String newProperty);

    @LogMessage(level = Logger.Level.DEBUG)
    @Message(id = 7002, format = Message.Format.MESSAGE_FORMAT,
            value = "Property has no AAL prefix: ''{0}''. Keeping the same.")
    void debugPropertyNoPrefix(String property);

    @LogMessage(level = Logger.Level.DEBUG)
    @Message(id = 7003, format = Message.Format.MESSAGE_FORMAT,
            value = "Returning value for realm property ''{0}''")
    void debugReturnReplacedPropertyValue(String audProperty);

    @LogMessage(level = Logger.Level.DEBUG)
    @Message(id = 7004, format = Message.Format.MESSAGE_FORMAT,
            value = "Value is absent for realm based property ''{0}''")
    void debugReplacedPropertyValueAbsent(String property);

    @LogMessage(level = Logger.Level.DEBUG)
    @Message(id = 7005, format = Message.Format.MESSAGE_FORMAT,
            value = "Returning value for original AAL property ''{0}''")
    void debugReturnOriginalPropertyValue(String property);
}
