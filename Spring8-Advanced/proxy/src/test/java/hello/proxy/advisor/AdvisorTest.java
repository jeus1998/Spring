package hello.proxy.advisor;

import hello.proxy.common.advice.TimeAdvice;
import hello.proxy.common.service.ServiceImpl;
import hello.proxy.common.service.ServiceInterface;
import org.junit.jupiter.api.Test;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;

public class AdvisorTest {
    /**
     * Advisor = Advice(부가 로직: MethodInterceptor) + Pointcut(어디에? 필터)
     * DefaultPointcutAdvisor (Advisor 인터페이스의 가장 일반적인 구현체) 생성자로 포인트컷, 어드바이스를 넣는다.
     * 이전에 addAdivce()를 했을 때 결과적으로는 (Pointcut.TRUE(포인트컷), Advice) Advisor를 ProxyFactory에 넣어준다.
     * 즉 ProxyFactory는 Advisor가 필수이다.
     */
    @Test
    void advisorTest1(){
        ServiceInterface target = new ServiceImpl();
        ProxyFactory proxyFactory = new ProxyFactory(target);
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(Pointcut.TRUE, new TimeAdvice());
        proxyFactory.addAdvisor(advisor);
        ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();

        proxy.save();
        proxy.find();
    }
}
