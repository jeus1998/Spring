
# Model 추가 v3

### 리팩토링 예정 

- 서블릿 종속성 제거
  - 컨트롤러 입장에서 HttpServletRequest, HttpServletResponse이 꼭 필요할까?
  - 요청 파라미터 정보는 자바의 Map으로 대신 넘기도록 하면 지금 구조에서는 컨트롤러가 서블릿 기술을 몰라도 동작할
    수 있다.
  - request 객체를 Model로 사용하는 대신에 별도의 Model 객체를 만들어서 반환하면 된다.
  - 우리가 구현하는 컨트롤러가 서블릿 기술을 전혀 사용하지 않도록 변경해보자.
  - 이렇게 하면 구현 코드도 매우 단순해지고, 테스트 코드 작성이 쉽다.

- 뷰 이름 중복성 제거 
  - 컨트롤러에서 지정하는 뷰 이름에 중복이 있는 것을 확인할 수 있다.
  - 컨트롤러는 뷰의 논리 이름을 반환하고, 실제 물리 위치의 이름은 프론트 컨트롤러에서 처리하도록 단순화 하자.
  - 이렇게 해두면 향후 뷰의 폴더 위치가 함께 이동해도 프론트 컨트롤러만 고치면 된다. 
  - /WEB-INF/views/new-form.jsp ➡️ new-form
  - /WEB-INF/views/save-result.jsp ➡️ save-result
  - /WEB-INF/views/members.jsp ➡️ members

### V3 구조 

![12.JPG](Image%2F12.JPG)

### ModelView
- spring/servlet/web/frontcontroller/ModelView.java
```java
public class ModelView {
    private String viewName;
    private Map<String, Object> model = new HashMap<>();
    public ModelView(String viewName) {
        this.viewName = viewName;
    }
    public void setViewName(String viewName) {
        this.viewName = viewName;
    }
    public String getViewName() {
        return viewName;
    }
    public Map<String, Object> getModel() {
        return model;
    }
}
```

- 뷰의 이름과 뷰를 렌더링할 때 필요한 model 객체를 가지고 있다. 
- model은 단순히 map으로 되어 있으므로 컨트롤러에서 뷰에 필요한 데이터를 key, value로 넣어주면 된다.

### ControllerV3

- spring/servlet/web/frontcontroller/v3/ControllerV3.java

```java
public interface ControllerV3 {
    ModelView process(Map<String, String> paramMap);
}
```
- 이 컨트롤러는 서블릿 기술을 전혀 사용하지 않는다.
- 따라서 구현이 매우 단순해지고, 테스트 코드 작성시 테스트 하기쉽다.
- HttpServletRequest가 제공하는 파라미터는 프론트 컨트롤러가 paramMap에 담아서 호출해주면 된다.
- 응답 결과로 뷰 이름과 뷰에 전달할 Model 데이터를 포함하는 ModelView 객체를 반환하면 된다.


### MemberFormControllerV3 - 회원 등록 폼
- spring/servlet/web/frontcontroller/v3/controller/MemberFormControllerV3.java
```java
public class MemberFormControllerV3 implements ControllerV3 {
    @Override
    public ModelView process(Map<String, String> paramMap) {
         return new ModelView("new-form");
    }
}
```
- ModelView 를 생성할 때 new-form 이라는 view의 논리적인 이름을 지정한다.
- 실제 물리적인 이름은 프론트 컨트롤러에서 처리한다.

### MemberSaveControllerV3 - 회원 저장

- spring/servlet/web/frontcontroller/v3/controller/MemberSaveControllerV3.java

```java
public class MemberSaveControllerV3 implements ControllerV3 {
    MemberRepository memberRepository = MemberRepository.getInstance();
    @Override
    public ModelView process(Map<String, String> paramMap) {
        String username = paramMap.get("username");
        int age = Integer.parseInt(paramMap.get("age"));

        Member member = new Member(username, age);
        memberRepository.save(member);
        ModelView modelView = new ModelView("save-result");
        modelView.getModel().put("member", member);
        return modelView;
    }
}
```
- paramMap.get("username");
  - 파라미터 정보는 map에 담겨있다. map에서 필요한 요청 파라미터를 조회하면 된다.
- mv.getModel().put("member", member);
  - 모델은 단순한 map이므로 모델에 뷰에서 필요한 member 객체를 담고 반환한다.

### MemberListControllerV3 - 회원 목록

- spring/servlet/web/frontcontroller/v3/controller/MemberListControllerV3.java
```java
public class MemberListControllerV3 implements ControllerV3 {
    MemberRepository memberRepository = MemberRepository.getInstance();
    @Override
    public ModelView process(Map<String, String> paramMap) {
        List<Member> members = memberRepository.findAll();
        ModelView mv = new ModelView("members");
        mv.getModel().put("members", members);
        return mv;
    }
}
```
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
    public void render(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
        modelToRequestAttribute(model, request);

        RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);
          dispatcher.forward(request, response);
    }

    private static void modelToRequestAttribute(Map<String, Object> model, HttpServletRequest request) {
        model.entrySet().iterator()
                .forEachRemaining(o -> request.setAttribute(o.getKey(), o.getValue()));
    }
}
```

### FrontControllerServletV3
- spring/servlet/web/frontcontroller/v3/FrontControllerServletV3.java

```java
@WebServlet(name = "frontControllerServletV3" , urlPatterns = "/front-controller/v3/*")
public class FrontControllerServletV3 extends HttpServlet {
    private Map<String, ControllerV3> controllerMap = new HashMap<>();
    public FrontControllerServletV3(){
        controllerMap.put("/front-controller/v3/members/new-form", new MemberFormControllerV3());
        controllerMap.put("/front-controller/v3/members/save" , new MemberSaveControllerV3());
        controllerMap.put("/front-controller/v3/members", new MemberListControllerV3());
    }
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        ControllerV3 controller = controllerMap.get(requestURI);
        if(controller == null){
            response.setStatus(HttpServletResponse.SC_FOUND);
            return;
        }
        HashMap<String, String> paramMap = creteParamMap(request);

        ModelView mv = controller.process(paramMap);

        MyView view = viewResolver(mv);

        // JSP forward
        view.render(mv.getModel(), request, response);
    }

    private static MyView viewResolver(ModelView mv) {
        return new MyView("/WEB-INF/views/" + mv.getViewName() + ".jsp");
    }

    private static HashMap<String, String> creteParamMap(HttpServletRequest request) {
        HashMap<String, String> paramMap = new HashMap<>();
        request.getParameterNames().asIterator().
                forEachRemaining(paramName -> paramMap.put(paramName, request.getParameter(paramName)));
        return paramMap;
    }
}
```


