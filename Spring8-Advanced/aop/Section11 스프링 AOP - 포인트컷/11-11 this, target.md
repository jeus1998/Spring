# this, target

정의
- ``this``: 스프링 빈 객체(스프링 AOP 프록시)를 대상으로 하는 조인 포인트
- ``target``: Target 객체(스프링 AOP 프록시가 가리키는 실제 대상)를 대상으로 하는 조인 포인트

설명 
```java
this(hello.aop.member.MemberService)
target(hello.aop.member.MemberService)
```
- ``this , target``은 다음과 같이 적용 타입 하나를 정확하게 지정해야 한다. 
- ``*``같은 패턴을 사용할 수 없다.
- 부모 타입을 허용한다.

this vs target
- 스프링에서 AOP를 적용하면 실제 ``target``객체 대신에 프록시 객체가 스프링 빈으로 등록된다.
- ``this``는 스프링 빈으로 등록되어 있는 프록시 객체를 대상으로 포인트컷을 매칭한다.
- ``target``은 실제 target 객체를 대상으로 포인트컷을 매칭한다.

### 프록시 생성 방식에 따른 차이

- 스프링은 프록시를 생성할 때 JDK 동적 프록시와 CGLIB를 선택할 수 있다. 
- 둘의 프록시를 생성하는 방식이 다르기 때문에 차이가 발생한다.
- JDK 동적 프록시: 인터페이스가 필수이고, 인터페이스를 구현한 프록시 객체를 생성한다.
- CGLIB: 인터페이스가 있어도 구체 클래스를 상속 받아서 프록시 객체를 생성한다.

JDK 동적 프록시
![2.png](Image%2F2.png)

MemberService 인터페이스 지정 
- ``this(hello.aop.member.MemberService)``
  - proxy 객체를 보고 판단한다. ``this``는 부모 타입을 허용하기 때문에 AOP가 적용된다.
- ``target(hello.aop.member.MemberService)``
  - ``target``객체를 보고 판단한다. ``target``은 부모 타입을 허용하기 때문에 AOP가 적용된다.

MemberServiceImpl 구체 클래스 지정
- ``this(hello.aop.member.MemberServiceImpl)``
  - proxy 객체를 보고 판단한다. 
  - JDK 동적 프록시 객체로 만들어진 proxy 객체는 ``MemberService``인터페이스를 기반으로 구현된 새로운 클래스이다. 
  - 따라서 ``MemberServiceImpl``룰 전혀 알지 못하므로 AOP 적용 대상이 아니다.
- ``target(hello.aop.member.MemberServiceImpl)``
  - ``target``객체를 보고 판단한다. 
  - ``target``객체가 ``MemberServiceImpl``타입이므로 AOP 적용 대상이다.

CGLIB 프록시
![3.png](Image%2F3.png)


MemberService 인터페이스 지정
- ``this(hello.aop.member.MemberService)``
  - proxy 객체를 보고 판단한다.
  - ``this``는 부모 타입을 허용하기 때문에 AOP가 적용된다.
  - MemberService ➡️ MemberServiceImpl ➡️ proxy
- ``target(hello.aop.member.MemberService)``
  - ``target``객체를 보고 판단한다. 
  - ``target`` 은 부모 타입을 허용하기 때문에 AOP가 적용된다.
  - MemberService ➡️ (MemberServiceImpl == target)

MemberServiceImpl 구체 클래스 지정
- ``this(hello.aop.member.MemberServiceImpl)`` 
  - proxy 객체를 보고 판단한다. 
  - CGLIB로 만들어진 proxy 객체는 ``MemberServiceImpl``를 상속 받아서 만들었기 때문에 AOP가 적용된다. 
  - ``this``가 부모 타입을 허용하기 때문에 포인트컷의 대상이 된다.
- ``target(hello.aop.member.MemberServiceImpl)``
  - ``target``객체를 보고 판단한다. 
  - ``target`` 객체가 ``MemberServiceImpl``타입이므로 AOP 적용 대상이다.

정리 
- 프록시를 대상으로 하는 ``this``의 경우 구체 클래스를 지정하면 프록시 생성 전략에 따라서 다른 결과가 나올 수 있다는 점을 알아두자.

### ThisTargetTest

```java
/**
 * application.properties
 * spring.aop.proxy-target-class=false
 * true: 항상 CGLIB
 * false: 인터페이스가 있으면 JDK, 없으면 CGLIB
 */
@Slf4j
@SpringBootTest(properties = "spring.aop.proxy-target-class=false")
@Import(ThisTargetTest.ThisTargetAspect.class)
public class ThisTargetTest {
    @Autowired
    MemberService memberService;

    @Test
    void success(){
        log.info("memberService Proxy={}", memberService.getClass());
        memberService.hello("helloA");
    }
    @Slf4j
    @Aspect
    static class ThisTargetAspect{
        /**
         * this(인터페이스) this는 proxy를 검사한다.
         * JDK 동적 프록시 - proxy를 MemberService 자식으로 만들고 proxy를 검사하기 때문에 어드바이스 적용
         * CGLIB - proxy를 MemberServiceImpl 자식으로 만든다. MemberService -> MemberService -> proxy 어드바이스 적용
         */
        @Around("this(spring.aop.member.MemberService)")
        public Object doThisInterface(ProceedingJoinPoint joinPoint) throws Throwable{
            log.info("[this - interface] {}", joinPoint.getSignature());
            return joinPoint.proceed();
        }

        /**
         * target(인터페이스) target은 실제 target(MemberServiceImpl)으로 판단
         * MemberServiceImpl은 MemberService의 자식이다. JDK 동적 프록시, CGLIB 모두 어드바이스 적용
         */
        @Around("target(spring.aop.member.MemberService)")
        public Object doTargetInterface(ProceedingJoinPoint joinPoint) throws Throwable{
            log.info("[target - interface] {}", joinPoint.getSignature());
            return joinPoint.proceed();
        }
        /**
         * this(MemberServiceImpl) this는 proxy를 검사한다.
         * JDK 동적 프록시 - MemberService 자식 proxy 생성 - 어드바이스 적용 x
         * CGLIB - MemberServiceImpl 자식 proxy 생성 - 어드바이스 적용 o
         */
        @Around("this(spring.aop.member.MemberServiceImpl)")
        public Object doThisConcrete(ProceedingJoinPoint joinPoint) throws Throwable{
            log.info("[this - concrete] {}", joinPoint.getSignature());
            return joinPoint.proceed();
        }

        /**
         * target(MemberServiceImpl) target는 실제 target(MemberServiceImpl)으로 판단
         * MemberServiceImpl은 MemberServiceImpl과 같은 타입이다. JDK 동적 프록시, CGLIB 모두 어드바이스 적용
         */
        @Around("target(spring.aop.member.MemberServiceImpl)")
        public Object doTargetConcrete(ProceedingJoinPoint joinPoint) throws Throwable{
            log.info("[target - concrete] {}", joinPoint.getSignature());
            return joinPoint.proceed();
        }
    }
}
```
- ``properties = {"spring.aop.proxy-target-class=false"}``
  - ``application.properties``에 정하는 대신에 해당 테스트에서만 설정을 임시로 적용한다.
  - 이렇게 하면 각 테스트마다 다른 설정을 손쉽게 적용할 수 있다.
  - false: 스프링이 AOP 프록시를 생성할 때 JDK 동적 프록시를 우선 생성한다. 물론 인터페이스가 없다면 CGLIB를 사용한다.
  - true: 스프링이 AOP 프록시를 생성할 때 CGLIB 프록시를 생성한다. 참고로 이 설정을 생략하면 스프링 부트에서 기본으로 CGLIB를 사용한다.

JDK 동적 프록시 사용
```text
memberService Proxy=class jdk.proxy3.$Proxy52
[target - concrete] String spring.aop.member.MemberService.hello(String)
[target - interface] String spring.aop.member.MemberService.hello(String)
[this - interface] String spring.aop.member.MemberService.hello(String)
```
- JDK 동적 프록시를 사용하면 this(spring.aop.member.MemberServiceImpl) 로 지정한 ```[this - concrete]```부분이 출력되지 
  않는 것을 확인할 수 있다.

CGLIB 사용
```text
memberService Proxy=class spring.aop.member.MemberServiceImpl$$SpringCGLIB$$0
[target - concrete] String spring.aop.member.MemberServiceImpl.hello(String)
[target - interface] String spring.aop.member.MemberServiceImpl.hello(String)
[this - concrete] String spring.aop.member.MemberServiceImpl.hello(String)
[this - interface] String spring.aop.member.MemberServiceImpl.hello(String)
```

