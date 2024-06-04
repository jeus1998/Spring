
# Bean Validation - 스프링 적용

### 스프링 mvc & Bean Validation

```java
@PostMapping("/add")
public String addItem(@Validated @ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

    // 검증에 실패하면 다시 입력 폼으로
    if(bindingResult.hasErrors()){
        log.info("errors = {}", bindingResult);
        return "validation/v3/addForm";
    }

    // 성공 로직
    Item savedItem = itemRepository.save(item);
    redirectAttributes.addAttribute("itemId", savedItem.getId());
    redirectAttributes.addAttribute("status", true);
    return "redirect:/validation/v3/items/{itemId}";
}
```

- ItemValidator 삭제 
- 실행: http://localhost:8080/validation/v3/items
- 애노테이션 기반의 Bean Validation 정상 동작 
- 특정 필드의 범위를 넘어서는 검증(가격 * 수량의 합은 10,000원 이상) 동작 ❌

스프링 MVC는 어떻게 Bean Validator를 사용?
- 스프링 부트가 spring-boot-starter-validation 라이브러리를 넣으면 자동으로 Bean Validator를
  인지하고 스프링에 통합한다.

스프링 부트는 자동으로 글로벌 Validator로 등록한다.
- LocalValidatorFactoryBean 을 글로벌 Validator로 등록한다.
- 이 Validator는 @NotNull 같은 애노테이션을 보고 검증을 수행한다.
- 이렇게 글로벌 Validator가 적용되어 있기 때문에, @Valid , @Validated 만 적용하면 된다.
- 검증 오류가 발생하면, FieldError , ObjectError 를 생성해서 BindingResult 에 담아준다.

✅참고
- 검증시 @Validated @Valid 둘다 사용가능하다.
- javax.validation.@Valid 를 사용하려면 build.gradle 의존관계 추가가 필요하다.
  - implementation 'org.springframework.boot:spring-boot-starter-validation'
- @Validated - 스프링 전용 검증 애노테이션
  - @Validated 내부에 groups 라는 기능을 포함
- @Valid - 자바 표준 검증 애노테이션

### 검증 순서

- @ModelAttribute 각각의 필드에 타입 변환 시도 
  - 성공하면 다음으로
  - 실패하면 typeMismatch ➡️ FieldError 추가
- Validator 적용

바인딩에 성공한 필드만 Bean Validation 적용
- BeanValidator는 바인딩에 실패한 필드는 BeanValidation을 적용하지 않는다.
- 생각해보면 타입 변환에 성공해서 바인딩에 성공한 필드여야 BeanValidation 적용이 의미 있다.


