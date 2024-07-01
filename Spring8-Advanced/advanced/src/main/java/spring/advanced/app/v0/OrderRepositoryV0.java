package spring.advanced.app.v0;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class OrderRepositoryV0 {
    public void save(String itemId){
        log.info("itemId={}", itemId);
        // 저장 로직
        if(itemId.equals("ex")){
            throw new IllegalStateException("예외발생!");
        }
        sleep(1000);
    }
    private void sleep(int millis){
        try {
            Thread.sleep(millis);
        }
        catch (InterruptedException e){
            log.info("sleep fail={}", e);
        }
    }
}
