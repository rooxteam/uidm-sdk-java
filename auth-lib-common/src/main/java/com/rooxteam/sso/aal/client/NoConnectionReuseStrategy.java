package com.rooxteam.sso.aal.client;

import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.ConnectionReuseStrategy;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.protocol.HttpContext;

/**
 * A strategy that never re-uses a connection.
 *
 */
@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class NoConnectionReuseStrategy implements ConnectionReuseStrategy {

    public static final NoConnectionReuseStrategy INSTANCE = new NoConnectionReuseStrategy();

    public NoConnectionReuseStrategy() {
        super();
    }

    @Override
    public boolean keepAlive(HttpRequest request, HttpResponse response, HttpContext context) {
        return false;
    }
}