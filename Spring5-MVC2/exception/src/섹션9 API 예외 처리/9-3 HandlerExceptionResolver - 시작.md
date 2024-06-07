
# HandlerExceptionResolver - 시작

### 목표

- 예외가 발생해서 서블릿을 넘어 WAS까지 예외가 전달되면 HTTP 상태코드가 500으로 처리된다. 
- 발생하는 예외에 따라서 400, 404 등등 다른 상태코드로 처리
- 오류 메시지, 형식등을 API마다 다르게 처리

상태코드 변환 
- 예를 들어서 IllegalArgumentException 을 처리하지 못해서 컨트롤러 밖으로 넘어가는 일이 발생하면 HTTP 
  상태코드를 400으로 처리하고 싶다.

### ApiExceptionController - 수정

```java
@Slf4j
@RestController
public class ApiExceptionController {
    @GetMapping("/api/members/{id}")
    public MemberDto getMember(@PathVariable String id){
        if(id.equals("ex")){
            throw new RuntimeException("잘못된 사용자");
        }

        if(id.equals("bad")){
            throw new IllegalArgumentException("잘못된 입력 값");
        }

        return new MemberDto(id, "hello" + id);
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String memberId;
        private String name;
    }
}
```

실행
- http://localhost:8080/api/members/bad

응답
```json
{
    "timestamp": "2024-06-07T04:46:38.293+00:00",
    "status": 500,
    "error": "Internal Server Error",
    "path": "/api/members/bad"
}
```

### HandlerExceptionResolver

- 스프링 MVC는 컨트롤러(핸들러) 밖으로 예외가 던져진 경우 예외를 해결하고, 
  동작을 새로 정의할 수 있는 방법을 제공한다.
- 컨트롤러 밖으로 던져진 예외를 해결하고, 동작 방식을 변경하고 싶으면 
  HandlerExceptionResolver 를 사용하면 된다.

![1.JPG](Image%2F1.JPG)

![2.JPG](Image%2F2.JPG)

- ExceptionResolver 로 예외를 해결해도 postHandle()은 호출되지 않는다.

HandlerExceptionResolver - 인터페이스
```java
public interface HandlerExceptionResolver {
 ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response,
                               Object handler, Exception ex);
}
```
- handler : 핸들러(컨트롤러) 정보
- Exception ex : 핸들러(컨트롤러)에서 발생한 발생한 예외

### MyHandlerExceptionResolver
```java
@Slf4j
public class MyHandlerExceptionResolver implements HandlerExceptionResolver {
    @Override
    public ModelAndView resolveException(HttpServletRequest request,
                                         HttpServletResponse response,
                                         Object handler, Exception ex) {

       try{
           if(ex instanceof IllegalArgumentException){
              log.info("IllegalArgumentException resolver to 400");
              response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
              return new ModelAndView();
           }
       }
       catch (IOException e){
            log.error("resolver ex", e);
       }

        return null;
    }
}
```
- ExceptionResolver 가 ModelAndView 를 반환하는 이유는 마치 try, catch를 하듯이, Exception을 
  처리해서 정상 흐름 처럼 변경하는 것이 목적이다.
- 이름 그대로 Exception 을 Resolver(해결)하는 것이 목적이다.
- IllegalArgumentException 이 발생하면 response.sendError(400)를 호출해서 HTTP 상태
  코드를 400으로 지정하고, 빈 ModelAndView 를 반환한다.

반환 값에 따른 동작 방식 
- HandlerExceptionResolver 의 반환 값에 따른 DispatcherServlet 의 동작 방식은 다음과 같다.
- 빈 ModelAndView: new ModelAndView() 처럼 빈 ModelAndView 를 반환하면 뷰를 렌더링 하지 않고, 
  정상 흐름으로 서블릿이 리턴된다.
  - sendError() ➡️ was 전달 ➡️ 서블릿 ➡️ BasicController ➡️ /error 
  - sendError()❌ json 데이터 전달 ➡️ 서블릿 ➡️ json 응답 
- ModelAndView 지정: ModelAndView 에 View , Model 등의 정보를 지정해서 반환하면 뷰를 렌더링 한다.
- null: null 을 반환하면, 다음 ExceptionResolver 를 찾아서 실행한다. 만약 처리할 수 있는
  ExceptionResolver 가 없으면 예외 처리가 안되고, 기존에 발생한 예외를 서블릿 밖으로 던진다.

ExceptionResolver 활용
- 예외 상태 코드 변환
  - 예외를 response.sendError(xxx) 호출로 변경해서 서블릿에서 상태 코드에 따른 오류를 처리하도록 위임
  - 이후 WAS는 서블릿 오류 페이지를 찾아서 내부 호출, 예를 들어서 스프링 부트가 기본으로 설정한 /error 가 호출됨
- 뷰 템플릿 처리
  - ModelAndView 에 값을 채워서 예외에 따른 새로운 오류 화면 뷰 렌더링 해서 고객에게 제공
- API 응답 처리
  - response.getWriter().println("hello"); 처럼 HTTP 응답 바디에 직접 데이터를 넣어주는것도 가능하다.
  - 여기에 JSON 으로 응답하면 API 응답 처리를 할 수 있다. (이때 was까지 전달 ❌)


### WebConfig - 수정
```java
@Configuration
public class WebConfig implements WebMvcConfigurer{

    @Override
    public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
         resolvers.add(new MyHandlerExceptionResolver());
    }
}
```

- configureHandlerExceptionResolvers(..) 를 사용하면 스프링이 기본으로 등록하는
  ExceptionResolver 가 제거되므로 주의
- extendHandlerExceptionResolvers 를 사용하자.




