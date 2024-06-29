# 트랜잭션 AOP 주의 사항 - 초기화 시점

스프링 초기화 시점에는 트랜잭션 AOP가 적용되지 않을 수 있다.

### InitTxTest

```java
@SpringBootTest
public class InitTxTest {
    @Autowired Hello hello;
    @Test
    void go(){
        // 초기화 코드는 스프링이 초기화 시점에 호출한다.
    }

    @TestConfiguration
    static class InitTxTestConfig{
        @Bean
        public Hello hello(){
            return new Hello();
        }
    }

    @Slf4j
    static class Hello{
        @PostConstruct
        @Transactional
        public void initV1(){
            boolean isActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("Hello init @PostConstruct tx active={}", isActive);
        }
        @EventListener(ApplicationReadyEvent.class)
        @Transactional
        public void initV2(){
            boolean isActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("Hello init ApplicationReady tx active={}", isActive);
        }
    }
}
```

테스트 실행 

초기화 코드(예:@PostConstruct)와 @Transactional 을 함께 사용하면 트랜잭션이 적용되지 않는다.
```java
@PostConstruct
@Transactional
public void initV1() {
    log.info("Hello init @PostConstruct");
}
```
- 왜냐하면 초기화 코드가 먼저 호출되고, 그 다음에 트랜잭션 AOP가 적용되기 때문이다. 따라서 초기화 시점에는 해당
  메서드에서 트랜잭션을 획득할 수 없다.
- 스프링 컨테이너 생성 ➡️ 빈 생성 ➡️ 의존관계 주입 ➡️ 초기화 코드(@PostConstruct) ➡️ 스프링 AOP 적용 ➡️ ..... ➡️ 빈 소멸(@PreDestroy)
  ➡️ 스프링 컨테이너 종료 

initV1() 관련 로그
```text
Hello init @PostConstruct tx active=false
```

가장 확실한 대안은 ApplicationReadyEvent 이벤트를 사용하는 것이다.
```java
@EventListener(value = ApplicationReadyEvent.class)
@Transactional
public void init2() {
    log.info("Hello init ApplicationReadyEvent");
}
```
- 이 이벤트는 트랜잭션 AOP를 포함한 스프링이 컨테이너가 완전히 생성되고 난 다음에 이벤트가 붙은 메서드를 호출해
준다. 따라서 ``init2()``는 트랜잭션이 적용된 것을 확인할 수 있다.

init2() ApplicationReadyEvent 이벤트가 호출하는 코드
```text
TransactionInterceptor : Getting transaction for [Hello.init2]
..ngtx.apply.InitTxTest$Hello : Hello init ApplicationReadyEvent tx active=true
TransactionInterceptor : Completing transaction for [Hello.init2]
```