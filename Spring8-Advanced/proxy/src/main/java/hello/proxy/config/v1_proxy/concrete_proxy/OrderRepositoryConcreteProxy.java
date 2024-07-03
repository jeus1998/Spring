package hello.proxy.config.v1_proxy.concrete_proxy;

import hello.proxy.app.v2.OrderRepositoryV2;
import hello.proxy.trace.TraceStatus;
import hello.proxy.trace.logtrace.LogTrace;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OrderRepositoryConcreteProxy extends OrderRepositoryV2 {
    private final LogTrace trace;
    private final OrderRepositoryV2 target;
    public OrderRepositoryConcreteProxy(LogTrace trace, OrderRepositoryV2 target) {
        this.trace = trace;
        this.target = target;
    }
    @Override
    public void save(String itemId) {
        TraceStatus status = null;
        try {
            status = trace.begin("OrderRepository.save()");
            target.save(itemId);
            trace.end(status);
        }
        catch (Exception e){
            trace.exception(status, e);
            throw e;
        }
    }
}
