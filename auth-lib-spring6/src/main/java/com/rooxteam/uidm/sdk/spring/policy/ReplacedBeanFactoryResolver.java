package com.rooxteam.uidm.sdk.spring.policy;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;

import java.util.Map;

/**
 * @author sergey.syroezhkin
 * @since 13.01.2021
 */
final class ReplacedBeanFactoryResolver extends BeanFactoryResolver {

    private final Map<String, Object> replacedBeans;

    ReplacedBeanFactoryResolver(BeanFactory beanFactory, Map<String, Object> replacedBeans) {
        super(beanFactory);
        this.replacedBeans = replacedBeans;
    }

    @Override
    public Object resolve(EvaluationContext context, String beanName) throws AccessException {
        if (replacedBeans.containsKey(beanName)) {
            return replacedBeans.get(beanName);
        }
        return super.resolve(context, beanName);
    }
}
