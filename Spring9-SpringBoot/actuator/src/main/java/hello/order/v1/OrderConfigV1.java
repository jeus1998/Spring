package hello.order.v1;

import hello.order.OrderService;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderConfigV1 {
    /**
     * MeterRegistry: 마이크로미터 기능을 제공하는 핵심 컴포넌트
     * 스프링을 통해서 자동 주입 받아서 사용하고, 이곳을 통해서 카운터, 게이지 등을 등록한다.
     */
    @Bean
    OrderService orderService(MeterRegistry meterRegistry){
        return new OrderServiceV1(meterRegistry);
    }
}
