package spring.advanced.trace.strategy;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import spring.advanced.trace.strategy.code.template.CallBack;
import spring.advanced.trace.strategy.code.template.TimeLogTemplate;

@Slf4j
public class TemplateCallBackTest {

    /**
     * 템플릿 콜백 패턴 적용
     */
    @Test
    void templateCallBackTest(){
        TimeLogTemplate template = new TimeLogTemplate();

        template.execute(()->{
            log.info("비즈니스 로직1 실행");
        });

        template.execute(new CallBack() {
            @Override
            public void call() {
                log.info("비즈니스 로직2 실행");
            }
        });
    }
}
