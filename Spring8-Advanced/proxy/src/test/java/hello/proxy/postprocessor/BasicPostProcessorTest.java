package hello.proxy.postprocessor;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
public class BasicPostProcessorTest {

    @Test
    void basicConfig(){
        ApplicationContext ac = new AnnotationConfigApplicationContext(Config.class);

        // beanA 이름으로 B 객체가 빈으로 등록된다.
        B b = ac.getBean("beanA", B.class);
        b.helloB();
        b.init();
        log.info("B={}", b);

        // A는 빈으로 등록되지 않는다.
        assertThatThrownBy(() -> ac.getBean(A.class))
                .isInstanceOf(NoSuchBeanDefinitionException.class);
    }

    @Slf4j
    @Configuration
    static class Config{
        @Bean(name = "beanA")
        public A a(){
            return new A();
        }
        @Bean
        public AtoBPostProcessor helloPostProcessor(){
            return new AtoBPostProcessor();
        }
    }

    /**
     * @PostConstruct 또한 그냥 메서드이다. 그래서 언제든 호출은 가능하다.
     * 하지만 특별한 점은 빈이 생성되고 나서 빈 후처리기가 해당 메서드를  자동 호출 한다는 점이다.
     */
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
        public void helloB(){
            log.info("hello B");
        }
        @PostConstruct
        public void init(){
            log.info("post construct B");
        }
    }

    /**
     * BeanPostProcessor 2가지 기능 제공 (default) 메서드로 제공한다. -> 필수 오버라이딩 x
     * 1. @PostConstruct 같은 초기화 이후 실행하는 후처리기  ->  postProcessAfterInitialization
     * 2. @PostConstruct 같은 초기화 이전에 실행하는 후처리기  ->  postProcessBeforeInitialization
     */
    @Slf4j
    static class AtoBPostProcessor implements BeanPostProcessor{
        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            log.info("beanName={} bean={}", beanName, bean);
            if(bean instanceof A){
                return new B();
            }
            return bean;
        }
    }

}
