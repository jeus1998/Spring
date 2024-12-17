package hello.core.aop;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MyPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if(beanName.equals("example")){
            log.info("[START POST PROCESSOR]");
            ProxyFactory proxyFactory = new ProxyFactory(new Example());
            DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(new TimeAdvice());
            advisor.setPointcut(Pointcut.TRUE);
            proxyFactory.addAdvisor(advisor);
            log.info("[END POST PROCESSOR]");
            return proxyFactory.getProxy();
        }
        return bean;
    }
}
