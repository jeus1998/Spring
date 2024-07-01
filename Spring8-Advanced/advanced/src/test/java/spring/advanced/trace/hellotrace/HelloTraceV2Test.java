package spring.advanced.trace.hellotrace;

import org.junit.jupiter.api.Test;
import spring.advanced.trace.TraceStatus;

public class HelloTraceV2Test {
    @Test
    void begin_end(){
        HelloTraceV2 trace = new HelloTraceV2();
        TraceStatus status1 = trace.begin("hello1");
        TraceStatus status2 = trace.beginSync(status1.getTraceId(), "hello2");
        trace.end(status2);
        trace.end(status1);
    }

    @Test
    void begin_exception(){
        HelloTraceV2 trace = new HelloTraceV2();
        TraceStatus status1 = trace.begin("hello1");
        TraceStatus status2 = trace.beginSync(status1.getTraceId(), "hello2");
        trace.exception(status2, new IllegalStateException("예외발생!"));
        trace.exception(status1, new IllegalStateException("예외발생!"));

    }
}
