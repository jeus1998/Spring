package spring.aop.internalcall;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import spring.aop.internalcall.aop.CallLogAspect;

/**
 * 스프링 부트 2.6부터는 순환 참조를 기본적으로 금지하도록 정책이 변경되었다.
 * 이 문제를 해결하려면 application.properties : spring.main.allow-circular-references=true 추가
 */
@Slf4j
@Import(CallLogAspect.class)
@SpringBootTest(properties = "spring.main.allow-circular-references=true")
class CallServiceV1Test {
    @Autowired
    CallServiceV1 callServiceV1;
    @Test
    void external() {
         callServiceV1.external();
    }
    @Test
    void internal() {
        callServiceV1.internal();
    }
}