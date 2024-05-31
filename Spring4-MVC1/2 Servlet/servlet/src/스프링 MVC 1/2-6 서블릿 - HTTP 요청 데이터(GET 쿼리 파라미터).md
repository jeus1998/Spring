
# HTTP 요청 데이터(GET 쿼리 파라미터)

/basic/request/RequestParamServlet.java

```java
@WebServlet(name = "requestParamServlet", urlPatterns = "/request-param")
public class RequestParamServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("[전체 파라미터 조회] - start");
        request.getParameterNames().asIterator().forEachRemaining(
                paramName -> System.out.println(paramName + ": " + request.getParameter(paramName))
        );
        System.out.println("[전체 파라미터 조회] - end");
        System.out.println();

        System.out.println("[단일 파라미터 조회]");
        String username = request.getParameter("username");
        String age = request.getParameter("age");

        System.out.println("username = " + username);
        System.out.println("age = " + age);
        System.out.println();

        System.out.println("[이름이 같은 복수 파라미터 조회]");
        String[] usernames = request.getParameterValues("username");
        for (String name : usernames) {
            System.out.println("name = " + name);
        }

        response.getWriter().write("ok");
    }
}
```

```text
[전체 파라미터 조회] - start
username: hello
age: 20
[전체 파라미터 조회] - end

[단일 파라미터 조회]
username = hello
age = 20

[이름이 같은 복수 파라미터 조회]
name = hello
name = hello3333
```

### 복수 파라미터에서 단일 파라미터 조회

- username=hello&username=kim 과 같이 파라미터 이름은 하나인데, 값이 중복이면 어떻게 될까?
- request.getParameter() 는 하나의 파라미터 이름에 대해서 단 하나의 값만 있을 때 사용해야 한다.
- 지금처럼 중복일 때는 request.getParameterValues() 를 사용해야 한다.
- 참고로 이렇게 중복일 때 request.getParameter() 를 사용하면 request.getParameterValues() 의 첫
  번째 값을 반환한다.