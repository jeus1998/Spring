package hello.hello_spring;

import hello.hello_spring.repository.JdbcMemberRepository;
import hello.hello_spring.repository.MemberRepository;
import hello.hello_spring.repository.MemoryMemberRepository;
import hello.hello_spring.service.MemberService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * 스프링 빈 수동 등록 방법 (자바 코드로 직접 스프링 빈 등록)
 */
@Configuration
public class SpringConfig {
    private DataSource dataSource;
    public SpringConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    public MemberService memberService(){
        return new MemberService(memberRepository());
    }

    /**
     * OCP(Open-Closed Principle 개방 - 폐쇄 원칙 )
     * 확장에는 열려있고, 수정, 변경에는 닫혀있다.
     * 스프링의 DI를 사용하면 기존 코드를 전혀 손대지 않고 설정만으로 구현 클래스를 변경 할 수 있다.
     */
    @Bean
    public MemberRepository memberRepository(){
        // return new MemoryMemberRepository();
        return new JdbcMemberRepository(dataSource);
    }

}
