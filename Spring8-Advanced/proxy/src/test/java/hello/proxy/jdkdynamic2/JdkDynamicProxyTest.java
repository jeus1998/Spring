package hello.proxy.jdkdynamic2;

import hello.proxy.jdkdynamic2.code.AImpl;
import hello.proxy.jdkdynamic2.code.AInterface;
import hello.proxy.jdkdynamic2.code.BImpl;
import hello.proxy.jdkdynamic2.code.BInterface;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Proxy;

@Slf4j
public class JdkDynamicProxyTest {
    @Test
    void dynamicA(){
        AInterface target = new AImpl();
        TimeInvocationHandler handler = new TimeInvocationHandler(target);
        AInterface proxy = (AInterface)Proxy.newProxyInstance(
                AInterface.class.getClassLoader(), new Class[]{AInterface.class}, handler);

        String result = proxy.call();
        log.info("result={}", result);
        log.info("targetClass={}", target.getClass());
        log.info("proxyClass={}", proxy.getClass());
    }
    @Test
    void dynamicB(){
        BInterface target = new BImpl();
        TimeInvocationHandler handler = new TimeInvocationHandler(target);
        BInterface proxy = (BInterface)Proxy.newProxyInstance(
                BInterface.class.getClassLoader(), new Class[]{BInterface.class}, handler);

        String result = proxy.call();
        log.info("result={}", result);
        log.info("targetClass={}", target.getClass());
        log.info("proxyClass={}", proxy.getClass());
    }
}
