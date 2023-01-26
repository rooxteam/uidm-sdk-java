package com.rooxteam.uidm.sdk.spring.configuration;

import com.rooxteam.sso.aal.ConfigKeys;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class HMACCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return context.getEnvironment().getProperty(ConfigKeys.HMAC_ENABLED, Boolean.class);
    }
}
