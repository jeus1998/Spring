package spring.advanced.trace.threadlocal;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import spring.advanced.trace.threadlocal.code.ThreadLocalService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class ThreadLocalServiceTest {
    private final ThreadLocalService threadLocalService = new ThreadLocalService();
    @Test
    void field(){
        log.info("main start");
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Runnable userA = () -> threadLocalService.logic("userA");
        Runnable userB = () -> threadLocalService.logic("userB");

        executorService.submit(userA);
        sleep(1000);
        executorService.submit(userB);

        sleep(3000);
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
