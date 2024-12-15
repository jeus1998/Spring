package hello.proxy.myaspect;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Slf4j
@SpringBootTest
public class AspectTest {
    @Autowired ApplicationContext ac;
    @Test
    void test(){
        A a = ac.getBean("a", A.class);
        a.yes_log();
        a.no_log();
    }
    static class A {
        public void yes_log (){
            log.info("yes_log");
        }
        public void no_log(){
            log.info("no_log");
        }
    }
    @Configuration
    @EnableAspectJAutoProxy
    static class Config {
        @Bean
        public A a(){
            return new A();
        }
        @Bean
        public AspectExample aspectExample(){
            return new AspectExample();
        }
    }
}
