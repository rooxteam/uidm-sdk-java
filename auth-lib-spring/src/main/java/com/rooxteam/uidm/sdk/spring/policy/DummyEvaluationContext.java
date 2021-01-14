package com.rooxteam.uidm.sdk.spring.policy;

import org.springframework.context.ApplicationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.HashMap;
import java.util.Map;

/**
 * @author sergey.syroezhkin
 * @since 14.01.2021
 */
final class DummyEvaluationContext extends StandardEvaluationContext {

    public DummyEvaluationContext(final ApplicationContext applicationContext, final DummyRootObject dummyRootObject) {
        Map<String, Object> replacedBeans = new HashMap<String, Object>();
        replacedBeans.put("uidmAuthz", dummyRootObject);

        ReplacedBeanFactoryResolver beanResolver = new ReplacedBeanFactoryResolver(applicationContext, replacedBeans);
        setBeanResolver(beanResolver);
        setRootObject(dummyRootObject);
    }

}
