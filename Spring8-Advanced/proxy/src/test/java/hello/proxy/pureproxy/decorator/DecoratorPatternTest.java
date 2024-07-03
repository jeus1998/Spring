package hello.proxy.pureproxy.decorator;

import hello.proxy.pureproxy.decorator.code.*;
import hello.proxy.pureproxy.decorator.code.practice1.Decorator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class DecoratorPatternTest {
    @Test
    void noDecorator(){
        DecoratorPatternClient client = new DecoratorPatternClient(new RealComponent());
        client.execute();
    }
    @Test
    void decorator1(){
        MessageDecorator decorator = new MessageDecorator(new RealComponent());
        DecoratorPatternClient client = new DecoratorPatternClient(decorator);
        client.execute();
    }
    @Test
    void decorator2(){
        RealComponent realComponent = new RealComponent();
        MessageDecorator messageDecorator = new MessageDecorator(realComponent);
        TimeDecorator timeDecorator = new TimeDecorator(messageDecorator);
        DecoratorPatternClient client = new DecoratorPatternClient(timeDecorator);
        client.execute();
    }

    /**
     * Abstract Decorator GOF + 익명 내부 클래스 적용하기
     */
    @Test
    void decorator3(){
        RealComponent realComponent = new RealComponent();
        Decorator messageDecorator = new Decorator(realComponent) {
            @Override
            public String operation() {
                log.info("MessageDecorator 실행");
                String result = getComponent().operation();
                String decoResult =  "****" + result + "****";
                log.info("MessageDecorator 꾸미기 적용 전={}, 적용 후={}", result, decoResult);
                return decoResult;
            }
        };
        Decorator timeDecorator = new Decorator(messageDecorator) {
            @Override
            public String operation() {
                log.info("TimeDecorator 실행");
                long startTime = System.currentTimeMillis();
                String result = getComponent().operation();
                long endTime = System.currentTimeMillis();
                log.info("TimeDecorator 종료 resultTime={}ms", endTime - startTime);
                return result;
            }
        };
        DecoratorPatternClient client = new DecoratorPatternClient(timeDecorator);
        client.execute();
    }

    /**
     * 인터페이스 사용하기
     */
    @Test
    void decorator4(){
        RealComponent realComponent = new RealComponent();

        Component messageDecorator = () ->{
            log.info("MessageDecorator 실행");
            String result = realComponent.operation();
            String decoResult =  "****" + result + "****";
            log.info("MessageDecorator 꾸미기 적용 전={}, 적용 후={}", result, decoResult);
            return decoResult;
        };

        Component timeDecorator = () ->{
            log.info("TimeDecorator 실행");
            long startTime = System.currentTimeMillis();
            String result = messageDecorator.operation();
            long endTime = System.currentTimeMillis();
            log.info("TimeDecorator 종료 resultTime={}ms", endTime - startTime);
            return result;
        };

        DecoratorPatternClient client = new DecoratorPatternClient(timeDecorator);
        client.execute();
    }

}
