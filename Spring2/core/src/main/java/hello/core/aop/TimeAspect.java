package hello.core.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j
// @Component
public class TimeAspect {
    @Around("execution(* hello.core.aop.Example..*(..))")
    public Object time(ProceedingJoinPoint joinPoint) throws Throwable{
        log.info("start={}", System.currentTimeMillis());
        Object result = joinPoint.proceed();
        log.info("end={}", System.currentTimeMillis());
        return result;
    }
}
