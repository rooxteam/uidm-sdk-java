package com.rooxteam.uidm.sdk.spring.configuration;

import com.rooxteam.sso.aal.AuthenticationAuthorizationLibrary;
import com.rooxteam.sso.request.HttpServletRequestHolderFilter;
import com.rooxteam.uidm.sdk.client.cookies.RequestCookieStoreFilter;
import com.rooxteam.uidm.sdk.spring.authentication.RooxConfigBasedUserPreAuthFilterSettingsImpl;
import com.rooxteam.uidm.sdk.spring.authentication.SsoAuthorizationClient;
import com.rooxteam.uidm.sdk.spring.authentication.UidmUserPreAuthenticationFilter;
import com.rooxteam.uidm.sdk.spring.authentication.UserPreAuthFilterSettings;
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
    public UserPreAuthFilterSettings springEnvironmentUserPreAuthFilterSettings(AuthenticationAuthorizationLibrary aal) {
        return new RooxConfigBasedUserPreAuthFilterSettingsImpl(aal.getConfiguration());
    }

    @Bean
    public GenericFilterBean uidmUserPreAuthenticationFilter(SsoAuthorizationClient ssoAuthorizationClient,
                                                             UserPreAuthFilterSettings userPreAuthFilterSettings) {
        return new UidmUserPreAuthenticationFilter(ssoAuthorizationClient, userPreAuthFilterSettings);
    }

    @Bean
    @Order(0)
    public Filter requestCookieStoreFilter() {
        return new RequestCookieStoreFilter();
    }

    @Bean
    @Order(0)
    public Filter httpServletRequestHolderFilter() {
        return new HttpServletRequestHolderFilter();
    }


}
