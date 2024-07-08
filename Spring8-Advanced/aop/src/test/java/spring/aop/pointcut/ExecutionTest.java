package spring.aop.pointcut;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import spring.aop.member.MemberServiceImpl;
import java.lang.reflect.Method;
import static org.assertj.core.api.Assertions.*;

@Slf4j
public class ExecutionTest {
    AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
    Method helloMethod;

    /**
     * 리플렉션 사용 MemberServiceImpl 클래스의 이름이 hello, 파라미터 타입이 String 메서드를 찾는다.
     *  Method helloMethod: Method 타입이다. hello 메소드에 대한 정보를 담게 된다.
     * @throws NoSuchMethodException 리플렉션을 사용하여 메소드를 찾을 때 해당 메소드를 찾을 수 없을 경우 발생하는 예외
     */
    @BeforeEach
    public void init() throws NoSuchMethodException{
        helloMethod = MemberServiceImpl.class.getMethod("hello", String.class);
    }
    @Test
    void printMethod(){
        // public java.lang.String spring.aop.member.MemberServiceImpl.hello(java.lang.String)
        log.info("helloMethod = {}", helloMethod);
    }

    /**
     * 가장 정확하게 매칭되는 포인트컷
     */
    @Test
    void exactMatch(){
        // public java.lang.String spring.aop.member.MemberServiceImpl.hello(java.lang.String)
        pointcut.setExpression("execution(public String spring.aop.member.MemberServiceImpl.hello(String))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    /**
     * 가장 많이 생략한 포인트컷
     */
    @Test
    void allMatch(){
        pointcut.setExpression("execution(* *(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    // 메서드 이름 매칭 관련 포인트컷

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

    // 패키지 매칭 관련 포인트컷

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
}
