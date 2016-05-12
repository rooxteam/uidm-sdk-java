package com.rooxteam.sso.aal.metrics;

import com.codahale.metrics.ObjectNameFactory;
import com.rooxteam.sso.aal.Principal;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.util.Hashtable;

/**
 * Задает специальный формат именования JMX MBeans.
 * <p/>
 * auth-lib-yota moved to common project
 *
 * @deprecated use {@link ObjectNameFactory} instead.
 */
public class YotaObjectNameFactory implements ObjectNameFactory {
    @Override
    public ObjectName createName(String type, String domain, String name) {
        try {
            Hashtable<String, String> params = new Hashtable<>();
            params.put("type", "Application.AAL");
            params.put("Application", "AAL");
            params.put("name", name);
            ObjectName objectName = new ObjectName(domain, params);
            if (objectName.isPattern()) {
                objectName = new ObjectName(domain, "name", ObjectName.quote(name));
            }
            return objectName;
        } catch (MalformedObjectNameException e) {
            try {
                return new ObjectName(domain, "name", ObjectName.quote(name));
            } catch (MalformedObjectNameException e1) {
                throw new RuntimeException(e1);
            }
        }
    }
}
