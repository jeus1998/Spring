package hello.proxy.myaspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class AspectExample {
    @Around("execution(* hello.proxy.myaspect.AspectTest.A.yes_log(..)))")
    public Object execute(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = proceedingJoinPoint.proceed();
        log.info("time={}", System.currentTimeMillis() - start);
        return result;
    }
}
