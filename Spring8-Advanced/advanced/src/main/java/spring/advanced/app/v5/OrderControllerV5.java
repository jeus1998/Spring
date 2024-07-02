package spring.advanced.app.v5;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import spring.advanced.trace.callback.TraceTemplate;

@RestController
@RequiredArgsConstructor
public class OrderControllerV5 {
    private final OrderServiceV5 orderService;
    private final TraceTemplate traceTemplate;
    @GetMapping("/v5/request")
    public String request(String itemId){
        return traceTemplate.execute("OrderController.request()", ()->{
           orderService.orderItem(itemId);
           return "ok";
        });
    }
}
