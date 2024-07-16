# 메트릭 등록2 - @Counted

### 마이크로미터 AOP

- 앞서 만든 ``OrderServiceV1``의 가장 큰 단점은 메트릭을 관리하는 로직이 핵심 비즈니스 개발 로직에 침투했다는 점이다.
- 이런 부분을 분리하려면 어떻게 해야할까? 바로 스프링 AOP를 사용하면 된다.
- 직접 필요한 AOP를 만들어서 적용해도 되지만, 마이크로미터는 이런 상황에 맞추어 필요한 AOP 구성요소를 이미 다
  만들어두었다.

### OrderServiceV2

```java
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
```
- ```@Counted```애노테이션을 측정을 원하는 메서드에 적용한다. 주문과 취소 메서드에 적용했다.
- 그리고 메트릭 이름을 지정하면 된다. 여기서는 이전과 같은 ``my.order``를 적용했다.
- 참고로 이렇게 사용하면 ``tag``에 ``method``를 기준으로 분류해서 적용한다.

### OrderConfigV2

```java
@Configuration
public class OrderConfigV2 {
    @Bean
    OrderService orderService(){
        return new OrderServiceV2();
    }

    /**
     * CountedAspect @Counted 인지 -> Counter AOP 적용
     */
    @Bean
    public CountedAspect countedAspect(MeterRegistry registry){
        return new CountedAspect(registry);
    }
}
```
- ``CountedAspect``를 등록하면 ``@Counted``를 인지해서 ``Counter``를 사용하는 ``AOP``를 적용한다

### ActuatorApplication - 변경

```java
@Import(OrderConfigV2.class)
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

- http://localhost:8080/order
- http://localhost:8080/cancel

액츄에이터 메트릭 확인
- http://localhost:8080/actuator/metrics/my.order

```json
{
    "name": "my.order",
    "measurements": [
        {
            "statistic": "COUNT",
            "value": 17
        }
    ],
    "availableTags": [
        {
            "tag": "result",
            "values": [
                "success"
            ]
        },
        {
            "tag": "exception",
            "values": [
              "none"
            ]
        },
        {
            "tag": "method",
            "values": [
                "cancel",
                "order"
            ]
        },
        {
            "tag": "class",
            "values": [
              "hello.order.v2.OrderServiceV2"
            ]
        }
    ]
}
```
- ```@Counted```를 사용하면 ``result , exception , method , class``같은 다양한 ``tag``를 자동으로 적용한다.

프로메테우스 포멧 메트릭 확인
- http://localhost:8080/actuator/prometheus

```text
# HELP my_order_total  
# TYPE my_order_total counter
my_order_total{class="hello.order.v2.OrderServiceV2",exception="none",method="order",result="success",} 12.0
my_order_total{class="hello.order.v2.OrderServiceV2",exception="none",method="cancel",result="success",} 5.0
```

그라파나 대시보드 확인
- 메트릭 이름과 ``tag``가 기존과 같으므로 같은 대시보드에서 확인할 수 있다.

![2.png](Image%2F2.png)
