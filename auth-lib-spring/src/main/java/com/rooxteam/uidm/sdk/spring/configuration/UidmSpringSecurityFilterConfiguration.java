package com.rooxteam.uidm.sdk.spring.configuration;

import com.rooxteam.uidm.sdk.spring.authentication.SsoAuthorizationClient;
import com.rooxteam.uidm.sdk.spring.authentication.UidmUserPreAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.filter.GenericFilterBean;

@Configuration
@Import(UidmSdkConfiguration.class)
public class UidmSpringSecurityFilterConfiguration {


    @Bean
    public GenericFilterBean uidmUserPreAuthenticationFilter(SsoAuthorizationClient ssoAuthorizationClient) throws Exception {
        return new UidmUserPreAuthenticationFilter(ssoAuthorizationClient);
    }

}
