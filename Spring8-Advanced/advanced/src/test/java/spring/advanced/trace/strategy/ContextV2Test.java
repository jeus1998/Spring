package spring.advanced.trace.strategy;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import spring.advanced.trace.strategy.code.strategy.ContextV2;

@Slf4j
public class ContextV2Test {

    /**
     * 전략 패턴 적용
     */
    @Test
    void strategyV1(){
        ContextV2 context = new ContextV2();
        context.execute(()->{
            log.info("비즈니스 로직1 실행");
        });

        context.execute(()->{
            log.info("비즈니스 로직2 실행");
        });
    }
}
