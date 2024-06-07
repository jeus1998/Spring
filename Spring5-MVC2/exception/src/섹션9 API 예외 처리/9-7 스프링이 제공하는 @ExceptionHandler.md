
# 스프링이 제공하는 @ExceptionHandler

### API 예외처리의 어려운 점
- HandlerExceptionResolver 를 떠올려 보면 ModelAndView 를 반환해야 했다.
  - 이것은 API 응답에는 필요하지 않다.
- API 응답을 위해서 HttpServletResponse 에 직접 응답 데이터를 넣어주었다.
  - 이것은 매우 불편하다.
  - 스프링 컨트롤러에 비유하면 마치 과거 서블릿을 사용하던 시절로 돌아간 것 같다.
    - response.setContentType()
    - ObjectMapper
    - response.getWrite() 등등..
- 특정 컨트롤러에서만 발생하는 예외를 별도로 처리하기 어렵다.
  - 회원을 처리하는 컨트롤러에서 발생하는 RuntimeException 예외
  - 상품을 관리하는 컨트롤러에서 발생하는 동일한 RuntimeException 예외
  - 서로 다르게 처리해야 한다.

### @ExceptionHandler

- 스프링은 API 예외 처리 문제를 해결하기 위해 @ExceptionHandler 라는 애노테이션을 사용하는 매우 편리한 예외 
  처리 기능을 제공
- ExceptionHandlerExceptionResolver 를 기본으로 제공
  - ExceptionResolver중에 우선순위도 가장 높다.
  - 실무에서 API 예외 처리는 대부분 이 기능을 사용

### ErrorResult
- 예외가 발생했을 때 API 응답으로 사용하는 객체를 정의했다.
```java
@Data
@AllArgsConstructor
public class ErrorResult {
    private String code;
    private String message;
}
```

### ApiExceptionV2Controller

```java
@Slf4j
@RestController
public class ApiExceptionV2Controller {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResult illegalExceptionHandler(IllegalArgumentException e){
        log.error("[IllegalArgumentException handler] ex", e);
        return new ErrorResult("BAD", e.getMessage());
    }


    // @ExceptionHandler(UserException.class) // 빼도 동일하게 동작
    @ExceptionHandler
    public ResponseEntity<ErrorResult> userExceptionHandler(UserException e){
        log.error("[UserException handler] ex", e);
        ErrorResult errorResult = new ErrorResult("user-ex", e.getMessage());
        return new ResponseEntity(errorResult, HttpStatus.BAD_GATEWAY);
    }
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResult exHandler(Exception e){
        log.error("[exception handler] ex", e);
        return new ErrorResult("all ex", e.getMessage());
    }

    @GetMapping("/api2/members/{id}")
    public MemberDto getMember(@PathVariable String id){
        log.info("id={}", id);

        if(id.equals("ex")){
            throw new RuntimeException("잘못된 사용자");
        }
        if(id.equals("bad")){
            throw new IllegalArgumentException("잘못된 입력 값");
        }
        if(id.equals("user-ex")){
            throw new UserException("사용자 오류");
        }

        return new MemberDto(id, "hello" + id);
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
       private String memberId;
       private String name;
    }
}
```

### @ExceptionHandler 예외 처리 방법

- @ExceptionHandler 애노테이션을 선언하고, 해당 컨트롤러에서 처리하고 싶은 예외를 지정해주면 된다.
- 해당 컨트롤러에서 예외가 발생하면 이 메서드가 호출된다.
  - 참고로 지정한 예외 또는 그 예외의 자식 클래스는 모두 잡을 수 있다.

다음 예제는 IllegalArgumentException 또는 그 하위 자식 클래스를 모두 처리할 수 있다.
```java
@ResponseStatus(HttpStatus.BAD_REQUEST)
@ExceptionHandler(IllegalArgumentException.class)
public ErrorResult illegalExceptionHandler(IllegalArgumentException e){
    log.error("[IllegalArgumentException handler] ex", e);
    return new ErrorResult("BAD", e.getMessage());
}
```
- @ResponseStatus가 없다면? 
  - 예외가 발생해도 정상 응답 처리를 하기 때문에 200 status code가 반환된다.

우선순위
- 스프링의 우선순위는 항상 자세한 것이 우선권을 가진다.
- 예를 들어서 부모, 자식 클래스가 있고 다음과 같이 예외가 처리된다.
```java
@ExceptionHandler(부모예외.class)
public String 부모예외처리()(부모예외 e) {}

@ExceptionHandler(자식예외.class)
public String 자식예외처리()(자식예외 e) {}
```
- @ExceptionHandler 에 지정한 부모 클래스는 자식 클래스까지 처리할 수 있다.
- 따라서 자식예외 가 발생하면 부모 예외처리() , 자식 예외처리() 둘다 호출 대상이 된다.
- 그런데 둘 중 더 자세한 것이 우선권을 가지므로 자식 예외처리()가 호출된다.
- 물론 부모예외 가 호출되면 부모 예외처리()만 호출 대상이 되므로 부모 예외처리()가 호출된다.

다양한 예외
- 다음과 같이 다양한 예외를 한번에 처리할 수 있다.
```java
@ExceptionHandler({AException.class, BException.class})
public String ex(Exception e) {
   log.info("exception e", e);
}
```

예외 생략
- @ExceptionHandler 에 예외를 생략할 수 있다. 생략하면 메서드 파라미터의 예외가 지정된다.
```java
@ExceptionHandler
public ResponseEntity<ErrorResult> userExHandle(UserException e) {}
```

파라미터와 응답
- @ExceptionHandler 에는 마치 스프링의 컨트롤러의 파라미터 응답처럼 다양한 파라미터와 응답을 지정할 수 있다.
- 자세한 파라미터와 응답은 다음 공식 메뉴얼을 참고하자.
- [@ExceptionHandler 공식 메뉴얼](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-annexceptionhandler-args)


Postman 실행
- http://localhost:8080/api2/members/bad

```java
@ResponseStatus(HttpStatus.BAD_REQUEST)
@ExceptionHandler(IllegalArgumentException.class)
public ErrorResult illegalExceptionHandler(IllegalArgumentException e){
    log.error("[IllegalArgumentException handler] ex", e);
    return new ErrorResult("BAD", e.getMessage());
}
```

실행 흐름
- 컨트롤러를 호출한 결과 IllegalArgumentException 예외가 컨트롤러 밖으로 던져진다.
- 예외가 발생했으로 ExceptionResolver가 작동한다. 
  - 가장 우선순위가 높은 ExceptionHandlerExceptionResolver가 실행된다.
  - ExceptionHandlerExceptionResolver 는 해당 컨트롤러에 IllegalArgumentException 을 처리 
    할 수 있는 @ExceptionHandler가 있는지 확인한다.
- illegalExceptionHandler()를 실행한다. 
  - @RestController 이므로 illegalExHandle()에도 @ResponseBody 가 적용된다.
  - 따라서 HTTP 컨버터가 사용되고, 응답이 다음과 같은 JSON으로 반환된다.
  - @ResponseStatus(HttpStatus.BAD_REQUEST) 를 지정했으므로 HTTP 상태 코드 400으로 응답한다

```json
{
 "code": "BAD",
 "message": "잘못된 입력 값"
}
```

Postman 실행
- http://localhost:8080/api2/members/user-ex

UserException 처리
```java
@ExceptionHandler
  public ResponseEntity<ErrorResult> userExceptionHandler(UserException e){
      log.error("[UserException handler] ex", e);
      ErrorResult errorResult = new ErrorResult("user-ex", e.getMessage());
      return new ResponseEntity(errorResult, HttpStatus.BAD_GATEWAY);
  }
```
- @ExceptionHandler 에 예외를 지정하지 않으면 해당 메서드 파라미터 예외를 사용한다.
  - UserException 을 사용
- ResponseEntity 를 사용해서 HTTP 메시지 바디에 직접 응답한다.
  - 물론 HTTP 컨버터가 사용된다. 
- ResponseEntity 를 사용하면 HTTP 응답 코드를 프로그래밍해서 동적으로 변경할 수 있다.
-  앞서 살펴본 @ResponseStatus 는 애노테이션이므로 HTTP 응답 코드를 동적으로 변경할 수 없다.

Postman 실행
- http://localhost:8080/api2/members/ex

Exception 처리
```java
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
@ExceptionHandler
public ErrorResult exHandler(Exception e){
    log.error("[exception handler] ex", e);
    return new ErrorResult("all ex", e.getMessage());
}
```

- throw new RuntimeException("잘못된 사용자") 이 코드가 실행되면서, 컨트롤러 밖으로
  RuntimeException 이 던져진다.
- RuntimeException 은 Exception 의 자식 클래스이다. 따라서 이 메서드가 호출된다.
- @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) 로 HTTP 상태 코드를 500으로 응답한다.








