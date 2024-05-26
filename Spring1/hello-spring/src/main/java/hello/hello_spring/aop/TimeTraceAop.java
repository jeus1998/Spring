package hello.hello_spring.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * AOP를 사용하려면 @Aspect
 * 스프링 빈으로 등록 해야함 보통 이런 공통 관심 사항은 수동 등록 빈으로 사용 하지만 컴포넌트 스캔도 해도 괜찮다.
 * @Around 해당 공통 로직이 적용될 target을 지정함
 * 메서드 로직을 인터셉팅하는 방식으로 동작
 */
@Aspect
@Component
public class TimeTraceAop {

    // @Around("execution(* hello.hellospring..*(..)) && !target(hello.hellospring.SpringConfig)")
    // TimeTraceAop를 직접 등록할 때 SpirngConfig를 제외하여 순환참조 오류를 막는다.
    @Around("execution(* hello.hello_spring..*(..))")
    public Object execute (ProceedingJoinPoint joinPoint) throws Throwable{
        long start = System.currentTimeMillis();
        System.out.println("START: " + joinPoint.toString());
        try {
          return joinPoint.proceed(); // joinPoint.proceed() 다음 메서드로 진행
        }
        finally {
            long finish = System.currentTimeMillis();
            long timeMs = finish - start;
            System.out.println("END: " + joinPoint.toString() + " " + timeMs + "ms");
        }
    }
}
