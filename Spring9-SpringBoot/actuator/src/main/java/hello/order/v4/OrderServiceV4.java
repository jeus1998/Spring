package hello.order.v4;

import hello.order.OrderService;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Timed 사용
 * @Counted 와 다르게 타입(클래스)레벨, 메서드 레벨 모두 적용 가능
 */
@Slf4j
@Timed("my.order")
@RequiredArgsConstructor
public class OrderServiceV4 implements OrderService {
    private AtomicInteger stock = new AtomicInteger(100);

    @Override
    public void order() {
        log.info("주문");
        stock.decrementAndGet();
        sleep(500);
    }
    @Override
    public void cancel() {
        log.info("취소");
        stock.incrementAndGet();
        sleep(200);
    }
    private static void sleep(int l){
       try {
           Thread.sleep(l + new Random().nextInt(200));
       }
       catch (InterruptedException e){
           throw new RuntimeException(e);
       }
   }
    @Override
    public AtomicInteger getStock() {
        return stock;
    }
}
