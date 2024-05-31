
# 2-11 서블릿 - HTTP 응답 데이터(단순 텍스트, HTML)

HTTP 응답 메시지는 주로 다음 내용을 담아서 전달한다

- 단순 텍스트 응답
  - 앞에서 살펴봄 ( writer.println("ok"); )
- HTML 응답
- HTTP API - MessageBody JSON 응답

### HTTP 응답 데이터 - HTML 응답

/basic/response/ResponseHtmlServlet.java

```java
@WebServlet(name = "responseHtmlServlet", urlPatterns = "/response-html")
public class ResponseHtmlServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Content-Type: text/html;charset=urf-8
        response.setContentType("text/html");
        response.setCharacterEncoding("utf-8");

        PrintWriter writer = response.getWriter();
        writer.println("<html>");
        writer.println("<body>");
        writer.println(" <div><b>안녕?</div>");
        writer.println("</body>");
        writer.println("</html>");

    }
}
```
- HTTP 응답으로 HTML을 반환할 때는 content-type을 text/html 로 지정해야 한다.

```html
<html>
<body>
 <div><b>안녕?</div>
</body>
</html>
```


