package com.rooxteam.sso.aal;

import com.google.common.cache.RemovalNotification;
import com.iplanet.sso.SSOToken;
import com.rooxteam.sso.aal.client.SsoAuthorizationClientByToken;

/**
 * Invalidates cached SSO session on YotaPrincipal removal from cache
 */
public class SSOSessionInvalidator implements com.google.common.cache.RemovalListener<YotaPrincipalKey, YotaPrincipal> {


    SsoAuthorizationClientByToken authorizationClient;

    public SSOSessionInvalidator(SsoAuthorizationClientByToken authorizationClient) {
        this.authorizationClient = authorizationClient;
    }

    @Override
    public void onRemoval(RemovalNotification<YotaPrincipalKey, YotaPrincipal> notification) {
        YotaPrincipal principalToBeRemoved = notification.getValue();
        if (principalToBeRemoved == null) return;
        SSOToken ssoToken = (SSOToken) principalToBeRemoved.getProperty(PropertyScope.PRIVATE_IDENTITY_PARAMS, YotaPrincipal.SESSION_PARAM);
        if (ssoToken != null) {
            authorizationClient.invalidateSSOSession(ssoToken);
        }
    }
}
