package spring.advanced.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import spring.advanced.trace.logtrace.FieldLogTrace;
import spring.advanced.trace.logtrace.LogTrace;
import spring.advanced.trace.logtrace.ThreadLocalLogTrace;

@Configuration
public class LogTraceConfig {
    @Bean
    public LogTrace logTrace(){
        // return new FieldLogTrace();
        return new ThreadLocalLogTrace();
    }
}
