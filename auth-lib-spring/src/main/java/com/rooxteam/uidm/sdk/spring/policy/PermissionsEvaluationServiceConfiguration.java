package com.rooxteam.uidm.sdk.spring.policy;

import com.rooxteam.sso.aal.AuthenticationAuthorizationLibrary;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * @author sergey.syroezhkin
 * @since 14.01.2021
 */
@Configuration
public class PermissionsEvaluationServiceConfiguration {

    @Bean
    public PermissionsEvaluationService permissionsEvaluationService(AuthenticationAuthorizationLibrary aal,
                                                                     RequestMappingHandlerMapping handlerMapping,
                                                                     ApplicationContext applicationContext) {
        return new PermissionsEvaluationServiceImpl(aal, handlerMapping, applicationContext);
    }
}
