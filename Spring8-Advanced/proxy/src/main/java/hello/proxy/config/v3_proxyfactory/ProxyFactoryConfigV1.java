package hello.proxy.config.v3_proxyfactory;

import hello.proxy.app.v1.*;
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
public class ProxyFactoryConfigV1 {

    @Bean
    public OrderRepositoryV1 orderRepositoryV1(LogTrace trace){
        OrderRepositoryV1 orderRepositoryV1 = new OrderRepositoryV1Impl(); // target 생성
        ProxyFactory proxyFactory = new ProxyFactory(orderRepositoryV1);   // target 주입
        DefaultPointcutAdvisor advisor =
                new DefaultPointcutAdvisor(Pointcut.TRUE, new LogTraceAdvice(trace)); // Advisor 생성

        proxyFactory.addAdvisor(advisor); // Advisor 추가
        return (OrderRepositoryV1) proxyFactory.getProxy();  // 프록시 객체 return
    }
    @Bean
    public OrderServiceV1 orderServiceV1(LogTrace trace){
        OrderServiceV1 orderServiceV1 = new OrderServiceV1Impl(orderRepositoryV1(trace)); // target 생성
        ProxyFactory proxyFactory = new ProxyFactory(orderServiceV1 );   // target 주입
        DefaultPointcutAdvisor advisor =
                new DefaultPointcutAdvisor(Pointcut.TRUE, new LogTraceAdvice(trace)); // Advisor 생성

        proxyFactory.addAdvisor(advisor); // Advisor 추가
        return (OrderServiceV1) proxyFactory.getProxy();  // 프록시 객체 return
    }
    @Bean
    public OrderControllerV1 orderControllerV1(LogTrace trace){
        OrderControllerV1 orderControllerV1 = new OrderControllerV1Impl(orderServiceV1(trace)); // target 생성
        ProxyFactory proxyFactory = new ProxyFactory(orderControllerV1);

        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut(); // Pointcut 생성
        pointcut.addMethodName("request*"); // request: true / no-log: false

        DefaultPointcutAdvisor advisor =
                new DefaultPointcutAdvisor(pointcut, new LogTraceAdvice(trace)); // Advisor 생성
        proxyFactory.addAdvisor(advisor);  // Advisor 추가
        return (OrderControllerV1) proxyFactory.getProxy(); // 프록시 객체 return
    }
}
