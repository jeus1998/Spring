package hello.login.web;

import hello.login.domain.member.Member;
import hello.login.domain.member.MemberRepository;
import hello.login.web.argumentresolver.Login;
import hello.login.web.session.SessionManger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {
    private final MemberRepository memberRepository;
    private final SessionManger sessionManger;
    // @GetMapping("/")
    public String homeLogin(@CookieValue(name = "memberId", required = false) Long memberId, Model model) {

        if(memberId == null){
            return "home";
        }

        Member loginMember = memberRepository.findById(memberId);

        if(loginMember == null){
            return "home";
        }

        model.addAttribute("member", loginMember);
        return "loginHome";
    }

    //@GetMapping("/")
    public String homeLoginV2(HttpServletRequest request, Model model) {

         Member loginMember = (Member)sessionManger.getSession(request);

         if(loginMember == null){
            return "home";
         }

         model.addAttribute("member", loginMember);
         return "loginHome";
    }

    // @GetMapping("/")
    public String homeLoginV3(HttpServletRequest request, Model model) {

        HttpSession session = request.getSession(false);
        if(session == null){
             return "home";
        }

        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        if(loginMember == null){
             return "home";
        }

        model.addAttribute("member", loginMember);
          return "loginHome";
    }

    /**
     * @SessionAttribute 적용해서 세션 편리하게 사용하기
     * 세션을 생성하지는 않는다.
     */
    // @GetMapping("/")
    public String homeLoginV4(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember, Model model) {
        if(loginMember == null){
             return "home";
        }
        model.addAttribute("member", loginMember);
          return "loginHome";
    }

    /**
     * ArgumentResolver 활용
     */
    @GetMapping("/")
    public String homeLoginArgumentResolver(@Login Member loginMember, Model model) {

        if(loginMember == null){
             return "home";
        }
        model.addAttribute("member", loginMember);
          return "loginHome";
    }

}