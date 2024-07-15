# @Profile

- 프로필과 외부 설정을 사용해서 각 환경마다 설정값을 다르게 적용하는 것은 이해했다.
- 그런데 설정값이 다른 정도가 아니라 각 환경마다 서로 다른 빈을 등록해야 한다면 어떻게 해야할까?
- 예를 들어서 결제 기능을 붙여야 하는데, 로컬 개발 환경에서는 실제 결제가 발생하면 문제가 되니 가짜 결제 기능이 있는 
  스프링 빈을 등록하고, 운영 환경에서는 실제 결제 기능을 제공하는 스프링 빈을 등록한다고 가정해보자.

### PayClient

```java
package hello.pay;
public interface PayClient {
    void pay(int money);
}
```
- DI를 적극 활용하기 위해 인터페이스를 사용한다.

### LocalPayClient

```java
@Slf4j
public class LocalPayClient implements PayClient{
    @Override
    public void pay(int money) {
        log.info("로컬 결제 money={}", money);
    }
}
```

### ProdPayClient

```java
@Slf4j
public class ProdPayClient implements PayClient{
    @Override
    public void pay(int money) {
        log.info("운영 결제 money={}", money);
    }
}
```

### OrderService

```java
@Service
@RequiredArgsConstructor
public class OrderService {
    private final PayClient payClient;
    public void order(int money){
        payClient.pay(money);
    }
}
```
- ``PayClient``를 사용하는 부분이다.
-  상황(프로필)에 따라서 ``LocalPayClient``또는 ``ProdPayClient``를 주입받는다.

### PayConfig

```java
@Slf4j
@Configuration
public class PayConfig {
    @Bean
    @Profile("default")
    public LocalPayClient localPayClient(){
        log.info("LocalPayClient 빈 등록");
        return new LocalPayClient();
    }
    @Bean
    @Profile("prod")
    public ProdPayClient prodPayClient(){
        log.info("ProdPayClient 빈 등록");
        return new ProdPayClient();
    }
}
```
- ``@Profile``애노테이션을 사용하면 해당 프로필이 활성화된 경우에만 빈을 등록한다.
  - ``default``프로필(기본값)이 활성화 되어 있으면 ``LocalPayClient``를 빈으로 등록한다.
  - ``prod``프로필이 활성화 되어 있으면 ``ProdPayClient``를 빈으로 등록한다.

### OrderRunner

```java
@Component
@RequiredArgsConstructor
public class OrderRunner implements ApplicationRunner {
    private final OrderService orderService;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        orderService.order(1000);
    }
}
```
- ``ApplicationRunner``인터페이스를 사용하면 스프링은 빈 초기화가 모두 끝나고 애플리케이션 로딩이 완료되는 시점에 
  run(args)메서드를 호출해준다.

### ExternalReadApplication 변경

```java
@Import(MyDataSourceConfigV3.class)
@SpringBootApplication(scanBasePackages = {"hello.datasource", "hello.pay"})
public class ExternalReadApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExternalReadApplication.class, args);
    }
}
```
- 실행하기 전에 컴포넌트 스캔 부분에 hello.pay 패키지를 추가하자.

### 실행, 결과 

프로필 없이 실행
```text
No active profile set, falling back to 1 default profile: "default"
LocalPayClient 빈 등록
...
로컬 결제 money=1000
```
- 프로필 없이 실행하면 ``default``프로필이 사용된다.
- ``default``프로필이 사용되면 ``LocalPayClient``가 빈으로 등록되는 것을 확인할 수 있다.


prod 프로필 실행
```text
The following 1 profile is active: "prod"
ProdPayClient 빈 등록
...
운영 결제 money=1000
```
- ```--spring.profiles.active=prod```프로필 활성화 적용
- ``prod``프로필을 적용했다.
- ``prod``프로필이 사용되면 ``ProdPayClient``가 빈으로 등록되는 것을 확인할 수 있다.

### @Profile

```java
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(ProfileCondition.class)
public @interface Profile {
	String[] value();
}
```
- ```@Profile```은 특정 조건에 따라서 해당 빈을 등록할지 말지 선택한다.
- 코드를 보면 ```@Conditional(ProfileCondition.class)```를 확인할 수 있다.
- 스프링은 ```@Conditional```기능을 활용해서 개발자가 더 편리하게 사용할 수 있는 ```@Profile```기능을 제공하는 것이다.

ProfileCondition.class
- ``Condition``인터페이스 구현 클래스 
- mathes(conditionContext context, AnnotatedTypeMetadata metadata)를 통해서 ``if-else``문 같이 동작한다. 
- 프로필 value에 따라서 ``false``or ``true``반환 


정리
- ```@Profile```을 사용하면 각 환경 별로 외부 설정 값을 분리하는 것을 넘어서, 등록되는 스프링 빈도 분리할 수 있다.




