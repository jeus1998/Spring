# execution2

### 타입 매칭 - 부모 타입 허용

```java
/**
 * 반환타입 *
 * 타입 spring.aop.member.MemberServiceImpl
 * 메서드 이름 *
 * 파라미터 (..)
 */
@Test
void typeExactMatch(){
    pointcut.setExpression("execution(* spring.aop.member.MemberServiceImpl.*(..))");
    assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
}

/**
 * 반환타입 *
 * 타입 spring.aop.member.MemberService - 부모 타입
 * 메서드 이름 *
 * 파라미터 (..)
 */
@Test
void typeMatchSuperType(){
    pointcut.setExpression("execution(* spring.aop.member.MemberService.*(..))");
    assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
}

/**
 * MemberServiceImpl internal() 메서드 MemberService 인터페이스에는 없는 메서드
 * 반환타입 *
 * 타입 spring.aop.member.MemberServiceImpl
 * 메서드 이름 *
 * 파라미터 (..)
 */
@Test
void typeMatchInternal() throws NoSuchMethodException{
    pointcut.setExpression("execution(* spring.aop.member.MemberServiceImpl.*(..))");

    Method internalMethod = MemberServiceImpl.class.getMethod("internal", String.class);
    assertThat(pointcut.matches(internalMethod, MemberServiceImpl.class)).isTrue();
}

/**
 * MemberServiceImpl internal() 메서드 MemberService 인터페이스에는 없는 메서드
 * 반환타입 *
 * 타입 spring.aop.member.MemberService 부모 타입
 * 메서드 이름 *
 * 파라미터 (..)
 * 매칭 실패 부모인 MemberService 에는 internal() 메서드가 없다.
 */
@Test
void typeMatchNoSuperTypeMethodFalse() throws NoSuchMethodException{
    pointcut.setExpression("execution(* spring.aop.member.MemberService.*(..))");

    Method internalMethod = MemberServiceImpl.class.getMethod("internal", String.class);
    assertThat(pointcut.matches(internalMethod, MemberServiceImpl.class)).isFalse();
}
```
- ``execution``에서는 ``MemberService``처럼 부모 타입을 선언해도 그 자식 타입은 매칭된다.

### 파라미터 매칭

```java
 /**
 * 반환타입 *
 * 타입 *
 * 파라미터 (String) 정확하게 String 매개변수 하나 허용
 */
@Test
void argsMatch(){
    pointcut.setExpression("execution(* *(String))");
    assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
}

/**
 * 반환타입 *
 * 타입 *
 * 파라미터 () 파라미터 허용 x
 * 매칭 실패 hello(String param) 메서드는 매개변수가 존재
 */
@Test
void argsMatchNoArgs(){
    pointcut.setExpression("execution(* *())");
    assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isFalse();
}

/**
 * 반환타입 *
 * 타입 *
 * 파라미터 (*) 어떤 타입이든 딱 1개 허용
 */
@Test
void argsMatchStar(){
    pointcut.setExpression("execution(* *(*))");
    assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
}

/**
 * 반환타입 *
 * 타입 *
 * 파라미터 (..) 모든 파라미터 허용
 */
@Test
void argsMatchAll(){
    pointcut.setExpression("execution(* *(..))");
    assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
}

/**
 * 반환타입 *
 * 타입 *
 * 파라미터 (String, ..)
 * (String), (String, xxx) , (String , xxx, xxx) .... 허용
 */
@Test
void argsMatchComplex(){
    pointcut.setExpression("execution(* *(String, ..))");
    assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
}
```
execution 파라미터 매칭 규칙
- (String) : 정확하게 String 타입 파라미터
- () : 파라미터가 없어야 한다.
- (*) : 정확히 하나의 파라미터, 단 모든 타입을 허용한다.
- (*, *) : 정확히 두 개의 파라미터, 단 모든 타입을 허용한다.
- (..) : 숫자와 무관하게 모든 파라미터, 모든 타입을 허용한다. + 파라미터가 없어도 된다.
- (String, ..) : String 타입으로 시작해야 한다. 숫자와 무관하게 모든 파라미터, 모든 타입을 허용한다.
  - ex) (String) , (String, Xxx) , (String, Xxx, Xxx) 허용