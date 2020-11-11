package com.rooxteam.sso.aal.userIp;

import com.rooxteam.sso.aal.configuration.Configuration;

import javax.servlet.http.HttpServletRequest;

import static com.rooxteam.sso.aal.ConfigKeys.USER_CONTEXT_IP_HEADER;
import static com.rooxteam.sso.aal.ConfigKeys.USER_CONTEXT_IP_HEADER_DEFAULT;


/**
 * Get user's IP address from {@link HttpServletRequest} header.
 *
 * Balancer must provide IP via some header.
 * Header name can be specified by configuration parameter {@code com.rooxteam.aal.user-context.ip-header}.
 * By default it uses {@code X-Forwarded-For} header name.
 *
 * To turn on this provider set next configuration property:
 * {@code com.rooxteam.aal.user-context.ip-source=header}
 *
 * @author sergey.syroezhkin
 * @since 11.11.2020
 */
final class HeaderUserIpProvider implements UserIpProvider {

    private final String headerName;

    public HeaderUserIpProvider(Configuration configuration) {
        this.headerName = configuration.getString(USER_CONTEXT_IP_HEADER, USER_CONTEXT_IP_HEADER_DEFAULT);
    }

    @Override
    public String getIpFromRequest(HttpServletRequest request) {
        String headerValue = request.getHeader(headerName);
        if (headerValue != null) {
            return headerValue.isEmpty() ? null : headerValue;
        }
        return null;
    }
}
