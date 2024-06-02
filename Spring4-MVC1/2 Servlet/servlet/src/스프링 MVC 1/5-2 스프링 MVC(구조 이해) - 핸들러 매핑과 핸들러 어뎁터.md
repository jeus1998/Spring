
# 스프링 MVC(구조 이해) - 핸들러 매핑과 핸들러 어뎁터

핸들러 매핑과 핸들러 어댑터가 어떤 것들이 어떻게 사용되는지 알아보자.
지금은 전혀 사용하지 않지만, 과거에 주로 사용했던 스프링이 제공하는 간단한 컨트롤러로 핸들러 매핑과 어댑터를 이
해해보자.

### Controller 인터페이스

- 과거 버전 스프링 컨트롤러
- org.springframework.web.servlet.mvc.Controller

```java
public interface Controller {
ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception;
}
```
- 스프링도 처음에는 이런 딱딱한 형식의 컨트롤러를 제공했다.
- 참고✅ Controller 인터페이스는 @Controller 애노테이션과는 전혀 다르다.

OldController
```java
@Component("/springmvc/old-controller")
public class OldController implements Controller {
    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        System.out.println("OldController.handleRequest");
        return null;
    }
}
```

- http://localhost:8080/springmvc/old-controller

이 컨트롤러는 어떻게 호출될 수 있을까?

![16.JPG](Image%2F16.JPG)

- HandlerMapping(핸들러 매핑)
  - 핸들러 매핑에서 이 컨트롤러를 찾을 수 있어야 한다.
  - 스프링 빈의 이름으로 핸들러를 찾을 수 있는 핸들러 매핑이 필요하다.
- HandlerAdapter(핸들러 어댑터)
  - 핸들러 매핑을 통해서 찾은 핸들러를 실행할 수 있는 핸들러 어댑터가 필요하다
  -  Controller 인터페이스를 실행할 수 있는 핸들러 어댑터를 찾고 실행해야 한다.

스프링은 이미 필요한 핸들러 매핑과 핸들러 어댑터를 대부분 구현해두었다. 개발자가 직접 핸들러 매핑과 핸들러 어댑
터를 만드는 일은 거의 없다.

### 스프링 부트가 자동 등록하는 핸들러 매핑과 핸들러 어댑터

HandlerMapping
```text
0 = RequestMappingHandlerMapping : 애노테이션 기반의 컨트롤러인 @RequestMapping에서 사용
1 = BeanNameUrlHandlerMapping : 스프링 빈의 이름으로 핸들러를 찾는다.
```

HandlerAdapter
```text
0 = RequestMappingHandlerAdapter : 애노테이션 기반의 컨트롤러인 @RequestMapping에서 사용
1 = HttpRequestHandlerAdapter : HttpRequestHandler 처리
2 = SimpleControllerHandlerAdapter : Controller 인터페이스(애노테이션X, 과거에 사용) 처리
```

핸들러 매핑도, 핸들러 어댑터도 모두 순서대로 찾고 만약 없으면 다음 순서로 넘어간다.

1. 핸들러 매핑으로 핸들러 조회
   - HandlerMapping 을 순서대로 실행해서, 핸들러를 찾는다.
   - 이 경우 빈 이름으로 핸들러를 찾아야 하기 때문에 이름 그대로 빈 이름으로 핸들러를 찾아주는
     BeanNameUrlHandlerMapping 가 실행에 성공하고 핸들러인 OldController 를 반환한다.
2. 핸들러 어댑터 조회
   - HandlerAdapter 의 supports() 를 순서대로 호출한다.
   - SimpleControllerHandlerAdapter 가 Controller 인터페이스를 지원하므로 대상이 된다.
3. 핸들러 어댑터 실행
   - 디스패처 서블릿이 조회한 SimpleControllerHandlerAdapter 를 실행하면서 핸들러 정보도 함께 넘겨준다.
   - SimpleControllerHandlerAdapter 는 핸들러인 OldController 를 내부에서 실행하고, 그 결과를 
     반환한다.

정리 - OldController 핸들러매핑, 어댑터
- HandlerMapping = BeanNameUrlHandlerMapping
- HandlerAdapter = SimpleControllerHandlerAdapter

### HttpRequestHandler

핸들러 매핑과, 어댑터를 더 잘 이해하기 위해 Controller 인터페이스가 아닌 다른 핸들러를 알아보자.
HttpRequestHandler 핸들러(컨트롤러)는 서블릿과 가장 유사한 형태의 핸들러이다.

```java
@Component("/springmvc/request-handler")
public class MyHttpRequestHandler implements HttpRequestHandler {
    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("MyHttpRequestHandler.handleRequest");
    }
}
```
- http://localhost:8080/springmvc/request-handler

1. 핸들러 매핑으로 핸들러 조회
   - HandlerMapping 을 순서대로 실행해서, 핸들러를 찾는다.
   - 이 경우 빈 이름으로 핸들러를 찾아야 하기 때문에 이름 그대로 빈 이름으로 핸들러를 찾아주는
     BeanNameUrlHandlerMapping 가 실행에 성공하고 핸들러인 MyHttpRequestHandler 를 반환한다.
2. 핸들러 어댑터 조회
   - HandlerAdapter 의 supports() 를 순서대로 호출한다.
   - HttpRequestHandlerAdapter 가 HttpRequestHandler 인터페이스를 지원하므로 대상이 된다.
3. 핸들러 어댑터 실행
   - 디스패처 서블릿이 조회한 HttpRequestHandlerAdapter 를 실행하면서 핸들러 정보도 함께 넘겨준다.
   - HttpRequestHandlerAdapter 는 핸들러인 MyHttpRequestHandler 를 내부에서 실행하고, 그 결과를 
     반환한다.

정리 - MyHttpRequestHandler 핸들러매핑, 어댑터
- HandlerMapping = BeanNameUrlHandlerMapping
- HandlerAdapter = HttpRequestHandlerAdapter

### @RequestMapping

가장 우선순위가 높은 핸들러 매핑과 핸들러 어댑터는 RequestMappingHandlerMapping, 
RequestMappingHandlerAdapter 이다.
@RequestMapping 의 앞글자를 따서 만든 이름인데, 이것이 바로 지금 스프링에서 주로 사용하는 애노테이션 기반의
컨트롤러를 지원하는 매핑과 어댑터이다.
실무에서는 99.9% 이 방식의 컨트롤러를 사용한다.
