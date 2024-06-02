package spring.servlet.web.springmvc.v3;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import spring.servlet.domain.member.Member;
import spring.servlet.domain.member.MemberRepository;

import java.util.List;

@Controller
@RequestMapping("/springmvc/v3/members")
public class SpringMemberControllerV3 {
    MemberRepository memberRepository = MemberRepository.getInstance();
    @RequestMapping(value = "/new-form", method = RequestMethod.GET)
    public String memberForm(){
        return "new-form";
    }
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String memberSave(@RequestParam("username") String username,
                                   @RequestParam("age") int age, Model model){
        // 멤버 저장
        Member member = new Member(username, age);
        memberRepository.save(member);

        model.addAttribute("member", member);

        return "save-result";
    }
    @GetMapping
    public String memberList(Model model){
        List<Member> members = memberRepository.findAll();

        // model 넣기
        model.addAttribute("members", members);

        return "members";
    }
}

