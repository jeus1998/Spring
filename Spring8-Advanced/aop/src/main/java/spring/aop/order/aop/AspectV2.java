package spring.aop.order.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * 포인트컷 분리
 * 장점1 다른 어드바이스에서도 해당 포인트컷을 사용 가능하다 -> 재사용성이 높아진다.
 * 장점2 모듈화를 통한 수정, 유지보수에 대한 편의성
 * 장점3 해당 포인트컷의 (적용범위)의미를 메서드 이름으로 개발자가 인식 가능
 */
@Slf4j
@Aspect
public class AspectV2 {

    // spring.aop.order 패키지와 하위 패키지 전부 포함
    @Pointcut("execution(* spring.aop.order..*(..))")
    private void allOrder(){} // pointcut signature

    @Around("allOrder()")
    public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable{
        log.info("[log] {}", joinPoint.getSignature()); // join point 시그니처
        return joinPoint.proceed();
    }
}
