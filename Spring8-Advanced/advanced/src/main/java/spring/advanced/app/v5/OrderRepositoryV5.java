package spring.advanced.app.v5;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import spring.advanced.trace.callback.TraceTemplate;
import spring.advanced.trace.logtrace.LogTrace;

@Slf4j
@Repository
@RequiredArgsConstructor
public class OrderRepositoryV5 {
    private final LogTrace trace;
    private final TraceTemplate template;
    public void save(String itemId){
        template.execute("OrderRepository.save()", ()->{
            if(itemId.equals("ex")){
                throw new IllegalStateException("예외발생");
            }
            sleep(1000);
            return null;
        });
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
