package com.rooxteam.sso.aal.client.cookies;


import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.cookie.Cookie;
import org.apache.hc.client5.http.cookie.CookieStore;

import java.util.Date;
import java.util.List;

import static com.rooxteam.sso.aal.AalLogger.LOG;


/**
 * @author Ivan Volynkin
 *         ivolynkin@roox.ru
 */
public class RequestCookieStore implements CookieStore {

    @Override
    public void addCookie(Cookie cookie) {
        getCookieStore().addCookie(cookie);
    }

    @Override
    public List<Cookie> getCookies() {
        return getCookieStore().getCookies();
    }

    @Override
    public boolean clearExpired(Date date) {
        return getCookieStore().clearExpired(date);
    }

    @Override
    public void clear() {
        getCookieStore().clear();
    }

    private CookieStore getCookieStore() {
        CookieStore cookieStore = RequestCookieStoreHolder.getCookieStore();
        if (cookieStore == null) {
            cookieStore = new BasicCookieStore();
        }
        return cookieStore;
    }
}