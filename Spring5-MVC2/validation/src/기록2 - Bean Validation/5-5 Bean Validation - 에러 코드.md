
# Bean Validation - 에러 코드

Bean Validation이 기본으로 제공하는 오류 메시지를 좀 더 자세히 변경하고 싶으면 어떻게 하면 될까?

Bean Validation을 적용하고 bindingResult 에 등록된 검증 오류 코드를 보자.
오류 코드가 애노테이션 이름으로 등록된다. 마치 typeMismatch 와 유사하다.

NotBlank 라는 오류 코드를 기반으로 MessageCodesResolver 를 통해 다양한 메시지 코드가 순서대로 
생성된다.

@NotBlank
- NotBlank.item.itemName
- NotBlank.itemName
- NotBlank.java.lang.String
- NotBlank

@Range
- Range.item.price
- Range.price
- Range.java.lang.Integer
- Range

### 메시지 등록 

- errors.properties
```text
#Bean Validation 추가
NotBlank={0} 공백X 
Range={0}, {2} ~ {1} 허용
Max={0}, 최대 {1}
```
- {0} 은 필드명
- {1} , {2} ...은 각 애노테이션 마다 다르다.

✅참고
- Range={0}, {2} ~ {1} 허용
- index 순서가 {2} ~ {1} 인 이유는 Annotation의 속성의 이름이 알파벳 순서로 처리되기 때문
- Max가 Min보다 알파벳 기준으로 우선순위가 높다. 
- 그래서 Min ~ Max 표현을 위해서 ➡️ {2} ~ {1} 

### BeanValidation 메시지 찾는 순서

- 생성된 메시지 코드 순서대로 messageSource 에서 메시지 찾기
- 애노테이션의 message 속성 사용 ➡️ @NotBlank(message = "공백! {0}")
  - fiendError & ObjectError - default message 파라미터와 동일한 동작
- 라이브러리가 제공하는 기본 값 사용 ➡️ 공백일 수 없습니다.

애노테이션의 message 사용 예
```java
@NotBlank(message = "공백은 입력할 수 없습니다.")
private String itemName;
```


