
# 서블릿 - Hello 서블릿

### 스프링 부트 서블릿 환경 구성

- 서블릿을 자동 등록해서 사용하도록 @ServletComponentScan 추가
```java
@ServletComponentScan
@SpringBootApplication
public class ServletApplication {
	public static void main(String[] args) {
		SpringApplication.run(ServletApplication.class, args);
	}
}
```

### 서블릿 등록하기

hello.servlet.basic.HelloServlet.java

```java
@WebServlet(name = "helloServlet", urlPatterns = "/hello")
public class HelloServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("HelloServlet.service");
        System.out.println("request = " + request);
        System.out.println("response = " + response);

        String username = request.getParameter("username");
        System.out.println("username = " + username);

        response.setContentType("text/plain");
        response.setCharacterEncoding("utf-8");
        response.getWriter().write("hello " + username);

    }
}
```
- @WebServlet 서블릿 애노테이션
  - name: 서블릿 이름
  - urlPatterns: URL 매핑

```text
HTTP 요청을 통해 매핑된 URL이 호출되면 서블릿 컨테이너는 다음 메서드를 실행한다.
protected void service(HttpServletRequest request, HttpServletResponse response)
```

- 웹 브라우저 실행
   - http://localhost:8080/hello?username=baejeu
   - 결과: hello baejeu
- 콘솔 실행결과

```text
HelloServlet.service
request = org.apache.catalina.connector.RequestFacade@5cc907fa
response = org.apache.catalina.connector.ResponseFacade@630b65be
username = baejeu
```

### HTTP 요청 메시지 로그로 확인하기

- application.properties
- spring boot 3.2 이전: logging.level.org.apache.coyote.http11=debug
- spring boot 3.2 이상: logging.level.org.apache.coyote.http11=trace

```text
...o.a.coyote.http11.Http11InputBuffer: Received [GET /hello?username=servlet
HTTP/1.1
Host: localhost:8080
Connection: keep-alive
Cache-Control: max-age=0
sec-ch-ua: "Chromium";v="88", "Google Chrome";v="88", ";Not A Brand";v="99"
sec-ch-ua-mobile: ?0
Upgrade-Insecure-Requests: 1
User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 11_2_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.150 Safari/537.36
Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/
webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9
Sec-Fetch-Site: same-origin
Sec-Fetch-Mode: navigate
Sec-Fetch-User: ?1
Sec-Fetch-Dest: document
Referer: http://localhost:8080/basic.html
Accept-Encoding: gzip, deflate, br
Accept-Language: ko,en-US;q=0.9,en;q=0.8,ko-KR;q=0.7 
]
```

- ✅ 참고 - 운영서버에 이렇게 모든 요청 정보를 다 남기면 성능저하가 발생할 수 있다. 개발 단계에서만 적용하자.

### 서블릿 컨테이너 동작 방식 

![1.JPG](Image%2F1.JPG)

![2.JPG](Image%2F2.JPG)

![3.JPG](Image%2F3.JPG)

- ✅ 참고 - HTTP 응답에서 Content-Length는 웹 애플리케이션 서버가 자동으로 생성해준다.

### welcome page 

- 지금부터 개발할 내용을 편리하게 참고할 수 있도록 welcome 페이지를 만들어두자
- webapp 경로에 index.html 을 두면 http://localhost:8080 호출시 index.html 페이지가 열린다.
- main/webapp/index.html
- main/webapp/basic.html 



