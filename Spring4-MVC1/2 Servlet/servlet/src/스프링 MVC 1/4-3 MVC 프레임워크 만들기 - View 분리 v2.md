
# View 분리 v2

### 뷰 코드 중복 

- 모든 컨트롤러에서 뷰로 이동하는 부분에 중복이 있고, 깔끔하지 않다.
```java
String viewPath = "/WEB-INF/views/new-form.jsp";
RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);
dispatcher.forward(request, response);
```

![11.JPG](Image%2F11.JPG)

### MyView
- spring/servlet/web/frontcontroller/MyView.java
```java
public class MyView {
    private String viewPath;

    public MyView(String viewPath) {
        this.viewPath = viewPath;
    }
    public void render(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);
        dispatcher.forward(request, response);
    }
}
```
### 인터페이스 ControllerV2
- spring/servlet/web/frontcontroller/v2/ControllerV2.java
```java
public interface ControllerV2 {
    MyView process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;
}
```

### MemberFormControllerV2 - 회원 등록 폼
- spring/servlet/web/frontcontroller/v2/controller/MemberFormControllerV2.java
```java
public class MemberFormControllerV2 implements ControllerV2 {
    @Override
    public MyView process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        return new MyView("/WEB-INF/views/new-form.jsp");
    }
}
```
- 이제 각 컨트롤러는 복잡한 dispatcher.forward() 를 직접 생성해서 호출하지 않아도 된다.
- 단순히 MyView 객체를 생성하고 거기에 뷰 이름만 넣고 반환하면 된다.
- ControllerV1 을 구현한 클래스와 ControllerV2 를 구현한 클래스를 비교해보면 이 부분의 중복이 확실하게 
  제거된 것을 확인할 수 있다.

### MemberSaveControllerV2 - 회원 저장
- spring/servlet/web/frontcontroller/v2/controller/MemberSaveControllerV2.java
```java
public class MemberSaveControllerV2 implements ControllerV2 {
    MemberRepository memberRepository = MemberRepository.getInstance();
    @Override
    public MyView process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            String username = request.getParameter("username");
            int age = Integer.parseInt(request.getParameter("age"));

            Member member = new Member(username, age);
            memberRepository.save(member);

            // Model 데이터를 보관
            request.setAttribute("member", member);

            return new MyView( "/WEB-INF/views/save-result.jsp");
    }
}
```

### MemberListControllerV2 - 회원 목록
- spring/servlet/web/frontcontroller/v2/controller/MemberListControllerV2.java
```java
public class MemberListControllerV2 implements ControllerV2 {
     MemberRepository memberRepository = MemberRepository.getInstance();
    @Override
    public MyView process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
          List<Member> members = memberRepository.findAll();
          request.setAttribute("members", members);
          return new MyView("/WEB-INF/views/members.jsp");
    }
}
```
### 프론트 컨트롤러 V2
- spring/servlet/web/frontcontroller/v2/FrontControllerServletV2.java
```java
@WebServlet(name = "frontControllerServletV2" , urlPatterns = "/front-controller/v2/*")
public class FrontControllerServletV2 extends HttpServlet {
    private Map<String, ControllerV2> controllerMap = new HashMap<>();
    public FrontControllerServletV2(){
        controllerMap.put("/front-controller/v2/members/new-form", new MemberFormControllerV2());
        controllerMap.put("/front-controller/v2/members/save" , new MemberSaveControllerV2());
        controllerMap.put("/front-controller/v2/members", new MemberListControllerV2());
    }
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        ControllerV2 controller = controllerMap.get(requestURI);
        if(controller == null){
            response.setStatus(HttpServletResponse.SC_FOUND);
            return;
        }

        MyView view = controller.process(request, response);

        // JSP forward
        view.render(request, response);
    }
}
```

- ControllerV2의 반환 타입이 MyView 이므로 프론트 컨트롤러는 컨트롤러의 호출 결과로 MyView 를 반환 받는다. 
- 그리고 view.render() 를 호출하면 forward 로직을 수행해서 JSP가 실행된다.
- 프론트 컨트롤러의 도입으로 MyView 객체의 render() 를 호출하는 부분을 모두 일관되게 처리할 수 있다
- 각각의 컨트롤러는 MyView 객체를 생성만 해서 반환하면 된다.
