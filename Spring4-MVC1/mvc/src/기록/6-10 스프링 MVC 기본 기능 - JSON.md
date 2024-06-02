
# 스프링 MVC 기본 기능 - JSON

이번에는 HTTP API에서 주로 사용하는 JSON 데이터 형식을 조회해보자.

### RequestBodyJsonController v1,v2,v3

```java
/**
 * {"username":"hello", "age":20}
 * content-type: application/json
 */
@Slf4j
@Controller
public class RequestBodyJsonController {
    private ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/request-body-json-v1")
    public void requestBodyJsonV1(HttpServletRequest request, HttpServletResponse response) throws IOException{
        ServletInputStream inputStream = request.getInputStream();
        String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);

        log.info("messageBody = {}", messageBody);

        HelloData helloData = objectMapper.readValue(messageBody, HelloData.class);
        log.info("username = {}, age = {}", helloData.getUsername(), helloData.getAge());

        response.getWriter().write("ok");
    }

    @ResponseBody
    @PostMapping("/request-body-json-v2")
    public String requestBodyJsonV2(@RequestBody String messageBody) throws IOException{

        log.info("messageBody = {}", messageBody);

        HelloData helloData = objectMapper.readValue(messageBody, HelloData.class);
        log.info("username = {}, age = {}", helloData.getUsername(), helloData.getAge());

        return "ok";
    }

    @ResponseBody
    @PostMapping("/request-body-json-v3")
    public String requestBodyJsonV3(@RequestBody HelloData helloData){

        log.info("username = {}, age = {}", helloData.getUsername(), helloData.getAge());

        return "ok";
    }
}
```
v1 
- HttpServletRequest를 사용해서 직접 HTTP 메시지 바디에서 데이터를 읽어와서, 문자로 변환한다.
- 문자로 된 JSON 데이터를 Jackson 라이브러리인 objectMapper 를 사용해서 자바 객체로 변환한다.

v2
- @RequestBody 를 사용해서 HTTP 메시지에서 데이터를 꺼내고 messageBody에 저장한다.
- 문자로 된 JSON 데이터인 messageBody 를 objectMapper 를 통해서 자바 객체로 변환한다.

v3
- @RequestBody HelloData data
- @RequestBody 에 직접 만든 객체를 지정할 수 있다.

HttpEntity , @RequestBody 를 사용하면 HTTP 메시지 컨버터가 HTTP 메시지 바디의 내용을 우리가 원하는 문
자나 객체 등으로 변환해준다.
HTTP 메시지 컨버터는 문자 뿐만 아니라 JSON도 객체로 변환해주는데, 우리가 방금 V2에서 했던 작업을 대신 처리
해준다.

### @RequestBody는 생략 불가능

스프링은 @ModelAttribute , @RequestParam 과 같은 해당 애노테이션을 생략시 다음과 같은 규칙을 적용한다
- String , int , Integer 같은 단순 타입 = @RequestParam
- 나머지 = @ModelAttribute (argument resolver 로 지정해둔 타입 외)

따라서 이 경우 HelloData에 @RequestBody 를 생략하면 @ModelAttribute 가 적용되어버린다.
- ```public String test(HelloData data)```
- ```public String test(@ModelAttribute HelloData data)``` 
- 따라서 생략하면 HTTP 메시지 바디가 아니라 요청 파라미터를 처리하게 된다.

### RequestBodyJsonController - requestBodyJsonV4  - HttpEntity

HttpEntity 활용하기 
```java
    @PostMapping("/request-body-json-v4")
    public HttpEntity<String> requestBodyJsonV4(HttpEntity<HelloData> httpEntity){
        HelloData helloData = httpEntity.getBody();
        log.info("username = {}, age = {}", helloData.getUsername(), helloData.getAge());
        return new HttpEntity<>("ok");
    }
```

- HttpEntity 활용하기2
  - RequestEntity
  - ResponseEntity

```java
   @PostMapping("/request-body-json-v4-2")
    public ResponseEntity<String> requestBodyJsonV4_2(RequestEntity<HelloData> requestEntity){
        HelloData helloData = requestEntity.getBody();
        log.info("username = {}, age = {}", helloData.getUsername(), helloData.getAge());
        return  new ResponseEntity<String>("ok", null, HttpServletResponse.SC_CREATED);
    }
```

### RequestBodyJsonController - requestBodyJsonV5  return Json

```java
    @ResponseBody
    @PostMapping("/request-body-json-v5")
    public HelloData requestBodyJsonV5(@RequestBody HelloData helloData){
        log.info("username = {}, age = {}", helloData.getUsername(), helloData.getAge());
        return helloData;
    }
```

- @ResponseBody
  - 응답의 경우에도 @ResponseBody 를 사용하면 해당 객체를 HTTP 메시지 바디에 직접 넣어줄 수 있다.
  - 물론 이 경우에도 HttpEntity 를 사용해도 된다.
- @RequestBody 요청
  - JSON 요청 ➡️ HTTP 메시지 컨버터 ➡️ 객체
- @ResponseBody 응답
  - 객체 ➡️ HTTP 메시지 컨버터 ➡️ JSON 응답



