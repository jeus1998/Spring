package spring.aop.internalcall;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * ObjectProvider, ApplicationContext 사용해서 지연(Lazy) 조회
 * 1. ApplicationContext 주입 -> 너무 거대하다.
 * 2. ObjectProvider<CallServiceV2> 사용하기 + getObject(); DependencyLookup
 */
@Slf4j
@Component
public class CallServiceV2 {
    // private final ApplicationContext applicationContext;
    private final ObjectProvider<CallServiceV2> callServiceProvider;
    public CallServiceV2(ObjectProvider<CallServiceV2> callServiceProvider) {
        this.callServiceProvider = callServiceProvider;
    }
    public void external(){
        log.info("call external");
        CallServiceV2 callServiceV2 = callServiceProvider.getObject();
        callServiceV2.internal();
    }
    public void internal(){
        log.info("call internal");
    }
}
