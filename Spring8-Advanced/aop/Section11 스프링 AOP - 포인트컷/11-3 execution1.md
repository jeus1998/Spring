# execution1

### execution 문법 

- ``execution(modifiers-pattern? ret-type-pattern declaring-type-pattern?name-pattern(param-pattern) throws-pattern?)``
- ``execution(접근제어자? 반환타입 선언타입?메서드이름(파라미터) 예외?)``
- 메소드 실행 조인 포인트를 매칭한다.
- ?는 생략할 수 있다.
- ``*``같은 패턴을 지정할 수 있다.

### ExecutionTest - 추가

exactMatch
```java
@Test
void exactMatch(){
    // public java.lang.String spring.aop.member.MemberServiceImpl.hello(java.lang.String)
    pointcut.setExpression("execution(public String spring.aop.member.MemberServiceImpl.hello(String))");
    assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
}
```
- ``MemberServiceImpl.hello(String)``메서드와 가장 정확하게 모든 내용이 매칭되는 표현식
- ``AspectJExpressionPointcut``에 ``pointcut.setExpression``을 통해서 포인트컷 표현식을 적용할 수 있다.
- ``pointcut.matches(메서드, 대상 클래스)``를 실행하면 지정한 포인트컷 표현식의 매칭 여부를 ``true, false``로 반환한다.
- 매칭 조건 
  - 접근제어자?: public
  - 반환타입: String
  - 선언타입?: spring.aop.member.MemberServiceImpl
  - 메서드이름: hello
  - 파라미터: (String)
  - 예외?: 생략
- ``MemberServiceImpl.hello(String)``메서드와 포인트컷 표현식의 모든 내용이 정확하게 일치
- ``true``를 반환


allMatch
```java
@Test
void allMatch(){
    pointcut.setExpression("execution(* *(..))");
    assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
}
```
- 가장 많이 생략한 포인트컷
- 매칭 조건 
  - 접근제어자?: 생략
  - 반환타입: ```*```
  - 선언타입?: 생략
  - 메서드이름: ```*```
  - 파라미터: (..)
  - 예외?: 없음
- ```*```은 아무 값이 들어와도 된다는 뜻이다.
- 파라미터에서 ```..```은 파라미터의 타입과 파라미터 수가 상관없다는 뜻이다.


메서드 이름 매칭 관련 포인트컷
```java
/**
 * 반환타입 * 
 * 메서드 이름 hello
 * 파라미터 (..) 타입, 파라미터 수가 상관 x 
 */
@Test
void nameMatch(){
    pointcut.setExpression("execution(* hello(..))");
    assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
}

/**
 * 반환타입 * 
 * 메서드 이름 hel*  - * like 같이 동작 
 * 파라미터 (..) 타입, 파라미터 수가 상관 x
 */
@Test
void nameMatchStar1(){
    pointcut.setExpression("execution(* hel*(..))");
    assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
}

/**
 * 반환타입 * 
 * 메서드 이름 *el*
 * 파라미터 (..) 타입, 파라미터 수가 상관 x 
 */
@Test
void nameMatchStar2(){
    pointcut.setExpression("execution(* *el*(..))");
    assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
}

/**
 * 반환타입 * 
 * 메서드 이름 nono
 * 파라미터 (..) 타입, 파라미터 수가 상관 x 
 * 매핑 x nono 메서드는 없다.
 */
@Test
void nameMatchStar3(){
    pointcut.setExpression("execution(* nono(..))");
    assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isFalse();
}
```
- 메서드 이름 앞 뒤에 ```*```을 사용해서 매칭할 수 있다.


패키지 매칭 관련 포인트컷
```java
/**
 * 반환타입 *
 * 패키지명 spring.aop.member.MemberServiceImpl
 * 메서드 이름 hello
 * 파라미터 (..) 타입, 파라미터 수가 상관 x
 */
@Test
void packageExactMatch1(){
    pointcut.setExpression("execution(* spring.aop.member.MemberServiceImpl.hello(..))");
    assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
}

/**
 * 반환타입 *
 * 패키지명 spring.aop.member.* 
 * 메서드 이름 *
 * 파라미터 (..) 타입, 파라미터 수가 상관 x
 */
@Test
void packageExactMatch2() {
    pointcut.setExpression("execution(* spring.aop.member.*.*(..))");
    assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
}

/**
 * 반환타입 *
 * 패키지명 spring.aop.* 
 * 메서드 이름 *
 * 파라미터 (..) 타입, 파라미터 수가 상관 x
 * 패키지명이 틀려서 매칭 x - .은 정확하게 해당 위치의 패키지 / ..은 해당 위치의 패키지와 그 하위 패키지도 포함 
 */
@Test
void packageExactFalse() {
    pointcut.setExpression("execution(* spring.aop.*.*(..))");
    assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isFalse();
}

/**
 * 반환타입 *
 * 패키지명 spring.aop.member..* member 패키지와 그 하위 패키지도 포함 
 * 메서드 이름 * 
 * 파라미터 (..) 타입, 파라미터 수가 상관 x
 */
@Test
void packageMatchSubPackage1(){
    pointcut.setExpression("execution(* spring.aop.member..*.*(..))");
    assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
}

/**
 * 반환타입 *
 * 패키지명 spring.aop..* member 패키지와 그 하위 패키지도 포함 
 * 메서드 이름 * 
 * 파라미터 (..) 타입, 파라미터 수가 상관 x
 */
@Test
void packageMatchSubPackage2(){
    pointcut.setExpression("execution(* spring.aop..*.*(..))");
    assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
}
```
-  ``hello.aop.member.*(1).*(2)``
  - (1): 타입
  - (2): 메서드 이름
- 패키지에서 ```. , ..```의 차이를 이해해야 한다.
  -  ``.``: 정확하게 해당 위치의 패키지 
  - ``..``: 해당 위치의 패키지와 그 하위 패키지도 포함


