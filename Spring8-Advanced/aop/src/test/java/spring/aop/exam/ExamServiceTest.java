package spring.aop.exam;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import spring.aop.exam.aop.RetryAspect;
import spring.aop.exam.aop.TraceAspect;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

@Slf4j
@Import({TraceAspect.class, RetryAspect.class})
@SpringBootTest
class ExamServiceTest {

    @Autowired
    ExamService examService;
    @Test
    void test(){
        for(int i = 0; i < 5; i++){
            log.info("client request i={}", i);
            examService.request("data" + i);
        }
    }
}