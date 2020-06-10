package com.rooxteam.sso.aal.client.cookies;

import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;

/**
 * @author Ivan Volynkin
 *         ivolynkin@roox.ru
 */
public class CookieStoreFactory {

    public CookieStore createCookieStore() {
        return new BasicCookieStore();
    }

}