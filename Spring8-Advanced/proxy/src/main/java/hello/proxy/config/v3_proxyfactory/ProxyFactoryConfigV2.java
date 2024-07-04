package hello.proxy.config.v3_proxyfactory;

import hello.proxy.app.v2.OrderControllerV2;
import hello.proxy.app.v2.OrderRepositoryV2;
import hello.proxy.app.v2.OrderServiceV2;
import hello.proxy.config.v3_proxyfactory.advice.LogTraceAdvice;
import hello.proxy.trace.logtrace.LogTrace;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class ProxyFactoryConfigV2 {

    @Bean
    public OrderRepositoryV2 orderRepositoryV2(LogTrace trace){
        OrderRepositoryV2 orderRepositoryV2 = new OrderRepositoryV2(); // target 생성
        ProxyFactory proxyFactory = new ProxyFactory(orderRepositoryV2);   // target 주입
        DefaultPointcutAdvisor advisor =
                new DefaultPointcutAdvisor(Pointcut.TRUE, new LogTraceAdvice(trace)); // Advisor 생성

        proxyFactory.addAdvisor(advisor); // Advisor 추가
        return (OrderRepositoryV2) proxyFactory.getProxy();  // 프록시 객체 return
    }
    @Bean
    public OrderServiceV2 orderServiceV2(LogTrace trace){
        OrderServiceV2 orderServiceV2 = new OrderServiceV2(orderRepositoryV2(trace)); // target 생성
        ProxyFactory proxyFactory = new ProxyFactory(orderServiceV2);   // target 주입
        DefaultPointcutAdvisor advisor =
                new DefaultPointcutAdvisor(Pointcut.TRUE, new LogTraceAdvice(trace)); // Advisor 생성

        proxyFactory.addAdvisor(advisor); // Advisor 추가
        return (OrderServiceV2) proxyFactory.getProxy();  // 프록시 객체 return
    }
    @Bean
    public OrderControllerV2 orderControllerV2(LogTrace trace){
        OrderControllerV2 orderControllerV2 = new OrderControllerV2(orderServiceV2(trace)); // target 생성
        ProxyFactory proxyFactory = new ProxyFactory(orderControllerV2);

        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut(); // Pointcut 생성
        pointcut.addMethodName("request*"); // request: true / no-log: false

        DefaultPointcutAdvisor advisor =
                new DefaultPointcutAdvisor(pointcut, new LogTraceAdvice(trace)); // Advisor 생성
        proxyFactory.addAdvisor(advisor);  // Advisor 추가
        return (OrderControllerV2) proxyFactory.getProxy(); // 프록시 객체 return
    }
}
