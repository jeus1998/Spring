# bean

- ``bean``: 스프링 전용 포인트컷 지시자, 빈의 이름으로 지정한다.
- 스프링 빈의 이름으로 AOP 적용 여부를 지정한다. 이것은 스프링에서만 사용할 수 있는 특별한 지시자이다
- ``bean(orderService) || bean(*Repository)``
- ```*```과 같은 패턴을 사용할 수 있다.

### BeanTest 

```java
@Slf4j
@Import(BeanTest.BeanAspect.class)
@SpringBootTest
public class BeanTest {

    @Autowired
    OrderService orderService;
    @Test
    void success(){
        orderService.orderItem("itemA");
    }
    @Aspect
    static class BeanAspect{
        @Around("bean(orderService) || bean(*Repository)")
        public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable{
            log.info("[bean] {}", joinPoint.getSignature());
            return joinPoint.proceed();
        }
    }
}
```
- ``OrderService , *Repository(OrderRepository)``의 메서드에 AOP가 적용된다.

실행 결과
```text
[bean] void spring.aop.order.OrderService.orderItem(String)
[orderService] 실행
[bean] String spring.aop.order.OrderRepository.save(String)
[orderRepository] 실행
```