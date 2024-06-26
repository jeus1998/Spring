
# 스프링 MVC(구조 이해) - 스프링 MVC 전체 구조

### 내가 만든 MVC VS 스프릥 MVC 비교

직접 만든 MVC 프레임워크 구조 
![15.JPG](Image%2F15.JPG)

SpringMVC 구조
![16.JPG](Image%2F16.JPG)

- 직접 만든 프레임워크 ➡️ 스프링 MVC 비교
  - FrontController ➡️ DispatcherServlet
  - handlerMappingMap ➡️ HandlerMapping
  - MyHandlerAdapter ➡️ HandlerAdapter
  - ModelView ➡️ ModelAndView
  - viewResolver ➡️ ViewResolver
  - MyView ➡️ View 

## DispatcherServlet 구조 살펴보기

- org.springframework.web.servlet.DispatcherServlet
- 스프링 MVC도 프론트 컨트롤러 패턴으로 구현되어 있다
- 스프링 MVC의 프론트 컨트롤러가 바로 디스패처 서블릿(DispatcherServlet)이다.
- 이 디스패처 서블릿이 바로 스프링 MVC의 핵심이다.

### DispatcherServlet 서블릿 등록

![17.JPG](Image%2F17.JPG)

- DispatcherServlet 도 부모 클래스에서 HttpServlet 을 상속 받아서 사용하고, 서블릿으로 동작한다.
- DispatcherServlet - FrameworkServlet - HttpServletBean - HttpServlet
- 스프링 부트는 DispatcherServlet 을 서블릿으로 자동으로 등록하면서 모든 경로(urlPatterns="/")에
  대해서 매핑한다.
- 참고: 더 자세한 경로가 우선순위가 높다. 그래서 기존에 등록한 서블릿도 함께 동작한다.

### DispatcherServlet 요청 흐름

- 서블릿이 호출되면 HttpServlet 이 제공하는 service() 가 호출된다.
- 스프링 MVC는 DispatcherServlet 의 부모인 FrameworkServlet 에서 service() 를 오버라이딩 해
  두었다
- FrameworkServlet.service() 를 시작으로 여러 메서드가 호출되면서 
  DispatcherServlet.doDispatch() 가 호출된다.

DispatcherServlet 의 핵심인 doDispatch() 코드를 분석
```java
protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpServletRequest processedRequest = request;
        HandlerExecutionChain mappedHandler = null;
        ModelAndView mv = null;
        
        // 1. 핸들러 조회
        mappedHandler = getHandler(processedRequest);
        if (mappedHandler == null) {
            noHandlerFound(processedRequest, response);
            return;
        }
        
        // 2. 핸들러 어댑터 조회 - 핸들러를 처리할 수 있는 어댑터
        HandlerAdapter ha = getHandlerAdapter(mappedHandler.getHandler());
        
        // 3. 핸들러 어댑터 실행 -> 4. 핸들러 어댑터를 통해 핸들러 실행 -> 5. ModelAndView 반환
        mv = ha.handle(processedRequest, response, mappedHandler.getHandler());
        
        processDispatchResult(processedRequest, response, mappedHandler, mv, dispatchException);
}

// 뷰 렌더링 호출
private void processDispatchResult(HttpServletRequest request, HttpServletResponse response, 
        HandlerExecutionChain mappedHandler, ModelAndView mv, Exception exception) throws Exception {
        
        render(mv, request, response);
}


protected void render(ModelAndView mv, HttpServletRequest request, HttpServletResponse response) throws Exception {
        View view;
        String viewName = mv.getViewName();
        
        // 6. 뷰 리졸버를 통해서 뷰 찾기, 7. View 반환
        view = resolveViewName(viewName, mv.getModelInternal(), locale, request);
        
        // 8. 뷰 렌더링
        view.render(mv.getModelInternal(), request, response);
}
```

![16.JPG](Image%2F16.JPG)

동작 순서
1. 핸들러 조회: 핸들러 매핑을 통해 요청 URL에 매핑된 핸들러(컨트롤러)를 조회한다.
2. 핸들러 어댑터 조회: 핸들러를 실행할 수 있는 핸들러 어댑터를 조회한다 
3. 핸들러 어댑터 실행: 핸들러 어댑터를 실행한다. 
4. 핸들러 실행: 핸들러 어댑터가 실제 핸들러를 실행한다.
5. ModelAndView 반환: 핸들러 어댑터는 핸들러가 반환하는 정보를 ModelAndView로 변환해서 반환한다.
6. viewResolver 호출: 뷰 리졸버를 찾고 실행한다.
   - JSP의 경우: InternalResourceViewResolver 가 자동 등록되고, 사용된다.
7. View 반환: 뷰 리졸버는 뷰의 논리 이름을 물리 이름으로 바꾸고, 렌더링 역할을 담당하는 뷰 객체를 반환한다.
   - JSP의 경우 InternalResourceView(JstlView) 를 반환하는데, 내부에 forward() 로직이 있다.
8. 뷰 렌더링: 뷰를 통해서 뷰를 렌더링 한다.

### 스프링 MVC 주요 인터페이스 목록

- 핸들러 매핑: org.springframework.web.servlet.HandlerMapping
- 핸들러 어댑터: org.springframework.web.servlet.HandlerAdapter
- 뷰 리졸버: org.springframework.web.servlet.ViewResolver
- 뷰: org.springframework.web.servlet.View

### 💯 정리

스프링 MVC는 코드 분량도 매우 많고, 복잡해서 내부 구조를 다 파악하는 것은 쉽지 않다. 
사실 해당 기능을 직접 확장하거나 나만의 컨트롤러를 만드는 일은 없으므로 걱정하지 않아도 된다.
왜냐하면 스프링 MVC는 전세계 수 많은 개발자들의 요구사항에 맞추어 기능을 계속 확장해왔고, 그래서 
웹 애플리케이션을 만들 때 필요로 하는 대부분의 기능이 이미 다 구현되어 있다.
그래도 이렇게 핵심 동작방식을 알아두어야 향후 문제가 발생했을 때 어떤 부분에서 문제가 발생했는지 쉽게 파악하고, 
문제를 해결할 수 있다. 
그리고 확장 포인트가 필요할 때, 어떤 부분을 확장해야 할지 감을 잡을 수 있다.