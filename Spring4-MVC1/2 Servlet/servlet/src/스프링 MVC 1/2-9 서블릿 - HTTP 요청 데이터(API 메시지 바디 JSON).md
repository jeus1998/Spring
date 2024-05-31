
# 서블릿 - HTTP 요청 데이터(API 메시지 바디 JSON)


### JSON 형식으로 파싱할 수 있게 객체 생성

/basic/HelloData.java
```java
@Setter @Getter
public class HelloData {
    private String username;
    private int age;
}
```

### String 그대로 출력 

```java
@WebServlet(name = "requestBodyJsonServlet", urlPatterns = "/request-body-json")
public class RequestBodyJsonServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletInputStream inputStream = request.getInputStream();
        String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);

        System.out.println("messageBody = " + messageBody);

        response.getWriter().write("ok");
    }
}
```

```text
messageBody = { "username" : "baejeu", "age" : 20   }
```

### 파싱해서 출력 

```java
@WebServlet(name = "requestBodyJsonServlet", urlPatterns = "/request-body-json")
public class RequestBodyJsonServlet extends HttpServlet {

    //  Jackson 라이브러리(Object Mapper): 스프링 부트 기본 제공 라이브러리
    private ObjectMapper objectMapper = new ObjectMapper();
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletInputStream inputStream = request.getInputStream();
        String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        
        // 파싱
        HelloData helloData = objectMapper.readValue(messageBody, HelloData.class);
        String username = helloData.getUsername();
        int age = helloData.getAge();

        System.out.println("username = " + username);
        System.out.println("age = " + age);

        response.getWriter().write("ok");
    }
}
```

```text
username = baejeu
age = 20
```

### ✅ 참고

- JSON 결과를 파싱해서 사용할 수 있는 자바 객체로 변환하려면 Jackson, Gson 같은 JSON 변환 라이브러리
  를 추가해서 사용해야 한다.
- 스프링 부트로 Spring MVC를 선택하면 기본으로 Jackson 라이브러리(ObjectMapper)를 함께 제공한다.
- HTML form 데이터도 메시지 바디를 통해 전송되므로 직접 읽을 수 있다.
- 편리한 파리미터 조회 기능(request.getParameter(...) )을 이미 제공하기 때문에 
  파라미터 조회 기능을 사용하면 된다.