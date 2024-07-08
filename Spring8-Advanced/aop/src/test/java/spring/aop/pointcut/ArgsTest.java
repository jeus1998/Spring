package spring.aop.pointcut;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import spring.aop.member.MemberServiceImpl;
import java.lang.reflect.Method;
import static org.assertj.core.api.Assertions.*;

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
