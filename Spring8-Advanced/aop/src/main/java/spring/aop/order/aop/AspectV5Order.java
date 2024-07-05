package spring.aop.order.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;

/**
 * Advice 순서
 * 어드바이스는 기본적으로 순서를 보장하지 않는다.
 * @Order 통해서 어드바이스 순서를 적용
 * 문제는 이것을 어드바이스 단위가 아니라 클래스 단위로만 적용할 수 있다.
 * 즉 그래서 하나의 애스펙트에 여러 어드바이스가 있으면 순서를 보장 받을 수 없다.
 * 따라서 애스펙트를 별도의 클래스로 분리하고 각각 스프링 빈으로 등록한다.
 */
@Slf4j
public class AspectV5Order {
    @Aspect
    @Order(2)
    public static class LogAspect{
        @Around("spring.aop.order.aop.Pointcuts.allOrder()")
        public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable{
           log.info("[log] {}", joinPoint.getSignature());
           return joinPoint.proceed();
        }
    }
    @Aspect
    @Order(1)
    public static class TxAspect{
        @Around("spring.aop.order.aop.Pointcuts.orderAndService()")
        public Object doTransaction(ProceedingJoinPoint joinPoint) throws Throwable{
            try {
               log.info("[트랜잭션 시작] {}", joinPoint.getSignature());
               Object result = joinPoint.proceed();
               log.info("[트랜잭션 커밋] {}", joinPoint.getSignature());
               return result;
            }
            catch (Exception e){
               log.info("[트랜잭션 롤백] {}", joinPoint.getSignature());
               throw e;
            }
            finally {
               log.info("[리소스 릴리즈] {}", joinPoint.getSignature());
            }
        }
    }
}
