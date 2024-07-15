# HTTP 요청 응답 기록

- HTTP 요청과 응답의 과거 기록을 확인하고 싶다면 ``httpexchanges``엔드포인트를 사용하면 된다.
- ``HttpExchangeRepository`` 인터페이스의 구현체를 빈으로 등록하면 ``httpexchanges``엔드포인트를 사용할 수 있다.
- 주의! 해당 빈을 등록하지 않으면 ``httpexchanges``엔드포인트가 활성화 되지 않는다
- 스프링 부트는 기본으로 ``InMemoryHttpExchangeRepository``구현체를 제공한다.

### InMemoryHttpExchangeRepository 추가

```java
@SpringBootApplication
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
- 이 구현체는 최대 100개의 HTTP 요청을 제공한다.
- 최대 요청이 넘어가면 과거 요청을 삭제한다.
- setCapacity()로 최대 요청수를 변경할 수 있다

### 실행

- http://localhost:8080/actuator/httpexchanges
- 실행해보면 지금까지 실행한 HTTP 요청과 응답 정보를 확인할 수 있다.
- 참고로 이 기능은 매우 단순하고 기능에 제한이 많기 때문에 개발 단계에서만 사용하고, 실제 운영 서비스에서는 모니터링 툴이나 
  핀포인트, Zipkin 같은 다른 기술을 사용하는 것이 좋다.

