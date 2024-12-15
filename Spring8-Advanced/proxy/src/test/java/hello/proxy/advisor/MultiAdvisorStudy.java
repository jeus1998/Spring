package hello.proxy.advisor;

import hello.proxy.common.service.ServiceImpl;
import hello.proxy.common.service.ServiceInterface;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.Test;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;

@Slf4j
public class MultiAdvisorStudy {
    @Test
    void multiProxy(){
        ServiceInterface target = new ServiceImpl();
        ProxyFactory proxyFactory1 = new ProxyFactory(target);
        proxyFactory1.addAdvisor(new DefaultPointcutAdvisor(Pointcut.TRUE, new Advice1()));
        Object proxy1 = proxyFactory1.getProxy();

        ProxyFactory proxyFactory2 = new ProxyFactory(proxy1);
        proxyFactory2.addAdvisor(new DefaultPointcutAdvisor(Pointcut.TRUE, new Advice2()));
        ServiceInterface proxy2 = (ServiceInterface) proxyFactory2.getProxy();

        proxy2.find();
    }
    @Test
    void singleProxy(){
        ServiceInterface target = new ServiceImpl();
        ProxyFactory proxyFactory = new ProxyFactory(target);
        proxyFactory.addAdvisor(new DefaultPointcutAdvisor(Pointcut.TRUE, new Advice2()));
        proxyFactory.addAdvisor(new DefaultPointcutAdvisor(Pointcut.TRUE, new Advice1()));
        ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();
        proxy.find();
    }
    static class Advice1 implements MethodInterceptor{
        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            log.info("advice1");
            return invocation.proceed();
        }
    }
    static class Advice2 implements MethodInterceptor{
        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            log.info("advice2");
            return invocation.proceed();
        }
    }
}
