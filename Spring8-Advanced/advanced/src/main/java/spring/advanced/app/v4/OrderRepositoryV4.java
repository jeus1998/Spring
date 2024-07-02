package spring.advanced.app.v4;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import spring.advanced.trace.logtrace.LogTrace;
import spring.advanced.trace.template.AbstractTemplate;

@Slf4j
@Repository
@RequiredArgsConstructor
public class OrderRepositoryV4 {
    private final LogTrace trace;
    public void save(String itemId){
        AbstractTemplate<Void> template = new AbstractTemplate<>(trace) {
            @Override
            protected Void call() {
                if(itemId.equals("ex")){
                    throw new IllegalStateException("예외발생!");
                }
                sleep(1000);
                return null;
            }
        };
        template.execute("OrderRepository.save()");
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
