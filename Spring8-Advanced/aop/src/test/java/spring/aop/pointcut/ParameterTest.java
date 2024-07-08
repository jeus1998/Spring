package spring.aop.pointcut;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import spring.aop.member.MemberService;
import spring.aop.member.annotation.ClassAop;
import spring.aop.member.annotation.MethodAop;

@Slf4j
@SpringBootTest
@Import(ParameterTest.ParameterAspect.class)
public class ParameterTest {

    @Autowired
    MemberService memberService;

    @Test
    void success(){
        log.info("memberService Proxy={}", memberService.getClass());
        memberService.hello("helloA");
    }

    @Aspect
    static class ParameterAspect{
        @Pointcut("execution(* spring.aop.member..*.*(..))")
        private void allMember() {}

        /**
         * getArgs() 사용
         */
        @Around("allMember()")
        public Object logArgs1(ProceedingJoinPoint joinPoint) throws Throwable{
            Object arg1 = joinPoint.getArgs()[0];
            log.info("[logArgs1]{}, args={}", joinPoint.getSignature(), arg1);
            return joinPoint.proceed();
        }

        /**
         * args(arg, ...) 사용
         */
        @Around("allMember() && args(arg, ..)")
        public Object logArgs2(ProceedingJoinPoint joinPoint, String arg) throws Throwable{
            log.info("[logArgs2]{}, args={}", joinPoint.getSignature(), arg);
            return joinPoint.proceed();
        }
        /**
         * args(arg, ...) 사용
         */
        @Before("allMember() && args(arg, ..)")
        public void logArgs3(String arg){
            log.info("[logArgs3] arg={}", arg);
        }

        /**
         * this(obj) 사용 프록시 객체
         */
        @Before("allMember() && this(obj)")
        public void thisArgs(JoinPoint joinPoint, MemberService obj){
            log.info("[this]{}, obj={}", joinPoint.getSignature(), obj.getClass());
        }

        /**
         * target(obj) 사용 실제 target 호출 대상
         */
        @Before("allMember() && target(obj)")
        public void targetArgs(JoinPoint joinPoint, MemberService obj){
            log.info("[target]{}, obj={}", joinPoint.getSignature(), obj.getClass());
        }

        /**
         * @target(annotation) 사용
         * @target, @within은 클래스 레벨에 붙은 애노테이션만 해당해서 필터
         */
        @Before("allMember() && @target(annotation)")
        public void atTarget(JoinPoint joinPoint, ClassAop annotation){
           log.info("[@target]{}, obj={}", joinPoint.getSignature(), annotation);
        }
        /**
         * @within(annotation) 사용
         * @target, @within은 클래스 레벨에 붙은 애노테이션만 해당해서 필터
         */
        @Before("allMember() && @within(annotation)")
        public void atWithin(JoinPoint joinPoint, ClassAop annotation){
          log.info("[@within]{}, obj={}", joinPoint.getSignature(), annotation);
        }

        /**
         * @annotation(annotation) 사용
         * @annotation은 메서드 레벨에 붙은 애노테이션만 해당해서 필터
         */
        @Before("allMember() && @annotation(annotation)")
        public void atAnnotation(JoinPoint joinPoint, MethodAop annotation){
          log.info("[@annotation]{}, annotationValue={}", joinPoint.getSignature(), annotation.value());
        }

    }
}
