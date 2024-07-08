package spring.aop.exam;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

@Slf4j
@SpringBootTest
class ExamServiceTest {

    @Autowired
    ExamService examService;

    @Test
    void test(){
        for(int i = 0; i < 5; i++){
            examService.request("data" + i);
        }
    }
}