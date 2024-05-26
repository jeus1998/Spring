package hello.hello_spring.controller;
import hello.hello_spring.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class MemberController {
    private final MemberService memberService;

    /* DI(필드 주입)
    @Autowired private MemberService memberService;
     */

    /* DI(세터 주입)
    @Autowired
    public void setMemberService(MemberService, memberService){
        this.memberService = memberService;
    }
     */

    /**
     * DI(생성자 주입)
     * @param memberService
     */
    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }
}
