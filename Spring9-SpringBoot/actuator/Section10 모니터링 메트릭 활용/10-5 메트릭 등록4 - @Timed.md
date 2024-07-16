# 메트릭 등록4 - @Timed

- 타이머는 ```@Timed```라는 애노테이션을 통해 ``AOP``를 적용할 수 있다

### OrderServiceV4

```java
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
```
- ``@Timed("my.order")`` 타입이나 메서드 중에 적용할 수 있다.
- 타입에 적용하면 해당 타입의 모든 ``public``메서드에 타이머가 적용된다

### OrderConfigV4

```java
@Configuration
public class OrderConfigV4 {
    @Bean
    OrderService orderService(){
        return new OrderServiceV4();
    }

    /**
     * @Timed 인식을 위해 TimedAspect 적용
     */
    @Bean
    public TimedAspect timedAspect(MeterRegistry registry){
        return new TimedAspect(registry);
    }
}
```
- ``TimedAspect``를 등록해야 ``@Timed``에 ``AOP``가 적용된다.

### ActuatorApplication - 수정

```java
@Import(OrderConfigV4.class)
@SpringBootApplication(scanBasePackages = "hello.controller")
public class ActuatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(ActuatorApplication.class, args);
    }
    @Bean
    public InMemoryHttpExchangeRepository httpExchangeRepository(){
        return new InMemoryHttpExchangeRepository();
    }
}
```

### 실행, 확인 

실행
- http://localhost:8080/order
- http://localhost:8080/cancel

액츄에이터 메트릭 확인
- http://localhost:8080/actuator/metrics/my.order
- ``tag``중에 ``exception``이 추가 되는 부분을 제외하면 기존과 같다

프로메테우스 포멧 메트릭 확인
- http://localhost:8080/actuator/prometheus
- 생성되는 프로메테우스 포멧도 기존과 같다.

그라파나 대시보드 확인
- 메트릭 이름과 ``tag``가 기존과 같으므로 같은 대시보드에서 확인할 수 있다.




