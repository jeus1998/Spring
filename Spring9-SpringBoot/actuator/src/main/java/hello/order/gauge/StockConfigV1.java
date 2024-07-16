package hello.order.gauge;

import hello.order.OrderService;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StockConfigV1 {

    @Bean
    public MyStockMetric myStockMetric(OrderService service, MeterRegistry registry){
        return new MyStockMetric(service, registry);
    }
    @Slf4j
    @RequiredArgsConstructor
    static class MyStockMetric{
        private final OrderService orderService;
        private final MeterRegistry registry;

        /**
         * 프로메테우스가 stock 조회를 하면 "stock gauge call" 로그가 찍힌다 (1초에 1번)
         */
        @PostConstruct
        public void init(){
            Gauge.builder("my.stock", orderService, service->{
                log.info("stock gauge call");
                return service.getStock().get();
            }).register(registry);
        }

    }
}
