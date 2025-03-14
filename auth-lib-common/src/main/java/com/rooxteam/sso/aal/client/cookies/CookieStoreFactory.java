package com.rooxteam.sso.aal.client.cookies;


import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.cookie.CookieStore;

/**
 * @author Ivan Volynkin
 *         ivolynkin@roox.ru
 */
public class CookieStoreFactory {

    public CookieStore createCookieStore() {
        return new BasicCookieStore();
    }

}