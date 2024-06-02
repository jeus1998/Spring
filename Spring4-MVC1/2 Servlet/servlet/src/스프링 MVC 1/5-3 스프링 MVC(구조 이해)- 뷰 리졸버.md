
# 스프링 MVC(구조 이해)- 뷰 리졸버

이번에는 뷰 리졸버에 대해서 자세히 알아보자

### OldController - View 조회할 수 있도록 변경

```java
@Component("/springmvc/old-controller")
public class OldController implements Controller {
    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        System.out.println("OldController.handleRequest");
        return new ModelAndView("new-form");
    }
}
```

- View를 사용할 수 있도록 다음 코드를 추가했다.
- return new ModelAndView("new-form");

실행
- http://localhost:8080/springmvc/old-controller
- 웹 브라우저에 Whitelabel Error Page 가 나오고, 콘솔에 OldController.handleRequest 이 출력

실행해보면 컨트롤러를 정상 호출되지만, Whitelabel Error Page 오류가 발생한다.

application.properties
```text
spring.mvc.view.prefix=/WEB-INF/views/
spring.mvc.view.suffix=.jsp
```

그럼 이제 new-form.jsp가 정상 호출된다.

### 뷰 리졸버 - InternalResourceViewResolver

스프링 부트는 InternalResourceViewResolver 라는 뷰 리졸버를 자동으로 등록하는데,
이때 application.properties 에 등록한 spring.mvc.view.prefix , spring.mvc.view.suffix 설정 정
보를 사용해서 등록한다.

참고로 권장하지는 않지만 설정 없이 다음과 같이 전체 경로를 주어도 동작하기는 한다.
return new ModelAndView("/WEB-INF/views/new-form.jsp");

### 뷰 리졸버 동작 방식

![16.JPG](Image%2F16.JPG)

스프링 부트가 자동 등록하는 뷰 리졸버
```text
1 = BeanNameViewResolver : 빈 이름으로 뷰를 찾아서 반환한다. 
2 = InternalResourceViewResolver : JSP를 처리할 수 있는 뷰를 반환한다.
```

1. 핸들러 어댑터 호출
   - 핸들러 어댑터를 통해 new-form 이라는 논리 뷰 이름을 획득한다.
2. ViewResolver 호출
   - new-form 이라는 뷰 이름으로 viewResolver를 순서대로 호출한다.
   - BeanNameViewResolver 는 new-form 이라는 이름의 스프링 빈으로 등록된 뷰를 찾아야 하는데 없다.
   - InternalResourceViewResolver 가 호출된다.
3. InternalResourceViewResolver
   - 이 뷰 리졸버는 InternalResourceView 를 반환한다.
4. 뷰 - InternalResourceView
   - InternalResourceView 는 JSP처럼 포워드 forward() 를 호출해서 처리할 수 있는 경우에 사용한다.
5. view.render()
   - view.render() 가 호출되고 InternalResourceView 는 forward() 를 사용해서 JSP를 실행한다.

✅참고
InternalResourceViewResolver 는 만약 JSTL 라이브러리가 있으면 InternalResourceView 를 상
속받은 JstlView 를 반환한다. JstlView 는 JSTL 태그 사용시 약간의 부가 기능이 추가된다.

✅참고
다른 뷰는 실제 뷰를 렌더링하지만, JSP의 경우 forward() 통해서 해당 JSP로 이동(실행)해야 렌더링이 된다. 
JSP를 제외한 나머지 뷰 템플릿들은 forward() 과정 없이 바로 렌더링 된다.

✅참고
Thymeleaf 뷰 템플릿을 사용하면 ThymeleafViewResolver 를 등록해야 한다. 최근에는 라이브러리만 추
가하면 스프링 부트가 이런 작업도 모두 자동화해준다.

