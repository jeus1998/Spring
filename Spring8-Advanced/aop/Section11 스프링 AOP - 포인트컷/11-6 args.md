# args

- ``args``: 인자가 주어진 타입의 인스턴스인 조인 포인트로 매칭
- 기본 문법은 ``execution``의 ``args``부분과 같다.

execution과 args의 차이점
- ``execution``은 파라미터 타입이 정확하게 매칭되어야 한다. 
- ``execution``은 클래스에 선언된 정보를 기반으로 판단한다.
- ``args``는 부모 타입을 허용한다. ``args``는 실제 넘어온 파라미터 객체 인스턴스를 보고 판단한다.


### ArgsTest

```java
/**
 * String 부모 Object, java.io.Serializable 인터페이스
 */
public class ArgsTest {
    Method helloMethod;
    @BeforeEach
    public void init() throws NoSuchMethodException{
        helloMethod = MemberServiceImpl.class.getMethod("hello", String.class);
    }
    private AspectJExpressionPointcut pointcut(String expression){
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(expression);
        return pointcut;
    }

    @Test
    void args(){
        // hello(String) 매칭
        assertThat(pointcut("args(String)")
                .matches(helloMethod, MemberServiceImpl.class)).isTrue();
        // hello(Object) 매칭
        assertThat(pointcut("args(Object)")
                .matches(helloMethod, MemberServiceImpl.class)).isTrue();
        // hello() 매칭 -> 매칭 실패
        assertThat(pointcut("args()")
                .matches(helloMethod, MemberServiceImpl.class)).isFalse();
        // hello(..) 매칭
        assertThat(pointcut("args(..)")
                .matches(helloMethod, MemberServiceImpl.class)).isTrue();
        // hello(*) 매칭
        assertThat(pointcut("args(*)")
                .matches(helloMethod, MemberServiceImpl.class)).isTrue();
        // hello(String, ..) 매칭
        assertThat(pointcut("args(String, ..)")
                .matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }
    /**
     *  execution: jvm 메서드 영역의 클래스 메타 정보로 컴파일 시점에 메서드 시그니처 정보를 가지고 타입이 같은지 판단 (정적)
     *  args: 런타임에 넘어온 매개변수에 대한 내부에서 instanceOf 같은 동작 예상 (동적)
     */
    @Test
    void argsVSExecution(){
        // Args 부모 타입 매칭 허용 (동적)
        assertThat(pointcut("args(String)")
                .matches(helloMethod, MemberServiceImpl.class)).isTrue();
        assertThat(pointcut("args(java.io.Serializable)")
                .matches(helloMethod, MemberServiceImpl.class)).isTrue();
        assertThat(pointcut("args(Object)")
                .matches(helloMethod, MemberServiceImpl.class)).isTrue();

        // Execution 정확한 타입 매칭 (정적)
        assertThat(pointcut("execution(* *(String))")
                       .matches(helloMethod, MemberServiceImpl.class)).isTrue();
        assertThat(pointcut("execution(* *(java.io.Serializable))")
                .matches(helloMethod, MemberServiceImpl.class)).isFalse(); // 매칭 실패
        assertThat(pointcut("execution(* *(Object))")
                .matches(helloMethod, MemberServiceImpl.class)).isFalse(); // 매칭 실패
    }
}
```
- 자바가 기본으로 제공하는 ``String``은 ``Object``, ``java.io.Serializable``의 하위 타입이다.
- 정적으로 클래스에 선언된 정보만 보고 판단하는 ``execution(* *(Object))``는 매칭에 실패한다.
- 동적으로 실제 파라미터로 넘어온 객체 인스턴스로 판단하는 ``args(Object)``는 매칭에 성공한다. (부모 타입 허용)

참고
- ``args``지시자는 단독으로 사용되기 보다는 파라미터 바인딩에서 주로 사용된다.