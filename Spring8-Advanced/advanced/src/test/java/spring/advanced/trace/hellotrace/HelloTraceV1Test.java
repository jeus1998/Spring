package spring.advanced.trace.hellotrace;

import org.junit.jupiter.api.Test;
import spring.advanced.trace.TraceStatus;

public class HelloTraceV1Test {
    @Test
    void begin_end(){
        HelloTraceV1 trace = new HelloTraceV1();
        TraceStatus status = trace.begin("hello");
        trace.end(status);
    }

    @Test
    void begin_exception(){
        HelloTraceV1 trace = new HelloTraceV1();
        TraceStatus status = trace.begin("hello");
        trace.exception(status, new IllegalStateException());
    }
}
