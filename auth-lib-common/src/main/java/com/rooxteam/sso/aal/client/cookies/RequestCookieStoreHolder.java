package com.rooxteam.sso.aal.client.cookies;

import org.apache.http.client.CookieStore;


/**
 * @author Ivan Volynkin
 *         ivolynkin@roox.ru
 */
public class RequestCookieStoreHolder {
    private static final ThreadLocal<CookieStore> cookieStoreThreadLocal = new ThreadLocal<CookieStore>();

    public static CookieStore getCookieStore() {
        return cookieStoreThreadLocal.get();
    }

    public static void clearCookieStore() {
        cookieStoreThreadLocal.remove();
    }

    public static void setCookieStoreThreadLocal(CookieStore cookieStore) {
        cookieStoreThreadLocal.set(cookieStore);
    }

}