package spring.advanced.app.v5;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spring.advanced.trace.callback.TraceTemplate;
@Service
@RequiredArgsConstructor
public class OrderServiceV5 {
    private final OrderRepositoryV5 orderRepository;
    private final TraceTemplate template;
    public void orderItem(String itemId){
        template.execute("OrderService.orderItem()", ()->{
            orderRepository.save(itemId);
            return null;
        });
    }
}
