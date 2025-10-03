package com.rooxteam.sso.aal.userIp;

import com.rooxteam.sso.aal.configuration.Configuration;

import static com.rooxteam.sso.aal.ConfigKeys.USER_CONTEXT_IP_SOURCE;

/**
 * @author sergey.syroezhkin
 * @since 11.11.2020
 */
public class UserIpProviderFactory {

    private final Configuration configuration;

    public UserIpProviderFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    public UserIpProvider create() {
        String ipSource = configuration.getString(USER_CONTEXT_IP_SOURCE);
        UserIpProviderType providerType = UserIpProviderType.of(ipSource);
        switch (providerType) {
            case REQUEST:
                return new RequestUserIpProvider();
            case HEADER:
                return new HeaderUserIpProvider(configuration);
            default:
                return new NoUserIpProvider();
        }
    }
}
