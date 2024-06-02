
# 스프링 MVC(구조 이해) - 스프링 MVC 시작, 컨트롤러 통합 

스프링이 제공하는 컨트롤러는 애노테이션 기반으로 동작해서, 매우 유연하고 실용적이다. 과거에는 자바 언어에 애노
테이션이 없기도 했고, 스프링도 처음부터 이런 유연한 컨트롤러를 제공한 것은 아니다.

### @RequestMapping

스프링은 애노테이션을 활용한 매우 유연하고, 실용적인 컨트롤러를 만들었는데 이것이 바로 @RequestMapping 애
노테이션을 사용하는 컨트롤러이다.

- RequestMappingHandlerMapping(핸들러 매핑)
- RequestMappingHandlerAdapter(핸들러 어뎁터)

앞서 보았듯이 가장 우선순위가 높은 핸들러 매핑과 핸들러 어댑터는 RequestMappingHandlerMapping , 
RequestMappingHandlerAdapter 이다.
@RequestMapping 의 앞글자를 따서 만든 이름인데, 이것이 바로 지금 스프링에서 주로 사용하는 애노테이션 기반의
컨트롤러를 지원하는 핸들러 매핑과 어댑터이다.
실무에서는 99.9% 이 방식의 컨트롤러를 사용한다.

- @Controller
  - 스프링이 자동으로 스프링 빈으로 등록한다. (내부에 @Component)
  - 스프링 MVC에서 애노테이션 기반 컨트롤러로 인식한다.
- @RequestMapping 
  - 요청 정보를 매핑한다. 해당 URL이 호출되면 이 메서드가 호출된다
  - 애노테이션을 기반으로 동작하기 때문에, 메서드의 이름은 임의로 지으면 된다.

RequestMappingHandlerMapping은 스프링 빈 중에서 @RequestMapping 또는 @Controller 가 클래스
레벨에 붙어 있는 경우에 매핑 정보로 인식한다. 따라서 다음 코드도 동일하게 동작한다.

```java
@Component //컴포넌트 스캔을 통해 스프링 빈으로 등록
@RequestMapping
public class SpringMemberFormControllerV1 {
        @RequestMapping("/springmvc/v1/members/new-form")
        public ModelAndView process() {
            return new ModelAndView("new-form");
        }
}
```

```java
@Controller
public class SpringMemberFormControllerV1 {
        @RequestMapping("/springmvc/v1/members/new-form")
        public ModelAndView process() {
            return new ModelAndView("new-form");
        }
}
```

❗️ 주의! - 스프링 3.0 이상
스프링 부트 3.0(스프링 프레임워크 6.0)부터는 클래스 레벨에 @RequestMapping 이 있어도 스프링 컨트롤러로 인
식하지 않는다. 오직 @Controller 가 있어야 스프링 컨트롤러로 인식한다.
참고로 @RestController 는 해당 애노테이션 내부에 @Controller 를 포함하고 있으므로 인식 된다.
따라서 클래스 레벨에 @Controller가 없는 코드는 스프링 컨트롤러로 인식되지 않는다.
(RequestMappingHandlerMapping 에서 @RequestMapping 는 이제 인식하지 않고, @Controller 만 인식한다.)

### SpringMemberController v1, v2 

```java
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
```

- mv.addObject("member", member)
  - 스프링이 제공하는 ModelAndView 를 통해 Model 데이터를 추가할 때는 addObject() 를 사용하면된다.
  - 이 데이터는 이후 뷰를 렌더링 할 때 사용된다.

