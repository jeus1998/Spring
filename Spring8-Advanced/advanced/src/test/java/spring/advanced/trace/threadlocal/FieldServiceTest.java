package spring.advanced.trace.threadlocal;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import spring.advanced.trace.threadlocal.code.FieldService;

@Slf4j
public class FieldServiceTest {
    private final FieldService fieldService = new FieldService();
    @Test
    void field(){
        log.info("main start");

        Runnable userA = () ->{
            fieldService.logic("userA");
        };

        Runnable userB = () ->{
            fieldService.logic("userB");
        };

        Thread threadA = new Thread(userA);
        threadA.setName("thread-A");
        Thread threadB = new Thread(userB);
        threadB.setName("thread-B");

        threadA.start();
        sleep(100); // 동시성 문제 발생 X
        threadB.start();

        sleep(3000); // 메인 쓰레드 종료 대기
        log.info("main exit");
    }
    private void sleep(int mills) {
        try {
            Thread.sleep(mills);
        }
        catch (InterruptedException e){
            log.info("sleep fail{}", e);
        }
    }
}
