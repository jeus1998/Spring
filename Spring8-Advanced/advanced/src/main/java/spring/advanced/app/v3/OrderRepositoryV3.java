package spring.advanced.app.v3;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import spring.advanced.trace.TraceStatus;
import spring.advanced.trace.logtrace.LogTrace;

@Slf4j
@Repository
@RequiredArgsConstructor
public class OrderRepositoryV3 {
    private final LogTrace trace;
    public void save(String itemId){

        TraceStatus status = null;
        try {
            status = trace.begin("OrderRepository.save()");

            // 저장 로직
            if(itemId.equals("ex")){
                throw new IllegalStateException("예외발생!");
            }
            sleep(1000);

            trace.end(status);
        }
        catch (Exception e){
            trace.exception(status, e);
            throw e;
        }
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
