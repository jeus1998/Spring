package hello.proxy.proxyfactory;

import hello.proxy.common.advice.TimeAdvice;
import hello.proxy.common.service.ServiceImpl;
import hello.proxy.common.service.ServiceInterface;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.AopUtils;

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
}
