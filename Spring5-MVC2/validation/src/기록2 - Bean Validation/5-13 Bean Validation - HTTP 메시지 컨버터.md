
# Bean Validation - HTTP 메시지 컨버터

@Valid , @Validated 는 HttpMessageConverter ( @RequestBody )에도 적용할 수 있다.

ValidationItemApiController 생성
```java
@Slf4j
@RestController
@RequestMapping("/validation/api/items")
public class ValidationItemApiController {
    @PostMapping("/add")
    public Object addItem(@RequestBody @Validated ItemSaveForm form, BindingResult bindingResult){
        log.info("API 컨트롤러 호출");

        if(bindingResult.hasErrors()){
            log.info("검증 오류 발생 errors={}", bindingResult);
            return bindingResult.getAllErrors();
        }

        log.info("성공 로직 실행");
        return form;
    }
}
```

Postman을 사용해서 테스트 해보자.

성공 요청
```text
POST http://localhost:8080/validation/api/items/add
{"itemName":"hello", "price":1000, "quantity": 10}
```

API의 경우 3가지 경우를 나누어 생각해야 한다.
- 성공 요청: 성공
- 실패 요청: JSON을 객체로 생성하는 것 자체가 실패함
- 검증 오류 요청: JSON을 객체로 생성하는 것은 성공했고, 검증에서 실패함

성공 요청 로그
```text
API 컨트롤러 호출
성공 로직 실행
```

실패 요청 
```text
POST http://localhost:8080/validation/api/items/add
{"itemName":"hello", "price":"A", "quantity": 10}
```

실패 요청 결과
```text
{
     "timestamp": "2021-04-20T00:00:00.000+00:00",
     "status": 400,
     "error": "Bad Request",
     "message": "",
     "path": "/validation/api/items/add"
}
```
- HttpMessageConverter 에서 요청 JSON을 ItemSaveForm 객체로 생성하는데 실패한다.
- 이 경우는 ItemSaveForm 객체를 만들지 못하기 때문에 컨트롤러 자체가 호출되지 않고 그 전에 예외가 발생한다.
- 물론 Validator도 실행되지 않는다. 

검증 오류 요청
- 이번에는 HttpMessageConverter 는 성공하지만 검증(Validator)에서 오류가 발생하는 경우를 확인해보자
```text
POST http://localhost:8080/validation/api/items/add
{"itemName":"hello", "price":1000, "quantity": 10000}
```
- 수량( quantity )이 10000 이면 BeanValidation @Max(9999) 에서 걸린다.

검증 오류 결과
```text
[
     {
         "codes": [
         "Max.itemSaveForm.quantity",
         "Max.quantity",
         "Max.java.lang.Integer",
         "Max"
     ],
     "arguments": [
     {
         "codes": [
         "itemSaveForm.quantity",
         "quantity"
     ],
         "arguments": null,
         "defaultMessage": "quantity",
         "code": "quantity"
     },
         9999
     ],
         "defaultMessage": "9999 이하여야 합니다",
         "objectName": "itemSaveForm",
         "field": "quantity",
         "rejectedValue": 10000,
         "bindingFailure": false,
         "code": "Max"
     }
 ]
```

return bindingResult.getAllErrors(); 는 ObjectError 와 FieldError 를 반환한다.
스프링이 이 객체를 JSON으로 변환해서 클라이언트에 전달했다.
여기서는 예시로 보여주기 위해서 검증 오류 객체들을 그대로 반환 했다. 
실제 개발할 때는 이 객체들을 그대로 사용하지 말고, 필요한 데이터만 뽑아서 별도의 API 스펙을 정의하고 그에
맞는 객체를 만들어서 반환해야 한다.

### @ModelAttribute vs @RequestBody

- HTTP 요청 파리미터를 처리하는 @ModelAttribute 는 각각의 필드 단위로 세밀하게 적용된다.
- 그래서 특정 필드에 타입이 맞지 않는 오류가 발생해도 나머지 필드는 정상 처리할 수 있었다.
- HttpMessageConverter 는 @ModelAttribute 와 다르게 각각의 필드 단위로 적용되는 것이 아니라, 전체 객체
  단위로 적용된다.
- 따라서 메시지 컨버터의 작동이 성공해서 ItemSaveForm 객체를 만들어야 @Valid , @Validated 가 적용된다.
  - @ModelAttribute 는 필드 단위로 정교하게 바인딩이 적용된다. 
  - 특정 필드가 바인딩 되지 않아도 나머지 필드는 정상 바인딩 되고, Validator를 사용한 검증도 적용할 수 있다.
  - @RequestBody 는 HttpMessageConverter 단계에서 JSON 데이터를 객체로 변경하지 못하면 이후 단계 자체가 
    진행되지 않고 예외가 발생한다.
  - 컨트롤러도 호출되지 않고, Validator도 적용할 수 없다.

