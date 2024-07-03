package hello.proxy.pureproxy.concreteproxy;

import hello.proxy.pureproxy.concreteproxy.code.ConcreteClient;
import hello.proxy.pureproxy.concreteproxy.code.ConcreteLogic;
import hello.proxy.pureproxy.concreteproxy.code.TimeProxy;
import org.junit.jupiter.api.Test;

public class ConcreteProxyTest {
    @Test
    void noProxy(){
        ConcreteClient client = new ConcreteClient(new ConcreteLogic());
        client.execute();
    }
    @Test
    void addProxy(){
        ConcreteLogic concreteLogic = new ConcreteLogic();
        ConcreteLogic timeProxy = new TimeProxy(concreteLogic);
        ConcreteClient client = new ConcreteClient(timeProxy);
        client.execute();
    }
}
