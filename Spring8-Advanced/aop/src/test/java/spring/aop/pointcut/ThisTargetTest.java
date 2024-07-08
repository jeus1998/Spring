package spring.aop.pointcut;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import spring.aop.member.MemberService;

/**
 * application.properties
 * spring.aop.proxy-target-class=false
 * true: 항상 CGLIB
 * false: 인터페이스가 있으면 JDK, 없으면 CGLIB
 */
@Slf4j
@SpringBootTest(properties = "spring.aop.proxy-target-class=true")
@Import(ThisTargetTest.ThisTargetAspect.class)
public class ThisTargetTest {
    @Autowired
    MemberService memberService;

    @Test
    void success(){
        log.info("memberService Proxy={}", memberService.getClass());
        memberService.hello("helloA");
    }
    @Slf4j
    @Aspect
    static class ThisTargetAspect{
        /**
         * this(인터페이스) this는 proxy를 검사한다.
         * JDK 동적 프록시 - proxy를 MemberService 자식으로 만들고 proxy를 검사하기 때문에 어드바이스 적용
         * CGLIB - proxy를 MemberServiceImpl 자식으로 만든다. MemberService -> MemberService -> proxy 어드바이스 적용
         */
        @Around("this(spring.aop.member.MemberService)")
        public Object doThisInterface(ProceedingJoinPoint joinPoint) throws Throwable{
            log.info("[this - interface] {}", joinPoint.getSignature());
            return joinPoint.proceed();
        }

        /**
         * target(인터페이스) target은 실제 target(MemberServiceImpl)으로 판단
         * MemberServiceImpl은 MemberService의 자식이다. JDK 동적 프록시, CGLIB 모두 어드바이스 적용
         */
        @Around("target(spring.aop.member.MemberService)")
        public Object doTargetInterface(ProceedingJoinPoint joinPoint) throws Throwable{
            log.info("[target - interface] {}", joinPoint.getSignature());
            return joinPoint.proceed();
        }
        /**
         * this(MemberServiceImpl) this는 proxy를 검사한다.
         * JDK 동적 프록시 - MemberService 자식 proxy 생성 - 어드바이스 적용 x
         * CGLIB - MemberServiceImpl 자식 proxy 생성 - 어드바이스 적용 o
         */
        @Around("this(spring.aop.member.MemberServiceImpl)")
        public Object doThisConcrete(ProceedingJoinPoint joinPoint) throws Throwable{
            log.info("[this - concrete] {}", joinPoint.getSignature());
            return joinPoint.proceed();
        }

        /**
         * target(MemberServiceImpl) target는 실제 target(MemberServiceImpl)으로 판단
         * MemberServiceImpl은 MemberServiceImpl과 같은 타입이다. JDK 동적 프록시, CGLIB 모두 어드바이스 적용
         */
        @Around("target(spring.aop.member.MemberServiceImpl)")
        public Object doTargetConcrete(ProceedingJoinPoint joinPoint) throws Throwable{
            log.info("[target - concrete] {}", joinPoint.getSignature());
            return joinPoint.proceed();
        }
    }
}
