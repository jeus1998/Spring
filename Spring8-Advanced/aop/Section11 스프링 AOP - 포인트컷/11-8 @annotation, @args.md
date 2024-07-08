# @annotation, @args

- ```@annotation```: 메서드가 주어진 애노테이션을 가지고 있는 조인 포인트를 매칭

다음과 같이 메서드(조인 포인트)에 애노테이션이 있으면 매칭한다.
```java
public class MemberServiceImpl {
    @MethodAop("test value")
    public String hello(String param) {
        return "ok";
    }
}
```

### AtAnnotationTest

```java
@Slf4j
@SpringBootTest
@Import(AtAnnotationTest.AtAnnotationAspect.class)
public class AtAnnotationTest {
    @Autowired
    MemberService memberService;

    @Test
    void success(){
        log.info("memberService Proxy={}", memberService.getClass());
        memberService.hello("helloA");
    }

    @Slf4j
    @Aspect
    static class AtAnnotationAspect{
        @Around("@annotation(spring.aop.member.annotation.MethodAop)")
        public Object doAtAnnotation(ProceedingJoinPoint joinPoint) throws Throwable{
            log.info("[@annotation] {}", joinPoint.getSignature());
            return joinPoint.proceed();
        }
    }
}
```

실행 결과
```text
[@annotation] String spring.aop.member.MemberServiceImpl.hello(String)
```

### @args

- ```@args```: 전달된 실제 인수의 런타임 타입이 주어진 타입의 애노테이션을 갖는 조인 포인트
- ex) 전달된 인수의 런타임 타입에 ```@Check```애노테이션이 있는 경우에 매칭한다.
  - @args(test.Check)