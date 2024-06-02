
# 스프링 MVC 기본 기능 - 요청 매핑

### MappingController

```java
@RestController
@Slf4j
public class MappingController {

    /**
     * 기본 요청
     * 스프링 부트 3.0 이전 허용 /hello-basic, /hello-basic/, /hello-go, /hello-go/
     * 스프링 부트 3.0 이후 허용 /hello-basic, /hello-go
     * HTTP 메서드 모두 허용 GET, HEAD, POST, PUT, PATCH, DELETE
    */
    @RequestMapping({"/hello-basic", "/hello-go"})
    public String helloBasic(){
        return "ok";
    }
    /**
     * method 특정 HTTP 메서드 요청만 허용
     * GET, HEAD, POST, PUT, PATCH, DELETE
     */
    @RequestMapping(value = "/mapping-get-v1", method = RequestMethod.GET)
    public String mappingGetV1() {
         log.info("mappingGetV1");
         return "ok";
    }
    
    /**
     * 편리한 축약 애노테이션 (코드보기)
     * @GetMapping
     * @PostMapping
     * @PutMapping
     * @DeleteMapping
     * @PatchMapping
     */
    @GetMapping(value = "/mapping-get-v2")
    public String mappingGetV2() {
         log.info("mapping-get-v2");
         return "ok";
    }
}
```

- 스프링 부트 3.0 이후
  - 스프링 부트 3.0 부터는 /hello-basic , /hello-basic/ 는 서로 다른 URL 요청을 사용해야 한다.
  - 기존에는 마지막에 있는 / (slash)를 제거했지만, 스프링 부트 3.0 부터는 마지막의 / (slash)를 유지한다.
  - 따라서 다음과 같이 다르게 매핑해서 사용해야 한다.
  - 매핑: /hello-basic URL 요청: /hello-basic
  - 매핑: /hello-basic/ URL 요청: /hello-basic/

- HTTP 메서드
  - @RequestMapping 에 method 속성으로 HTTP 메서드를 지정하지 않으면 HTTP 메서드와 무관하게 호출된다.
  - 모두 허용 GET, HEAD, POST, PUT, PATCH, DELETE

- HTTP 메서드 매핑 축약
  - HTTP 메서드를 축약한 애노테이션을 사용하는 것이 더 직관적이다.
  - 코드를 보면 내부에서 @RequestMapping 과 method 를 지정해서 사용하는 것을 확인할 수 있다.

### PathVariable(경로 변수)

```java
    /**
     * PathVairable 사용 
     * 변수명이 같으면 생략 가능 
     * @PathVariable("userId) String userId -> @PathVariable userId
     */
    @GetMapping("/mapping/{userId}")
    public String mappingPath(@PathVariable("userId") String data){
        log.info("mappingPath userId={}", data);
        return "ok";
    }
```

- 최근 HTTP API는 다음과 같이 리소스 경로에 식별자를 넣는 스타일을 선호한다
  - /mapping/userA
  - /users/1
- @PathVariable 의 이름과 파라미터 이름이 같으면 생략할 수 있다

### PathVariable 사용 - 다중

```java
    /**
     * @PathVariable 다중 사용
     */
    @GetMapping("/mapping/users/{userId}/orders/{orderId}")
    public String mappingPath((@PathVariable String userId, @PathVariable Long orderId){
        log.info("mappingPath userId={}, orderId={}", userId, orderId);
        return "ok";
    }
```

### 특정 파라미터 조건 매핑

```java
    /**
     * 파라미터로 추가 매핑
     * params="mode",
     * params="!mode"
     * params="mode=debug"
     * params="mode!=debug" (! = )
     * params = {"mode=debug","data=good"}
     */
    @GetMapping(value = "/mapping-param", params = "mode=debug")
    public String mappingParam() {
         log.info("mappingParam");
         return "ok";
    }
```
- 특정 파라미터가 있거나 없는 조건을 추가할 수 있다. 잘 사용하지는 않는다.

### 특정 헤더 조건 매핑

```java
    /**
     * 특정 헤더로 추가 매핑
     * headers="mode",
     * headers="!mode"
     * headers="mode=debug"
     * headers="mode!=debug" (! = )
     */
    @GetMapping(value = "/mapping-header", headers = "mode=debug")
    public String mappingHeader(){
        log.info("mappingHeader");
        return "ok";
    }
```

### 미디어 타입 조건 매핑 - HTTP 요청 Content-Type, consume

```java
    /**
     * Content-Type 헤더 기반 추가 매핑 Media Type
     * consumes="application/json"
     * consumes="!application/json"
     * consumes="application/*"
     * consumes="*\/*"
     * MediaType.APPLICATION_JSON_VALUE
     */
    @PostMapping(value = "/mapping-consume", consumes = "application/json")
    public String mappingConsumes() {
         log.info("mappingConsumes");
         return "ok";
    }
```

- HTTP 요청의 Content-Type 헤더를 기반으로 미디어 타입으로 매핑한다.
- 만약 맞지 않으면 HTTP 415 상태코드(Unsupported Media Type)을 반환한다.

예시)consumes
```text
consumes = "text/plain"
consumes = {"text/plain", "application/*"}
consumes = MediaType.TEXT_PLAIN_VALUE
```

### 미디어 타입 조건 매핑 - HTTP 요청 Accept, produce

```java
    /**
     * Accept 헤더 기반 Media Type
     * produces = "text/html"
     * produces = "!text/html"
     * produces = "text/*"
     * produces = "*\/*"
     */
    @PostMapping(value = "/mapping-produce", produces = "text/html")
    public String mappingProduces() {
         log.info("mappingProduces");
         return "ok";
    }
```

- HTTP 요청의 Accept 헤더를 기반으로 미디어 타입으로 매핑한다.
- 만약 맞지 않으면 HTTP 406 상태코드(Not Acceptable)을 반환한다.

예시)
```text
produces = "text/plain"
produces = {"text/plain", "application/*"}
produces = MediaType.TEXT_PLAIN_VALUE
produces = "text/plain;charset=UTF-8"
```

### 정리
- consumes - 클라이언트 요청 Content-Type 헤더 기반 동작 
- produce - 클라이언트 요청 Accept 헤더 기반 동작 
