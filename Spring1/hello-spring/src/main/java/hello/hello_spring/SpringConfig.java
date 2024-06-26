package hello.hello_spring;

import hello.hello_spring.aop.TimeTraceAop;
import hello.hello_spring.repository.*;
import hello.hello_spring.service.MemberService;
import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * 스프링 빈 수동 등록 방법 (자바 코드로 직접 스프링 빈 등록)
 */
@Configuration
public class SpringConfig {
    // private DataSource dataSource;
    // private EntityManager em;
    /**
     * 스프링 데이터 jpa가 자동으로 해당 리포지토리를 빈으로 등록을 해줘서 바로 주입
     */
    private final MemberRepository memberRepository;
    public SpringConfig(DataSource dataSource, EntityManager em, MemberRepository memberRepository) {
        // this.dataSource = dataSource;
        // this.em = em;
        this.memberRepository = memberRepository;
    }

    @Bean
    public MemberService memberService(){
        return new MemberService(memberRepository);
    }

    /**
     * OCP(Open-Closed Principle 개방 - 폐쇄 원칙 )
     * 확장에는 열려있고, 수정, 변경에는 닫혀있다.
     * 스프링의 DI를 사용하면 기존 코드를 전혀 손대지 않고 설정만으로 구현 클래스를 변경 할 수 있다.
     */
   /* @Bean
    public MemberRepository memberRepository(){
        // return new MemoryMemberRepository();
        // return new JdbcMemberRepository(dataSource);
        // return new JdbcTemplateMemberRepository(dataSource);
        // return new JpaMemberRepository(em);  // JPA는 EntityManager 주입
    }*/


    /**
     * 시간 측정 로직 빈으로 등록
     */
   /* @Bean
    public TimeTraceAop timeTraceAop(){
        return new TimeTraceAop();
    }*/

}
