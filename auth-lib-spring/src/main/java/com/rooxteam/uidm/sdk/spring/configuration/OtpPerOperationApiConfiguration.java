package com.rooxteam.uidm.sdk.spring.configuration;

import com.rooxteam.errors.exception.ErrorTranlator;
import com.rooxteam.errors.exception.RethrowingEntityTranslator;
import com.rooxteam.errors.exception.ToResponseEntityTranslator;
import com.rooxteam.sso.aal.AuthenticationAuthorizationLibrary;
import com.rooxteam.uidm.sdk.spring.authorization.M2MOtpController;
import com.rooxteam.uidm.sdk.spring.authorization.M2MOtpService;
import com.rooxteam.uidm.sdk.spring.authorization.PolicyEvaluationController;
import com.rooxteam.uidm.sdk.spring.authorization.PolicyEvaluationService;
import com.rooxteam.uidm.sdk.spring.authorization.PolicyEvaluationServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Exposes /otp and /policyEvaluation API that can handle OTP per operation Use Cases.
 * Alternative is to provide your own API that use
 */
@Configuration
@Import(UidmSdkConfiguration.class)
public class OtpPerOperationApiConfiguration {


    public static final String ERROR_TRANSLATION_PROPERTY = "com.rooxteam.uidm.sdk.error.translation";
    public static final String ERROR_TRANSLATION_RESPOND = "respond";
    public static final String ERROR_TRANSLATION_RETHROW = "rethrow";

    @Bean
    public M2MOtpService aalOtpService(AuthenticationAuthorizationLibrary aal) {
        return new M2MOtpService(aal);
    }

    @Bean
    public M2MOtpController aalOtpController(M2MOtpService aalOtpService, ErrorTranlator errorTranslator) {
        return new M2MOtpController(aalOtpService, errorTranslator);
    }

    @Bean
    public PolicyEvaluationService policyEvaluationService(AuthenticationAuthorizationLibrary aal) {
        return new PolicyEvaluationServiceImpl(aal);
    }

    @Bean
    public PolicyEvaluationController policyEvaluationController(final PolicyEvaluationService policyEvaluationService) {
        return new PolicyEvaluationController(policyEvaluationService);
    }

    @Bean
    public ErrorTranlator errorTranlator(AuthenticationAuthorizationLibrary aal) {
        // use the same configuration provider as core SDK
        com.rooxteam.sso.aal.configuration.Configuration configuration = aal.getConfiguration();
        // respond or rethrow
        String errorTranslateMethod = configuration.getString(ERROR_TRANSLATION_PROPERTY, ERROR_TRANSLATION_RESPOND);
        switch (errorTranslateMethod) {
            case ERROR_TRANSLATION_RESPOND: {
                return new ToResponseEntityTranslator();
            }
            case ERROR_TRANSLATION_RETHROW: {
                return new RethrowingEntityTranslator();
            }
        }
        throw new IllegalArgumentException("'com.rooxteam.uidm.sdk.error.translation' should be one of 'respond' or 'rethrow'");
    }
}
