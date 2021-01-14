package com.rooxteam.uidm.sdk.spring.policy;

import org.springframework.context.ApplicationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.HashMap;
import java.util.Map;

/**
 * @author sergey.syroezhkin
 * @since 14.01.2021
 */
final class InjectedRootEvaluationContext extends StandardEvaluationContext {

    InjectedRootEvaluationContext(final ApplicationContext applicationContext, final InvocationRootObject invocationRootObject) {
        Map<String, Object> replacedBeans = new HashMap<String, Object>();
        replacedBeans.put("uidmAuthz", invocationRootObject);

        ReplacedBeanFactoryResolver beanResolver = new ReplacedBeanFactoryResolver(applicationContext, replacedBeans);
        setBeanResolver(beanResolver);
        setRootObject(invocationRootObject);
    }

}
