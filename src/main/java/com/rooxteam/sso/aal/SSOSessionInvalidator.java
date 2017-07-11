package com.rooxteam.sso.aal;

import com.google.common.cache.RemovalNotification;
import com.iplanet.sso.SSOToken;
import com.iplanet.sso.SSOTokenManager;
import com.rooxteam.sso.aal.client.SsoAuthorizationClient;
import org.apache.commons.configuration.Configuration;

import static com.rooxteam.sso.aal.AalLogger.LOG;
import static com.rooxteam.sso.aal.ConfigKeys.AUTHORIZATION_TYPE;
import static com.rooxteam.sso.aal.ConfigKeys.AUTHORIZATION_TYPE_DEFAULT;

/**
 * Invalidates cached SSO session on YotaPrincipal removal from cache
 */
public class SSOSessionInvalidator implements com.google.common.cache.RemovalListener<YotaPrincipalKey, YotaPrincipal> {


    SsoAuthorizationClient authorizationClient;
    Configuration config;

    public SSOSessionInvalidator(SsoAuthorizationClient authorizationClient, Configuration config) {
        this.authorizationClient = authorizationClient;
        this.config = config;
    }

    @Override
    public void onRemoval(RemovalNotification<YotaPrincipalKey, YotaPrincipal> notification) {
        YotaPrincipal principalToBeRemoved = notification.getValue();
        if (principalToBeRemoved == null) return;

        if (config != null) {
            String authTypeString = config.getString(AUTHORIZATION_TYPE, AUTHORIZATION_TYPE_DEFAULT);
            AuthorizationType authorizationType = AuthorizationType.valueOf(authTypeString);
            if (authorizationType == AuthorizationType.SSO_TOKEN) {
                SSOToken ssoToken = (SSOToken) principalToBeRemoved.getProperty(PropertyScope.PRIVATE_IDENTITY_PARAMS, YotaPrincipal.SESSION_PARAM);
                if (ssoToken != null) {
                    try {
                        SSOTokenManager.getInstance().destroyToken(ssoToken);
                    } catch (Exception e) {
                        LOG.traceFailedToInvalidateSSOToken(e);
                    }
                }
            }
        }
    }
}
