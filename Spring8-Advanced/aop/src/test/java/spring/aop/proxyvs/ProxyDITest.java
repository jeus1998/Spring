package spring.aop.proxyvs;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import spring.aop.member.MemberService;
import spring.aop.member.MemberServiceImpl;
import spring.aop.proxyvs.code.ProxyDIAspect;

@Slf4j
// @SpringBootTest(properties = {"spring.aop.proxy-target-class=false"}) // JDK 동적 프록시
@SpringBootTest(properties = {"spring.aop.proxy-target-class=true"})  // CGLIB
@Import(ProxyDIAspect.class)
public class ProxyDITest {
    @Autowired
    MemberService memberService; // JDK 동적 프록시 OK, CGLIB OK
    @Autowired
    MemberServiceImpl memberServiceImpl; // JDK 동적 프록시 X, CGLIB OK

    @Test
    void go(){
        log.info("memberService class={}", memberService.getClass());
        log.info("memberServiceImpl class={}", memberServiceImpl.getClass());
        memberServiceImpl.hello("hello");
    }
}
