package com.rooxteam.sso.aal.userIp;

import com.rooxteam.sso.aal.request.HttpRequestInfo;

/**
 * Get user's IP address from {@link HttpRequestInfo} remote address property.
 *
 * Balancer must provide IP via X-Forwarded-For header,
 * Webapi must be configured to throw XFF header to http request remote address property.
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
    public String getIpFromRequest(HttpRequestInfo request) {
        return request.getRemoteAddr();
    }
}
