package hello.proxy.app.v2;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OrderRepositoryV2 {
    public void save(String itemId) {
        // 저장로직
        if(itemId.equals("ex")){
            throw new IllegalStateException("예외 발생!");
        }
        sleep(1000);
    }
    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        }
        catch (InterruptedException e){
            log.info("sleep fail{}", e);
        }
    }
}
