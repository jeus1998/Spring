package spring.advanced.app.v2;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import spring.advanced.trace.TraceStatus;
import spring.advanced.trace.hellotrace.HelloTraceV2;

@RestController
@RequiredArgsConstructor
public class OrderControllerV2 {
    private final OrderServiceV2 orderService;
    private final HelloTraceV2 trace;
    @GetMapping("/v2/request")
    public String request(String itemId){

        TraceStatus status = null;

        try {
            status = trace.begin("OrderController.request()");
            orderService.orderItem(itemId, status.getTraceId());
            trace.end(status);
            return "ok";
        }
        catch (Exception e){
            trace.exception(status, e);
            throw e; // 애플리케이션 흐름을 바꾸지 말자
        }
    }
}
