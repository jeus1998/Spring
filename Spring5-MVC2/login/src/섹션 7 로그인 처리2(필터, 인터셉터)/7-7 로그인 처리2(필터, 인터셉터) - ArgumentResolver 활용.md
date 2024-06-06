
# 로그인 처리2(필터, 인터셉터) - ArgumentResolver 활용

### HomeController - 추가

```java
@GetMapping("/")
public String homeLoginArgumentResolver(@Login Member loginMember, Model model) {
    if(loginMember == null){
         return "home";
    }
    model.addAttribute("member", loginMember);
    return "loginHome";
}
```

### @Login 애노테이션 생성
```java
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Login {}
```
- @Target(ElementType.PARAMETER) : 파라미터에만 사용
- @Retention(RetentionPolicy.RUNTIME) : 리플렉션 등을 활용할 수 있도록 런타임까지 애노테이션 
  정보가 남아있음

### LoginMemberArgumentResolver 생성

```java
@Slf4j
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        log.info("supportsParameter 실행");

        boolean hasLoginAnnotation = parameter.hasParameterAnnotation(Login.class);
        boolean hasMemberType = Member.class.isAssignableFrom(parameter.getParameterType());

        return hasLoginAnnotation && hasMemberType;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        log.info("resolveArgument 실행");

        HttpServletRequest request = (HttpServletRequest)webRequest.getNativeRequest();
        HttpSession session = request.getSession(false);

        if(session == null){
            return null;
        }
        Member member = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        return member;
    }
}
```
- supportsParameter() @Login 애노테이션이 있으면서 Member 타입이면 해당 ArgumentResolver가 사용된다.
- resolveArgument() 
  - 컨트롤러 호출 직전에 호출 되어서 필요한 파라미터 정보를 생성해준다.
  - 여기서는 세션에 있는 로그인 회원 정보인 member 객체를 찾아서 반환해준다.
  - 이후 스프링MVC는 컨트롤러의 메서드를 호출 하면서 여기에서 반환된 member 객체를 파라미터에 전달해준다.

### WebMvcConfigurer에 설정 추가
```java
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new LoginMemberArgumentResolver());
    }
}
```
- 실행해보면, 결과는 동일하지만, 더 편리하게 로그인 회원 정보를 조회할 수 있다.
- 이렇게 ArgumentResolver 를 활용하면 공통 작업이 필요할 때 컨트롤러를 더욱 편리하게 사용할 수 있다.



