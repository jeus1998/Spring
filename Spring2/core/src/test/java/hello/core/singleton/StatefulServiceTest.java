package hello.core.singleton;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class StatefulServiceTest {
    @Test
    void statefulServiceSingleton(){
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(TestConfig.class);

        StatefulService thread1 = ac.getBean("statefulService", StatefulService.class);
        StatefulService thread2 = ac.getBean("statefulService", StatefulService.class);

        // 스레드1 : 사용자 10000원 주문
        thread1.order("itemA", 10000);

        // 스레드2 : 사용자 20000원 주문
        thread2.order("itemB", 20000);

        // 스레드1 : 사용자 주문 금액 조회
        int price = thread1.getPrice();

        // 주문한 금액은 10000원인데 20000원
        assertThat(price).isEqualTo(20000);
    }

    static class TestConfig{
        @Bean
        public StatefulService statefulService(){
            return new StatefulService();
        }
    }
}