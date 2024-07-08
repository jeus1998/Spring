# @target, @within

정의
- ```@target```: 실행 객체의 클래스에 주어진 타입의 애노테이션이 있는 조인 포인트
- ```@within```: 주어진 애노테이션이 있는 타입 내 조인 포인트


@target vs @within
- ```@target```은 인스턴스의 모든 메서드를 조인 포인트로 적용한다. 
  - ```@target```은 부모 클래스의 메서드까지 어드바이스를 다 적용한다. 
- ```@within```은 해당 타입 내에 있는 메서드만 조인 포인트로 적용한다.
  - ```@within`` 은 자기 자신의 클래스에 정의된 메서드에만 어드바이스를 적용

![1.png](Image%2F1.png)

### AtTargetAtWithinTest

```java
@Slf4j
@SpringBootTest
@Import(AtTargetAtWithinTest.Config.class)
public class AtTargetAtWithinTest {

    @Autowired Child child;
    @Autowired Parent parent;
    @Test
    @DisplayName("child test")
    void test1(){
        log.info("child Proxy={}", child.getClass());
        child.childMethod();  // 자식만 있는 메서드
        child.parentMethod(); // 부모, 자식 모두 있는 메서드
    }

    @Test
    @DisplayName("parent test")
    void test2(){
        log.info("parent Proxy={}", parent.getClass());
        parent.parentMethod();
    }


    static class Config{

        // @Aspect(Advisor) 스프링 빈으로 등록 -> 빈 후처리기 @Aspect 붙은 스프링 빈을 찾아서 Advisor 등록(Advisor 빌더)
        @Bean
        AtTargetAtWithinAspect.Target target(){
            return new AtTargetAtWithinAspect.Target();
        }

        @Bean
        AtTargetAtWithinAspect.Within within(){
            return new AtTargetAtWithinAspect.Within();
        }
        @Bean
        Child child(){
            return new Child();
        }

        @Bean
        Parent parent(){
            return new Parent();
        }
    }
    static class Parent{
        public void parentMethod(){}  // 부모와 자식 모두 있는 메서드
    }
    @ClassAop // class 레벨에 붙이는 애노테이션
    static class Child extends Parent{
        public void childMethod(){}  // 자식만 있는 메서드
    }
    @Slf4j
    static class AtTargetAtWithinAspect{

        // @Pointcut
        @Pointcut("execution(* spring.aop..*(..))")
        public void executionPointCut() {};
        @Slf4j
        @Aspect
        @Order(1)
        static class Target{
            // @target: 인스턴스 기준으로 모든 메서드의 조인 포인트를 선정. 상속 받은 부모 타입의 메서드도 적용
            @Around("spring.aop.pointcut.AtTargetAtWithinTest.AtTargetAtWithinAspect.executionPointCut() && @target(spring.aop.member.annotation.ClassAop)")
            public Object atTarget(ProceedingJoinPoint joinPoint) throws Throwable{
               log.info("[@target] {}", joinPoint.getSignature());
               return joinPoint.proceed();
            }
        }
        @Slf4j
        @Aspect
        @Order(2)
        static class Within{
            // @within: 선택된 클래스 내부에 있는 메서드만 조인 포인트로 선정. 상속 받은 부모 타입의 메서드 적용되지 않음
            @Around("spring.aop.pointcut.AtTargetAtWithinTest.AtTargetAtWithinAspect.executionPointCut() && @within(spring.aop.member.annotation.ClassAop)")
            public Object atWithin(ProceedingJoinPoint joinPoint) throws Throwable{
               log.info("[@within] {}", joinPoint.getSignature());
               return joinPoint.proceed();
            }
        }
    }
}
```

test1 실행 결과
```text
child Proxy=class spring.aop.pointcut.AtTargetAtWithinTest$Child$$SpringCGLIB$$0
[@target] void spring.aop.pointcut.AtTargetAtWithinTest$Child.childMethod()
[@within] void spring.aop.pointcut.AtTargetAtWithinTest$Child.childMethod()
[@target] void spring.aop.pointcut.AtTargetAtWithinTest$Parent.parentMethod()
```
- parentMethod()는 Child 클래스에 클래스에 정의되어 있지 않기 때문에 ```@within```에서 AOP 적용 대상이 되지 않는다.

test2 실행 결과
```text
parent Proxy=class spring.aop.pointcut.AtTargetAtWithinTest$Parent$$SpringCGLIB$$0
```
- Parent 클래스는 execution 표현식에 통과하여 proxy 객체가 빈으로 등록되었지만 ```@target, @within```은 ```@ClassAop```애노테이션을 기반으로 
  AOP를 적용하기 때문에 적용 대상이 아니다. 

참고
- ```@target , @within```지시자는 파라미터 바인딩에서 함께 사용된다.

주의 
- 다음 포인트컷 지시자는 단독으로 사용하면 안된다. ``args, @args, @target``
- 이번 예제를 보면 ``execution(* hello.aop..*(..))``를 통해 적용 대상을 줄여준 것을 확인할 수 있다. 
- ``args , @args , @target``은 실제 객체 인스턴스가 생성되고 실행될 때 어드바이스 적용 여부를 확인할 수 있다.
- 실행 시점에 일어나는 포인트컷 적용 여부도 결국 프록시가 있어야 실행 시점에 판단할 수 있다.
- 프록시가 없다면 판단 자체가 불가능하다. 
- 그런데 스프링 컨테이너가 프록시를 생성하는 시점은 스프링 컨테이너가 만들어지는 애플리케이션 로딩 시점에 적용할 수 있다.
- 따라서 ``args , @args , @target``같은 포인트컷 지시자가 있으면 스프링은 모든 스프링 빈에 AOP를 적용하려고 시도한다.
- 문제는 이렇게 모든 스프링 빈에 AOP 프록시를 적용하려고 하면 스프링이 내부에서 사용하는 빈 중에는 ``final``로 지정된 빈들도 있기 
  때문에 오류가 발생할 수 있다.
- 따라서 이러한 표현식은 최대한 프록시 적용 대상을 축소하는 표현식과 함께 사용해야 한다.

정리
- 축소하는 표현식과 같이 사용하지 않으면 런타임 시점에 적용 대상을 알 수 있기 때문에 일단 프록시 객체로 모드 등록하려고 한다.
- 이때 프록시로 등록하는 과정에서 에러가 발생한다. 
  - final 키워드 (상속 불가능)