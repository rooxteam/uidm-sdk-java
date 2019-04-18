package com.rooxteam.uidm.sdk.spring.configuration;

import com.rooxteam.sso.aal.client.cookies.CookieStoreFactory;
import com.rooxteam.sso.aal.client.cookies.RequestCookieStoreFilter;
import com.rooxteam.uidm.sdk.spring.authentication.SsoAuthorizationClient;
import com.rooxteam.uidm.sdk.spring.authentication.UidmUserPreAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.Filter;

@Configuration
@Import(UidmSdkConfiguration.class)
public class UidmSpringSecurityFilterConfiguration {


    @Bean
    public GenericFilterBean uidmUserPreAuthenticationFilter(SsoAuthorizationClient ssoAuthorizationClient) {
        return new UidmUserPreAuthenticationFilter(ssoAuthorizationClient);
    }


    @Bean
    @Order(0)
    public Filter requestContextFilter() {
        return new RequestCookieStoreFilter(new CookieStoreFactory());
    }


}
