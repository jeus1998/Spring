# 메트릭 등록3 - Timer

### Timer 설명 

- ``Timer``는 좀 특별한 메트릭 측정 도구인데, 시간을 측정하는데 사용된다.
- 카운터와 유사한데, ``Timer``를 사용하면 실행 시간도 함께 측정할 수 있다.
- ``Timer``는 다음과 같은 내용을 한번에 측정해준다.
  - ``seconds_count``: 누적 실행 수 - 카운터
  - ``seconds_sum``: 실행 시간의 합 - sum
  - ``seconds_max``: 최대 실행 시간(가장 오래걸린 실행 시간) - 게이지
    - 내부에 타임 윈도우라는 개념이 있어서 1~3분 마다 최대 실행 시간이 다시 계산된다.

### OrderServiceV3

```java
/**
 * Timer 사용
 */
@Slf4j
@RequiredArgsConstructor
public class OrderServiceV3 implements OrderService {
    private final MeterRegistry registry;
    private AtomicInteger stock = new AtomicInteger(100);

    @Override
    public void order() {
        Timer timer = Timer.builder("my.order")
                .tag("class", this.getClass().getName())
                .tag("method", "order")
                .description("order")
                .register(registry);

        timer.record(()->{
            log.info("주문");
            stock.decrementAndGet();
            sleep(500);
        });
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
    public void cancel() {

        Timer timer = Timer.builder("my.order")
                .tag("class", this.getClass().getName())
                .tag("method", "cancel")
                .description("order")
                .register(registry);

        timer.record(()->{
            log.info("취소");
            stock.incrementAndGet();
            sleep(200);
        });

    }
    @Override
    public AtomicInteger getStock() {
        return stock;
    }
}
```
- ``Timer.builder(name)``를 통해서 타이머를 생성한다.
- ``name``에는 메트릭 이름을 지정한다.
- 주문과 취소는 메트릭 이름은 같고 ``tag``를 통해서 구분하도록 했다.
- ``register(registry)``: 만든 타이머를 ``MeterRegistry``에 등록한다.
- 타이머를 사용할 때는 ``timer.record()``를 사용하면 된다.
  - 그 안에 시간을 측정할 내용을 함수로 포함하면 된다.

걸리는 시간 측정 
```text
걸리는 시간을 확인하기 위해 주문은 0.5초, 취소는 0.2초 대기하도록 했다.
추가로 가장 오래 걸린 시간을 확인하기 위해 sleep() 에서 최대 0.2초를 랜덤하게 더 추가했다.
```

### OrderConfigV3

```java
@Configuration
public class OrderConfigV3 {
    @Bean
    OrderService orderService(MeterRegistry registry){
        return new OrderServiceV3(registry);
    }
}
```

### ActuatorApplication - 변경

```java
@Import(OrderConfigV3.class)
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

```json
{
    "name": "my.order",
    "description": "order",
    "baseUnit": "seconds",
    "measurements": [
        {
            "statistic": "COUNT",
            "value": 18
        },
        {
            "statistic": "TOTAL_TIME",
            "value": 8.2320227
        },
        {
            "statistic": "MAX",
            "value": 0.6641853
        }
    ],
    "availableTags": [
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
                "hello.order.v3.OrderServiceV3"
            ]
        }
    ]
}
```
- ``measurements``항목을 보면 ``COUNT``, ``TOTAL_TIME`` , ``MAX`` 이렇게 총 3가지 측정 항목을 확인할 수 있다.
  - COUNT : 누적 실행 수(카운터와 같다)
  - TOTAL_TIME : 실행 시간의 합(각각의 실행 시간의 누적 합이다)
  - MAX : 최대 실행 시간(가장 오래 걸린 실행시간이다) - 게이지
- 타이머를 사용하면 총 3가지 측정 항목이 생기는 것을 확인할 수 있다.


프로메테우스 포멧 메트릭 확인
- http://localhost:8080/actuator/prometheus
```text
# HELP my_order_seconds order
# TYPE my_order_seconds summary
my_order_seconds_count{class="hello.order.v3.OrderServiceV3",method="order",} 9.0
my_order_seconds_sum{class="hello.order.v3.OrderServiceV3",method="order",} 5.6024309
my_order_seconds_count{class="hello.order.v3.OrderServiceV3",method="cancel",} 9.0
my_order_seconds_sum{class="hello.order.v3.OrderServiceV3",method="cancel",} 2.6295918
# HELP my_order_seconds_max order
# TYPE my_order_seconds_max gauge
my_order_seconds_max{class="hello.order.v3.OrderServiceV3",method="order",} 0.6641853
my_order_seconds_max{class="hello.order.v3.OrderServiceV3",method="cancel",} 0.3939781
```
- 프로메테우스로 다음 접두사가 붙으면서 3가지 메트릭을 제공한다.
  - ``seconds_count``: 누적 실행 수
  - ``seconds_sum``: 실행 시간의 합
  - ``seconds_max``: 최대 실행 시간(가장 오래걸린 실행 시간), 프로메테우스 ``gague``
    - 참고: 내부에 타임 윈도우라는 개념이 있어서 1~3분 마다 최대 실행 시간이 다시 계산된다.
- 여기서 평균 실행 시간도 계산할 수 있다.
  - ``seconds_sum`` / ``seconds_count``= 평균 실행시간

## 그라파나 등록 

### 그라파나 등록 - 주문수 v3

- 패널 옵션
  - Title : 주문수 v3
- PromQL
  - ``increase(my_order_seconds_count{method="order"}[1m])``
    - ``Legend : {{method}}``
  - ``increase(my_order_seconds_count{method="cancel"}[1m])``
    - ``Legend : {{method}}``
- 참고: 카운터는 계속 증가하기 때문에 특정 시간에 얼마나 증가했는지 확인하려면 ``increase() , rate()``
  같은 함수와 함께 사용하는 것이 좋다.

![3.png](Image%2F3.png)

### 그라파나 등록 - 최대 실행시간

- 패널 옵션
  - Title : 최대 실행시간
- PromQL
  - ``my_order_seconds_max``
  - 게이지여서 그냥 사용 

![4.png](Image%2F4.png)

### 그라파나 등록 - 평균 실행시간

- 패널 옵션
  - Title : 평균 실행시간
- PromQL
  - ``increase(my_order_seconds_sum[1m]) / increase(my_order_seconds_count[1m])``

![5.png](Image%2F5.png)