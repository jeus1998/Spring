
# 프론트 컨트롤러 도입 v1

프론트 컨트롤러를 단계적으로 도입해보자.
이번 목표는 기존 코드를 최대한 유지하면서, 프론트 컨트롤러를 도입하는 것이다.
먼저 구조를 맞추어두고 점진적으로 리펙터링 해보자.

![10.JPG](Image%2F10.JPG)

### 인터페이스 ControllerV1

- spring/servlet/web/frontcontroller/v1/ControllerV1.java

```java
public interface ControllerV1 {
    void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;
}
```

- 서블릿과 비슷한 모양의 컨트롤러 인터페이스를 도입한다.
- 각 컨트롤러들은 이 인터페이스를 구현하면 된다.
- 프론트 컨트롤러는 이 인터페이스를 호출해서 구현과 관계없이 로직의 일관성을 가져갈 수 있다.

### MemberFormControllerV1 - 회원 등록 컨트롤러

- spring/servlet/web/frontcontroller/v1/controller/MemberFormControllerV1.java

```java
public class MemberFormControllerV1 implements ControllerV1 {
    @Override
    public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
          String viewPath = "/WEB-INF/views/new-form.jsp";
          RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);
          dispatcher.forward(request, response); // 서블릿 -> JSP
    }
}
```

### MemberSaveControllerV1 - 회원 저장 컨트롤러

-spring/servlet/web/frontcontroller/v1/controller/MemberSaveControllerV1.java

```java
public class MemberSaveControllerV1 implements ControllerV1 {
    MemberRepository memberRepository = MemberRepository.getInstance();
    @Override
    public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            String username = request.getParameter("username");
            int age = Integer.parseInt(request.getParameter("age"));

            Member member = new Member(username, age);
            memberRepository.save(member);

            // Model 데이터를 보관
            request.setAttribute("member", member);

            String viewPath = "/WEB-INF/views/save-result.jsp";
            RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);
            dispatcher.forward(request, response);
    }
}
```

### MemberListControllerV1 - 회원 목록 컨트롤러

- spring/servlet/web/frontcontroller/v1/controller/MemberListControllerV1.java

```java
public class MemberListControllerV1 implements ControllerV1 {
    MemberRepository memberRepository = MemberRepository.getInstance();
    @Override
    public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
          List<Member> members = memberRepository.findAll();

          request.setAttribute("members", members);

          String viewPath = "/WEB-INF/views/members.jsp";

          RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);
          dispatcher.forward(request, response);
    }
}
```

### FrontControllerServletV1 - 프론트 컨트롤러

- spring/servlet/web/frontcontroller/v1/FrontControllerServletV1.java

```java
@WebServlet(name = "frontControllerServletV1" , urlPatterns = "/front-controller/v1/*")
public class FrontControllerServletV1 extends HttpServlet {
    private Map<String, ControllerV1> controllerMap = new HashMap<>();
    public FrontControllerServletV1(){
        controllerMap.put("/front-controller/v1/members/new-form", new MemberFormControllerV1());
        controllerMap.put("/front-controller/v1/members/save" , new MemberSaveControllerV1());
        controllerMap.put("/front-controller/v1/members", new MemberListControllerV1());
    }
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        ControllerV1 controller = controllerMap.get(requestURI);
        if(controller == null){
            response.setStatus(HttpServletResponse.SC_FOUND);
            return;
        }

        controller.process(request, response);
    }
}
```

### 프론트 컨트롤러 분석

- urlPatterns = "/front-controller/v1/*" 
  - /front-controller/v1 를 포함한 하위 모든 요청은 이 서블릿에서 받아들인다.
- controllerMap
  - key: 매핑 URL
  - value: 호출될 컨트롤러
- service()
  - 먼저 requestURI 를 조회해서 실제 호출할 컨트롤러를 controllerMap에서 찾는다.
  - 만약 없다면 404(SC_NOT_FOUND) 상태 코드를 반환한다.
  - 컨트롤러를 찾고 controller.process(request, response); 을 호출해서 해당 컨트롤러를 실행한다.
- JSP
  - JSP는 이전 MVC에서 사용했던 것을 그대로 사용한다.



