package hello.proxy.advisor;

import hello.proxy.common.advice.TimeAdvice;
import hello.proxy.common.service.ServiceImpl;
import hello.proxy.common.service.ServiceInterface;
import org.junit.jupiter.api.Test;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import java.lang.reflect.Method;

class AdvisorStudy {
    @Test
    void advisorTest(){
        ServiceInterface target = new ServiceImpl();
        ProxyFactory proxyFactory = new ProxyFactory(target);
        proxyFactory.addAdvisor(new DefaultPointcutAdvisor(new MyPointCut(), new TimeAdvice()));
        ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();
        proxy.save();
        proxy.find();
    }
    static class MyPointCut implements Pointcut{
        @Override
        public ClassFilter getClassFilter() {
            return ClassFilter.TRUE;
        }
        @Override
        public MethodMatcher getMethodMatcher() {
            return new MyMethodMatcher();
        }
        static class MyMethodMatcher implements MethodMatcher{
            @Override
            public boolean matches(Method method, Class<?> targetClass) {
                return method.getName().equals("save");
            }
            @Override
            public boolean isRuntime() {
                return false;
            }
            @Override
            public boolean matches(Method method, Class<?> targetClass, Object... args) {
                throw new UnsupportedOperationException();
            }
        }
    }
}
