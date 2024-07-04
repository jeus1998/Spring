package hello.proxy.common.advice;

import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * 패키지에 주의하자 CGLIB MethodInterceptor 이름과 동일
 * import org.aopalliance.intercept.MethodInterceptor; Advice -> Interceptor -> MethodInterceptor
 * ProxyFactory(프록시 팩토리)가 프록시를 생성할 때 target 실제 호출 객체(원래 pure 객체) 넣어준다.
 * proceed() 호출을 통해서 target 호출
 * 물론 프록시 생성은 프록시 팩토리가 알아서 target(인터페이스 구현체(JDK 동적 프록시), 그냥 CLASS(CGLIB))
 * 클라이언트 -> 프록시 호출 ->  adviceMethodInterceptor or adviceInvocationHandler 호출 -> 내가 구현한 advice -> target 호출 순서
 */
@Slf4j
public class TimeAdvice implements MethodInterceptor {
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        log.info("TimeProxy 실행");
        long startTime = System.currentTimeMillis();

        Object result = invocation.proceed(); // target 호출

        long endTime = System.currentTimeMillis();
        long resultTime = endTime - startTime;

        log.info("TimeProxy 종료 resultTime={}ms", resultTime);
        return result;
    }
}
