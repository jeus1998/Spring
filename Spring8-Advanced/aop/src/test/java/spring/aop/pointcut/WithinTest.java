package spring.aop.pointcut;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import spring.aop.member.MemberServiceImpl;
import java.lang.reflect.Method;
import static org.assertj.core.api.Assertions.*;

/**
 * within 포인트컷 지시자 타입 매칭으로 조인포인트 매칭한다.
 */
public class WithinTest {
    AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
    Method helloMethod;
    @BeforeEach
    void init() throws NoSuchMethodException{
        helloMethod = MemberServiceImpl.class.getMethod("hello", String.class);
    }

    /**
     * within(spring.aop.member.MemberServiceImpl)
     */
    @Test
    void withinExact(){
        pointcut.setExpression("within(spring.aop.member.MemberServiceImpl)");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    /**
     * within(spring.aop.member.*Service*)
     */
    @Test
    void withinStar(){
        pointcut.setExpression("within(spring.aop.member.*Service*)");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    /**
     * within(spring.aop..*)
     */
    @Test
    void withinSubPackage(){
        pointcut.setExpression("within(spring.aop..*)");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    // 주의 within 사용시 표현식에 부모 타입 지정 x 정확하게 타입이 맞아야 한다.

    /**
     * within(spring.aop.member.MemberService)
     * 타입 매칭 실패 withing 은 부모타입이 아닌 정확한 타입을 선정해야 한다.
     */
    @Test
    @DisplayName("타겟의 타입에만 직접 적용, 인터페이스를 선정하면 안된다.")
    void withinSuperTypeFalse(){
        pointcut.setExpression("within(spring.aop.member.MemberService)");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isFalse();
    }

    /**
     * execution(* spring.aop.member.MemberService.*(..))
     * 타입 매칭 성공 execution은 부모타입인 인터페이스도 타입으로 선정 가능하다.
     * 주의점 부모(인터페이스)에 있는 메서드만 가능하다. internal() x
     */
    @Test
    @DisplayName("execution은 타입 기반, 인터페이스를 선정 가능.")
    void executionSuperTypeTrue(){
        pointcut.setExpression("execution(* spring.aop.member.MemberService.*(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }
}
