package hello.core;

import hello.core.member.MemberRepository;
import hello.core.member.MemoryMemberRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

/**
 * @ComponentScan -> @Component가 붙은 클래스를 전부 스프링 빈으로 등록한다.
 * 현재 AppConfig에는 @Configuration이 붙어 있다. @Configuration에는 @Component가 안에 들어있다.
 * 그래서 AppConfig가 대상에 있으면 자동 스캔과 수동 등록에 대한 충돌이 일어난다.
 * -> excludeFilters 사용해서 제외 시켜준다.
 */
@Configuration
@ComponentScan(
        excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Configuration.class)
)
public class AutoAppConfig {
    // @Bean(name = "memoryMemberRepository")
    public MemberRepository memberRepository(){
        return new MemoryMemberRepository();
    }
}
