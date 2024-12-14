package hello.proxy.jdkdynamic;

import com.sun.jdi.InvocationException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Slf4j
public class ReflectionTest {
    @Test
    void reflection0(){
        Hello target = new Hello();

        // 공통 로직1 시작
        log.info("start");
        String result1 = target.callA(); // 호출하는 메서드가 다르다.
        log.info("result={}", result1);
        // 공통 로직1 종료

        // 공통 로직1 시작
        log.info("start");
        String result2 = target.callB(); // 호출하는 메서드가 다르다.
        log.info("result={}", result2);
        // 공통 로직1 종료
    }
    @Test
    void reflection1() throws Exception{
        // 클래스 정보
        Class classHello = Class.forName("hello.proxy.jdkdynamic.ReflectionTest$Hello");

        Hello target = new Hello();

        // callA 메서드 정보
        Method methodCallA = classHello.getMethod("callA");
        Object result1 = methodCallA.invoke(target);
        log.info("result1={}", result1);

        // callB 메서드 정보
        Method methodCallB = classHello.getMethod("callB");
        Object result2 = methodCallB.invoke(target);
        log.info("result2={}", result2);
    }

    @Test
    void reflection2() throws Exception{
        // 클래스 정보
        Class classHello = Class.forName("hello.proxy.jdkdynamic.ReflectionTest$Hello");

        Hello target = new Hello();

        // callA 메서드 정보
        Method methodCallA = classHello.getMethod("callA");
        dynamicCall(methodCallA, target);

        // callB 메서드 정보
        Method methodCallB = classHello.getMethod("callB");
        dynamicCall(methodCallB, target);
    }
    private void dynamicCall(Method method, Object target) throws Exception{
        log.info("start");
        Object result = method.invoke(target);
        log.info("result={}", result);
    }

    @Test
    void reflection3() throws Exception{
        Hello target = new Hello();
        Class classHello = target.getClass();
        // callA 정보
        Method callA = classHello.getMethod("callA");
        dynamicCall(callA, target);

        // callB 정보
        Method callB = classHello.getMethod("callB");
        dynamicCall(callB, target);
    }

    @Test
    void reflection4() throws Exception{
        Hello target = new Hello();
        List<String> list = List.of("callA", "callB");
        Class helloClass = target.getClass();
        List<Method> collect = Arrays.stream(helloClass.getDeclaredMethods())
                .filter(m -> list.contains(m)).collect(Collectors.toList());

        for (Method m: collect) {
            dynamicCall(m, target);
        }
    }
    @Test
    public void supplier(){
        Hello hello = new Hello();
        dynamicCall(hello::callA);
        dynamicCall(hello::callB);
    }
    private void dynamicCall(Supplier<String> supplier) {
        log.info("start");
        String result = supplier.get();
        log.info("result={}", result);
    }
    @Slf4j
    static class Hello{
        public String callA(){
            log.info("callA");
            return "A";
        }
        public String callB(){
            log.info("callB");
            return "B";
        }
    }
}
