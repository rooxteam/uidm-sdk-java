package com.rooxteam.sso.aal.userIp;

import javax.servlet.http.HttpServletRequest;

/**
 * Provider for the user`s IP address.
 *
 * @author sergey.syroezhkin
 * @since 11.11.2020
 */
public interface UserIpProvider {

    /**
     * Returns string value of user's IP address.
     * Returns {@code null} if it's not possible to define it.
     *
     * @param request http servlet request
     * @return IP address
     */
    String getIpFromRequest(HttpServletRequest request);

}
