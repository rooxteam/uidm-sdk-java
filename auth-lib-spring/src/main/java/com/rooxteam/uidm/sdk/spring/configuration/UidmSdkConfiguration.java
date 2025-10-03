package com.rooxteam.uidm.sdk.spring.configuration;

import com.rooxteam.sso.aal.AalFactory;
import com.rooxteam.sso.aal.AuthenticationAuthorizationLibrary;
import com.rooxteam.sso.aal.configuration.ConfigurationBuilder;
import com.rooxteam.sso.aal.realms.RealmDetermineService;
import com.rooxteam.sso.aal.realms.impl.RealmBasedAuthenticationAuthorizationLibrary;
import com.rooxteam.uidm.sdk.spring.authentication.AalAuthorizationClient;
import com.rooxteam.uidm.sdk.spring.authentication.SsoAuthorizationClient;
import com.rooxteam.uidm.sdk.spring.authorization.AalResourceValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class UidmSdkConfiguration {

    @Bean
    public AuthenticationAuthorizationLibrary aal(@Autowired(required = false) RealmDetermineService realmDetermineService,
                                                  Environment environment) {
        if (realmDetermineService != null) {
            return new RealmBasedAuthenticationAuthorizationLibrary(ConfigurationBuilder.fromSpringEnvironment(environment), realmDetermineService);
        } else {
            return AalFactory.create(ConfigurationBuilder.fromSpringEnvironment(environment));
        }
    }

    @Bean
    public AalAuthorizationClient aalAuthorizationClient(AuthenticationAuthorizationLibrary aal) {
        return new AalAuthorizationClient(aal);
    }

    @Bean
    public AalResourceValidation aalResourceValidation(AalAuthorizationClient aalAuthorizationClient) {
        return aalAuthorizationClient;
    }

    @Bean
    public AalResourceValidation uidmAuthz(AalAuthorizationClient aalAuthorizationClient) {
        return aalAuthorizationClient;
    }

    @Bean
    public SsoAuthorizationClient ssoAuthorizationClient(AalAuthorizationClient aalAuthorizationClient){
        return aalAuthorizationClient;
    }

}
