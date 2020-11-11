package com.rooxteam.sso.aal.userIp;

import javax.servlet.http.HttpServletRequest;

/**
 * Get user's IP address from {@link HttpServletRequest} remote address property.
 *
 * Balancer must provide IP via X-Forwarded-For header,
 * Webapi must be configured to throw XFF header to {@link HttpServletRequest} remote address property.
 * Use property like {@code server.forward-headers-strategy=NATIVE} or some other with similar behaviour.
 *
 * To turn on this provider set next configuration property:
 * {@code com.rooxteam.aal.user-context.ip-source=request}
 *
 * @author sergey.syroezhkin
 * @since 11.11.2020
 */
final class RequestUserIpProvider implements UserIpProvider {

    @Override
    public String getIpFromRequest(HttpServletRequest request) {
        return request.getRemoteAddr();
    }
}
