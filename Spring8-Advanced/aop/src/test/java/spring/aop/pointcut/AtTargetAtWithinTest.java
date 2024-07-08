package spring.aop.pointcut;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import spring.aop.member.annotation.ClassAop;

@Slf4j
@SpringBootTest
@Import(AtTargetAtWithinTest.Config.class)
public class AtTargetAtWithinTest {

    @Autowired Child child;
    @Autowired Parent parent;
    @Test
    @DisplayName("child test")
    void test1(){
        log.info("child Proxy={}", child.getClass());
        child.childMethod();  // 자식만 있는 메서드
        child.parentMethod(); // 부모, 자식 모두 있는 메서드
    }

    @Test
    @DisplayName("parent test")
    void test2(){
        log.info("parent Proxy={}", parent.getClass());
        parent.parentMethod();
    }


    static class Config{

        // @Aspect(Advisor) 스프링 빈으로 등록 -> 빈 후처리기 @Aspect 붙은 스프링 빈을 찾아서 Advisor 등록(Advisor 빌더)
        @Bean
        AtTargetAtWithinAspect.Target target(){
            return new AtTargetAtWithinAspect.Target();
        }

        @Bean
        AtTargetAtWithinAspect.Within within(){
            return new AtTargetAtWithinAspect.Within();
        }
        @Bean
        Child child(){
            return new Child();
        }

        @Bean
        Parent parent(){
            return new Parent();
        }
    }
    static class Parent{
        public void parentMethod(){}  // 부모와 자식 모두 있는 메서드
    }
    @ClassAop // class 레벨에 붙이는 애노테이션
    static class Child extends Parent{
        public void childMethod(){}  // 자식만 있는 메서드
    }
    @Slf4j
    static class AtTargetAtWithinAspect{

        // @Pointcut
        @Pointcut("execution(* spring.aop..*(..))")
        public void executionPointCut() {};
        @Slf4j
        @Aspect
        @Order(1)
        static class Target{
            // @target: 인스턴스 기준으로 모든 메서드의 조인 포인트를 선정. 상속 받은 부모 타입의 메서드도 적용
            @Around("spring.aop.pointcut.AtTargetAtWithinTest.AtTargetAtWithinAspect.executionPointCut() && @target(spring.aop.member.annotation.ClassAop)")
            public Object atTarget(ProceedingJoinPoint joinPoint) throws Throwable{
               log.info("[@target] {}", joinPoint.getSignature());
               return joinPoint.proceed();
            }
        }
        @Slf4j
        @Aspect
        @Order(2)
        static class Within{
            // @within: 선택된 클래스 내부에 있는 메서드만 조인 포인트로 선정. 상속 받은 부모 타입의 메서드 적용되지 않음
            @Around("spring.aop.pointcut.AtTargetAtWithinTest.AtTargetAtWithinAspect.executionPointCut() && @within(spring.aop.member.annotation.ClassAop)")
            public Object atWithin(ProceedingJoinPoint joinPoint) throws Throwable{
               log.info("[@within] {}", joinPoint.getSignature());
               return joinPoint.proceed();
            }
        }

    }
}
