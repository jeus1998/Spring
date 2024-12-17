package hello.core.aop;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
@Component
@Slf4j
public class Example {
    public void call(){
        log.info("call");
    }
    @PostConstruct
    public void init(){
        log.info("init");
    }
    @PreDestroy
    public void destroy(){
        log.info("destroy");
    }
}
