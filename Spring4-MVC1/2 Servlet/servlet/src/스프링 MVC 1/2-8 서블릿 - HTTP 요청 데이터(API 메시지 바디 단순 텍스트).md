
# 서블릿 - HTTP 요청 데이터(API 메시지 바디) - 단순 텍스트 

- HTTP message body에 데이터를 직접 담아서 요청
  - HTTP API에서 주로 사용, JSON, XML, TEXT
  - POST, PUT, PATCH
- HTTP 메시지 바디의 데이터를 InputStream을 사용해서 직접 읽을 수 있다

/basic/request/RequestBodyStringServlet.java
```java
@WebServlet(name = "requestBodyStringServlet", urlPatterns = "/request-body-string")
public class RequestBodyStringServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletInputStream inputStream = request.getInputStream();

        String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        System.out.println("messageBody = " + messageBody);

        response.getWriter().write("ok");
    }
}
```

### Postman을 사용해서 테스트 해보자.

- 문자 전송
  - POST http://localhost:8080/request-body-string
  - content-type: text/plain
  - message body: hello
  - 결과: messageBody = hello

### ✅ 참고
- inputStream은 byte 코드를 반환한다. - request.getInputStream() ➡️ byte 코드
- byte 코드를 우리가 읽을 수 있는 문자(String)로 보려면 문자표(Charset)를 지정해주어야 한다.
  - UTF_8 Charset을 지정 
  - StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8)