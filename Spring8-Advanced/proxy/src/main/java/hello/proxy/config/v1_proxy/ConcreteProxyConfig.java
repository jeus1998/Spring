package hello.proxy.config.v1_proxy;

import hello.proxy.app.v2.OrderControllerV2;
import hello.proxy.app.v2.OrderRepositoryV2;
import hello.proxy.app.v2.OrderServiceV2;
import hello.proxy.config.v1_proxy.concrete_proxy.OrderControllerConcreteProxy;
import hello.proxy.config.v1_proxy.concrete_proxy.OrderRepositoryConcreteProxy;
import hello.proxy.config.v1_proxy.concrete_proxy.OrderServiceConcreteProxy;
import hello.proxy.trace.logtrace.LogTrace;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConcreteProxyConfig {
    @Bean
    public OrderControllerV2 orderController(LogTrace trace){
        OrderControllerV2 orderControllerImpl = new OrderControllerV2(orderService(trace));
        return new OrderControllerConcreteProxy(orderControllerImpl, trace);
    }
    @Bean
    public OrderServiceV2 orderService(LogTrace trace){
        OrderServiceV2 orderServiceImpl = new OrderServiceV2(orderRepository(trace));
        return new OrderServiceConcreteProxy(orderServiceImpl, trace);
    }
    @Bean
    public OrderRepositoryV2 orderRepository(LogTrace trace){
        OrderRepositoryV2 orderRepositoryImpl = new OrderRepositoryV2();
        return new OrderRepositoryConcreteProxy(trace, orderRepositoryImpl);
    }
}
