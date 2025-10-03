package com.rooxteam.sso.aal.userIp;

import com.rooxteam.sso.aal.request.HttpRequestInfo;

/**
 * This provider returns always {@code null} value.
 *
 * It uses if configuration property is not set:
 * {@code com.rooxteam.aal.user-context.ip-source}
 *
 * @author sergey.syroezhkin
 * @since 11.11.2020
 */
final class NoUserIpProvider implements UserIpProvider {

    @Override
    public String getIpFromRequest(HttpRequestInfo request) {
        return null;
    }

}
