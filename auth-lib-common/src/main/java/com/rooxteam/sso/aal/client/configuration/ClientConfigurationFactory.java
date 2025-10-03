package com.rooxteam.sso.aal.client.configuration;

import com.rooxteam.sso.aal.configuration.Configuration;

public final class ClientConfigurationFactory {

    public static ClientConfiguration create(Configuration configuration) {
        return new ClientConfigurationImpl(configuration);
    }
}
