package spring.servlet.web.springmvc.v2;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import spring.servlet.domain.member.Member;
import spring.servlet.domain.member.MemberRepository;

import java.util.List;

@Controller
@RequestMapping("/springmvc/v2/members")
public class SpringMemberController {
    MemberRepository memberRepository = MemberRepository.getInstance();
    @RequestMapping("/new-form")
    public ModelAndView memberForm(){
        return new ModelAndView("new-form");
    }
    @RequestMapping("/save")
    public ModelAndView memberSave(HttpServletRequest request, HttpServletResponse response){
        String username = request.getParameter("username");
        int age = Integer.parseInt(request.getParameter("age"));

        // 멤버 저장
        Member member = new Member(username, age);
        memberRepository.save(member);

        ModelAndView mv = new ModelAndView("save-result");
        // 모델 넣기
        mv.addObject("member", member);

        return mv;
    }

    @RequestMapping
    public ModelAndView memberList(){
        ModelAndView mv = new ModelAndView("members");

        List<Member> members = memberRepository.findAll();
        // model 넣기
        mv.addObject("members", members);

        return mv;
    }

}
