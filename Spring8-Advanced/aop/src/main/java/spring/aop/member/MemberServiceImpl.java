package spring.aop.member;

import org.springframework.stereotype.Component;
import spring.aop.member.annotation.ClassAop;
import spring.aop.member.annotation.MethodAop;

@ClassAop
@Component
public class MemberServiceImpl implements MemberService{
    @Override
    @MethodAop(value = "test value") // @MethodAop("test value") 와 동일
    public String hello(String param) {
        return "ok";
    }

    public String internal(String param){
        return "ok";
    }
}
