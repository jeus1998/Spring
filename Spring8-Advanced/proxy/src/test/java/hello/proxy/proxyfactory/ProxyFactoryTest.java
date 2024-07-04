package hello.proxy.proxyfactory;

import hello.proxy.common.advice.TimeAdvice;
import hello.proxy.common.service.ConcreteService;
import hello.proxy.common.service.ServiceImpl;
import hello.proxy.common.service.ServiceInterface;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.util.PatternMatchUtils;

import static org.assertj.core.api.Assertions.*;

@Slf4j
public class ProxyFactoryTest {
    @Test
    @DisplayName("인터페이스가 있으면 JDK 동적 프록시를 사용")
    void interfaceProxy(){
        ServiceInterface target = new ServiceImpl();

        ProxyFactory proxyFactory = new ProxyFactory(target); // target 추가 실제 객체

        proxyFactory.addAdvice(new TimeAdvice());             // advice(부가 기능 로직) 추가 advice가 target 호출

        ServiceInterface proxy = (ServiceInterface)proxyFactory.getProxy();

        log.info("targetClass={}", target.getClass());
        log.info("proxyClass={}", proxy.getClass());

        proxy.save();
        proxy.find();

        // ProxyFactory 통해서 만든 프록시 객체만 사용 가능

        // 프록시 객체인지?
        boolean isProxy = AopUtils.isAopProxy(proxy);
        assertThat(isProxy).isTrue();

        // JDK 동적 프록시 인지?
        boolean isJdkDynamicProxy = AopUtils.isJdkDynamicProxy(proxy);
        assertThat(isJdkDynamicProxy).isTrue();

        // CGLIB 프록시 인지?
        boolean isCglibProxy = AopUtils.isCglibProxy(proxy);
        assertThat(isCglibProxy).isFalse();
    }

    @Test
    @DisplayName("구체 클래스만 있으면 CGLIB 프록시 사용")
    void concreteProxy(){

        ConcreteService target = new ConcreteService();

        ProxyFactory proxyFactory = new ProxyFactory(target); // target 추가 실제 객체

        proxyFactory.addAdvice(new TimeAdvice());             // advice(부가 기능 로직) 추가 advice가 target 호출

        ConcreteService proxy = (ConcreteService)proxyFactory.getProxy();

        log.info("targetClass={}", target.getClass());
        log.info("proxyClass={}", proxy.getClass());

        proxy.call();

        // ProxyFactory 통해서 만든 프록시 객체만 사용 가능

        // 프록시 객체인지?
        boolean isProxy = AopUtils.isAopProxy(proxy);
        assertThat(isProxy).isTrue();

        // JDK 동적 프록시 인지?
        boolean isJdkDynamicProxy = AopUtils.isJdkDynamicProxy(proxy);
        assertThat(isJdkDynamicProxy).isFalse();

        // CGLIB 프록시 인지?
        boolean isCglibProxy = AopUtils.isCglibProxy(proxy);
        assertThat(isCglibProxy).isTrue();

        // PatternMatchUtils 연습하기
        assertThat(PatternMatchUtils.simpleMatch(new String[]{"*CGLIB*"}, proxy.getClass().toString()))
                .isTrue();

    }

    @Test
    @DisplayName("ProxyTargetClass 옵션을 사용하면 인터페이스가 있어도 CGLIB 사용, 클래스 기반 프록시 사용")
    void proxyTargetClass(){
        ServiceInterface target = new ServiceImpl();

        ProxyFactory proxyFactory = new ProxyFactory(target); // target 추가 실제 객체

        proxyFactory.addAdvice(new TimeAdvice());             // advice(부가 기능 로직) 추가 advice가 target 호출

        proxyFactory.setProxyTargetClass(true);               // CGLIB 기반 프록시 생성하도록 설정

        ServiceInterface proxy = (ServiceInterface)proxyFactory.getProxy();

        log.info("targetClass={}", target.getClass());
        log.info("proxyClass={}", proxy.getClass());

        proxy.save();
        proxy.find();

        // ProxyFactory 통해서 만든 프록시 객체만 사용 가능

        // 프록시 객체인지?
        boolean isProxy = AopUtils.isAopProxy(proxy);
        assertThat(isProxy).isTrue();

        // JDK 동적 프록시 인지?
        boolean isJdkDynamicProxy = AopUtils.isJdkDynamicProxy(proxy);
        assertThat(isJdkDynamicProxy).isFalse();

        // CGLIB 프록시 인지?
        boolean isCglibProxy = AopUtils.isCglibProxy(proxy);
        assertThat(isCglibProxy).isTrue();
    }
}
