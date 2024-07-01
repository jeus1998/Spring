package spring.advanced.trace.logtrace;

import org.junit.jupiter.api.Test;
import spring.advanced.trace.TraceStatus;

public class FieldLogTraceTest {
    FieldLogTrace trace = new FieldLogTrace();

    @Test
    void begin_end_level2(){
        TraceStatus status1 = trace.begin("hello1");
        TraceStatus status2 = trace.begin("hello2");
        trace.end(status2);
        trace.end(status1);
    }
    @Test
    void begin_exception_level2(){
        TraceStatus status1 = trace.begin("hello1");
        TraceStatus status2 = trace.begin("hello2");
        trace.exception(status2, new IllegalStateException("예외발생!"));
        trace.exception(status1, new IllegalStateException("예외발생!"));
    }
}
