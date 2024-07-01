package spring.advanced.app.v2;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import spring.advanced.trace.TraceId;
import spring.advanced.trace.TraceStatus;
import spring.advanced.trace.hellotrace.HelloTraceV2;

@Slf4j
@Repository
@RequiredArgsConstructor
public class OrderRepositoryV2 {
    private final HelloTraceV2 trace;
    public void save(String itemId, TraceId traceId){

        TraceStatus status = null;
        try {
            status = trace.beginSync(traceId,"OrderRepository.save()");

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
