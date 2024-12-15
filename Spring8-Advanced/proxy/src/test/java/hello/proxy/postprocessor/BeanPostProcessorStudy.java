package hello.proxy.postprocessor;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class BeanPostProcessorStudy {
    @Test
    void test(){
        ApplicationContext ac = new AnnotationConfigApplicationContext(Config.class);
        B b = ac.getBean("beanA", B.class);

        assertThatThrownBy(() -> ac.getBean("beanA", A.class))
                .isInstanceOf(BeanNotOfRequiredTypeException.class);
    }
    @Configuration
    static class Config{
        @Bean
        public A beanA(){
            return new A();
        }
        @Bean
        public BeanPostProcessor myPostProcessor(){
            return new MyPostProcessor();
        }
    }
    @Slf4j
    static class MyPostProcessor implements BeanPostProcessor{
        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            log.info("beanName={}, bean={}", beanName, bean.getClass());
            if(bean instanceof A) return new B();
            return bean;
        }
    }
    @Slf4j
    static class A {
        public void helloA(){
            log.info("hello A");
        }
        @PostConstruct
        public void init(){
            log.info("post construct A");
        }
    }
    @Slf4j
    static class B {
        public void helloB() {
            log.info("hello B");
        }
        @PostConstruct
        public void init(){
            log.info("post construct B");
        }
    }
}
