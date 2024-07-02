package spring.advanced.trace.callback;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import spring.advanced.trace.TraceStatus;
import spring.advanced.trace.logtrace.LogTrace;

@Component
@RequiredArgsConstructor
public class TraceTemplate {
    private final LogTrace trace;
    public <T> T execute(String message, TraceCallBack<T> callBack){
        TraceStatus status = null;
        try {
            status = trace.begin(message);
            T result =  callBack.call();
            trace.end(status);
            return result;
        }
        catch (Exception e){
            trace.exception(status, e);
            throw e;
        }
    }
}
