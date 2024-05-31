
# 서블릿 - HTTP 응답 데이터(API JSON)

```java
@WebServlet(name = "responseJsonServlet", urlPatterns = "/response-json")
public class ResponseJsonServlet extends HttpServlet {

    ObjectMapper objectMapper = new ObjectMapper();
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Content-Type: application/json
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        HelloData helloData = new HelloData();
        helloData.setUsername("김영한");
        helloData.setAge(30);

        String result = objectMapper.writeValueAsString(helloData);

        response.getWriter().write(result);
    }
}
```

```text
{"username":"김영한","age":30}
```

- HTTP 응답으로 JSON을 반환할 때는 content-type을 application/json 로 지정해야 한다
- Jackson 라이브러리가 제공하는 objectMapper.writeValueAsString()를 사용하면 객체를 JSON 문자로
  변경할 수 있다.

### ✅참고
- application/json 은 스펙상 utf-8 형식을 사용하도록 정의되어 있다.
- 스펙에서 charset=utf-8 과 같은 추가 파라미터를 지원하지 않는다
- application/json 이라고만 사용해야지 application/json;charset=utf-8 이라고 전달하는 것은 의미 없는 파라미터를 추가한 것이 된다.
- response.getWriter()를 사용하면 추가 파라미터를 자동으로 추가해버린다.
- response.getOutputStream()으로 출력하면 그런 문제가 없다.