
# 스프링 MVC 기본 기능 - HTTP 요청 파라미터 -  단순 텍스트

- HTTP message body에 데이터를 직접 담아서 요청
  - HTTP API에서 주로 사용, JSON, XML, TEXT

요청 파라미터와 다르게, HTTP 메시지 바디를 통해 데이터가 직접 넘어오는 경우는 @RequestParam , 
@ModelAttribute 를 사용할 수 없다.
(물론 HTML Form 형식으로 전달되는 경우는 요청 파라미터로 인정된다.)

- 먼저 가장 단순한 텍스트 메시지를 HTTP 메시지 바디에 담아서 전송하고, 읽어보자.
- HTTP 메시지 바디의 데이터를 InputStream 을 사용해서 직접 읽을 수 있다.

### RequestBodyStringController v1,v2,v3

```java
@Slf4j
@Controller
public class RequestBodyStringController {
    @PostMapping("/request-body-string-v1")
    public void requestBodyString(HttpServletRequest request, HttpServletResponse response) throws IOException{
        ServletInputStream inputStream = request.getInputStream();
        String message = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);

        log.info("message = {}", message);

        response.getWriter().write("ok");
    }
    @PostMapping("/request-body-string-v2")
    public void requestBodyStringV2(InputStream inputStream, Writer responseWriter) throws IOException{

        String message = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);

        log.info("message = {}", message);

        responseWriter.write("ok");
    }

    @PostMapping("/request-body-string-v3")
    public HttpEntity<String> requestBodyStringV3(HttpEntity<String> httpEntity) throws IOException{

        String message = httpEntity.getBody();

        log.info("message = {}", message);

        return new HttpEntity<>("ok");
    }
}
```

- 스프링 MVC는 다음 파라미터를 지원한다
  - InputStream(Reader): HTTP 요청 메시지 바디의 내용을 직접 조회
  - OutputStream(Writer): HTTP 응답 메시지의 바디에 직접 결과 출력
  - HttpEntity: HTTP header, body 정보를 편리하게 조회
    - 메시지 바디 정보를 직접 조회
    - 요청 파라미터를 조회하는 기능과 관계 없음 @RequestParam ❌ , @ModelAttribute ❌ 
  - HttpEntity는 응답에도 사용 가능
    - 메시지 바디 정보 직접 반환
    - 헤더 정보 포함 가능
    - view 조회 ❌
- HttpEntity 를 상속받은 다음 객체들도 같은 기능을 제공한다.
  - RequestEntity
    - HttpMethod, url 정보가 추가, 요청에서 사용
  - ResponseEntity
    - HTTP 상태 코드 설정 가능, 응답에서 사용
    - ```return new ResponseEntity<String>("Hello World", responseHeaders, HttpStatus.CREATED)```

✅참고
스프링MVC 내부에서 HTTP 메시지 바디를 읽어서 문자나 객체로 변환해서 전달해주는데, 이때 HTTP 메시지
컨버터( HttpMessageConverter )라는 기능을 사용한다.

### RequestBodyStringController v4

```java
    @ResponseBody
    @PostMapping("/request-body-string-v4")
    public String requestBodyStringV4(@RequestBody String message){

        log.info("message = {}", message);

        return "ok";
    }
```

@RequestBody
- @RequestBody 를 사용하면 HTTP 메시지 바디 정보를 편리하게 조회할 수 있다.
- 헤더 정보가 필요하다면 HttpEntity 를 사용하거나 @RequestHeader 를 사용하면 된다.

요청 파라미터 vs HTTP 메시지 바디
- 요청 파라미터를 조회하는 기능: @RequestParam , @ModelAttribute
- HTTP 메시지 바디를 직접 조회하는 기능: @RequestBody

@ResponseBody
- @ResponseBody 를 사용하면 응답 결과를 HTTP 메시지 바디에 직접 담아서 전달할 수 있다
- 물론 이 경우에도 view를 사용하지 않는다.

