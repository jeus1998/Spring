package spring.aop.pointcut;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import spring.aop.member.MemberServiceImpl;

import java.lang.reflect.Method;

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
}
