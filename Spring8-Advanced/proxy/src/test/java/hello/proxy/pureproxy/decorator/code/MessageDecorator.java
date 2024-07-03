package hello.proxy.pureproxy.decorator.code;

import lombok.extern.slf4j.Slf4j;

/**
 * 부가기능 - 응답 꾸며주기
 */
@Slf4j
public class MessageDecorator implements Component {
    private Component target;
    public MessageDecorator(Component target) {
        this.target = target;
    }
    @Override
    public String operation() {
        log.info("MessageDecorator 실행");
        String result = target.operation();
        String decoResult =  "****" + result + "****";
        log.info("MessageDecorator 꾸미기 적용 전={}, 적용 후={}", result, decoResult);
        return decoResult;
    }
}
