
# @ControllerAdvice

- @ExceptionHandler 를 사용해서 예외를 깔끔하게 처리할 수 있게 되었지만, 정상 코드와 예외 처리 코드가 하나의
  컨트롤러에 섞여 있다.
- @ControllerAdvice 또는 @RestControllerAdvice 를 사용하면 둘을 분리할 수 있다.

### ExControllerAdvice

```java
@Slf4j
@RestControllerAdvice
public class ExControllerAdvice {
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
}
```
### ApiExceptionV3Controller 

```java
@Slf4j
@RestController
public class ApiExceptionV3Controller {
    @GetMapping("/api3/members/{id}")
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

Postman 실행
- http://localhost:8080/api3/members/bad
- http://localhost:8080/api3/members/user-ex
- http://localhost:8080/api3/members/ex
- @ExceptionHandler 모두 잘 동작한다.

### @ControllerAdvice

- @ControllerAdvice 는 대상으로 지정한 여러 컨트롤러에 @ExceptionHandler , @InitBinder 기능
  을 부여해주는 역할을 한다.
- @ControllerAdvice 에 대상을 지정하지 않으면 모든 컨트롤러에 적용된다. (글로벌 적용)
- @RestControllerAdvice 는 @ControllerAdvice 와 같고, @ResponseBody 가 추가되어 있다. 
  - @Controller , @RestController 의 차이와 같다.

대상 컨트롤러 지정 방법
```java
// Target all Controllers annotated with @RestController
@ControllerAdvice(annotations = RestController.class)
public class ExampleAdvice1 {}

// Target all Controllers within specific packages
@ControllerAdvice("org.example.controllers")
public class ExampleAdvice2 {}

// Target all Controllers assignable to specific classes
@ControllerAdvice(assignableTypes = {ControllerInterface.class, 
AbstractController.class})
public class ExampleAdvice3 {}
```
- [스프링 공식 문서 참고](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-anncontroller-advice)
- 스프링 공식 문서 예제에서 보는 것 처럼 특정 애노테이션이 있는 컨트롤러를 지정할 수 있고, 
  특정 패키지를 직접 지정 할 수도 있다.
- 패키지 지정의 경우 해당 패키지와 그 하위에 있는 컨트롤러가 대상이 된다.
- 그리고 특정 클래스를 지정할 수도 있다.
- 대상 컨트롤러 지정을 생략하면 모든 컨트롤러에 적용된다.

💯정리
- @ExceptionHandler 와 @ControllerAdvice 를 조합하면 예외를 깔끔하게 해결할 수 있다.



