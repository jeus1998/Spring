package hello.order.v2;

import hello.order.OrderService;
import io.micrometer.core.annotation.Counted;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Counter AOP 적용 @Counted(메트릭 이름)
 */
@Slf4j
public class OrderServiceV2 implements OrderService {
    private AtomicInteger stock = new AtomicInteger(100);
    @Counted("my.order")
    @Override
    public void order() {
        log.info("주문");
        stock.decrementAndGet();

    }
    @Counted("my.order")
    @Override
    public void cancel() {
        log.info("취소");
        stock.incrementAndGet();

    }
    @Override
    public AtomicInteger getStock() {
        return stock;
    }
}