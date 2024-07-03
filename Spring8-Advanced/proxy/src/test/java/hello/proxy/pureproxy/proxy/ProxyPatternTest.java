package hello.proxy.pureproxy.proxy;

import hello.proxy.pureproxy.proxy.code.ProxyPatternClient;
import hello.proxy.pureproxy.proxy.code.RealSubject;
import org.junit.jupiter.api.Test;

public class ProxyPatternTest {
    @Test
    void noProxyTest(){
        ProxyPatternClient client = new ProxyPatternClient(new RealSubject());
        client.execute();
        client.execute();
        client.execute();
    }
}
