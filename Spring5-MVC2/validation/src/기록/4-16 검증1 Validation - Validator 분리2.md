
# 검증1 Validation - Validator 분리2

```text
스프링이 Validator 인터페이스를 별도로 제공하는 이유는 체계적으로 검증 기능을 도입하기 위해서다.
그런데 앞에서는 검증기를 직접 불러서 사용했고, 이렇게 사용해도 된다.
그런데 Validator 인터페이스를 사용해서 검증기를 만들면 스프링의 추가적인 도움을 받을 수 있다.
```

### ValidationItemControllerV2  WebDataBinder 적용 

```java
@InitBinder
public void init(WebDataBinder dataBinder){
    dataBinder.addValidators(itemValidator);
}
```
- 이렇게 WebDataBinder 에 검증기를 추가하면 해당 컨트롤러에서는 검증기를 자동으로 적용할 수 있다.
- @InitBinder ➡️ 해당 컨트롤러에만 영향을 준다. 글로벌 설정은 별도로 해야한다. 

@Validated 적용
- ValidationItemControllerV2 - addItemV6()
```java
/**
 * @InitBinder WebDataBinder(ItemValidator) 활용 검증
 * @Validated
 */
@PostMapping("/add")
public String addItemV6(@Validated @ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

    // 검증에 실패하면 다시 입력 폼으로
    if(bindingResult.hasErrors()){
        log.info("errors = {}", bindingResult);
        return "validation/v2/addForm";
    }

    // 성공 로직
    Item savedItem = itemRepository.save(item);
    redirectAttributes.addAttribute("itemId", savedItem.getId());
    redirectAttributes.addAttribute("status", true);
    return "redirect:/validation/v2/items/{itemId}";
}
```

- validator를 직접 호출하는 부분이 사라지고, 대신에 검증 대상 앞에 @Validated 가 붙었다.
- 실행: 기존과 동일하게 잘 동작하는 것을 확인할 수 있다.

동작 방식
```text
@Validated 는 검증기를 실행하라는 애노테이션이다.
이 애노테이션이 붙으면 앞서 WebDataBinder 에 등록한 검증기를 찾아서 실행한다.
그런데 여러 검증기를 등록한다면 그 중에 어떤 검증기가 실행되어야 할지 구분이 필요하다.
이때 supports() 가 사용된다. 
여기서는 supports(Item.class) 호출되고, 결과가 true 이므로 ItemValidator 의 validate() 가 호출된다
```

### 글로벌 설정 - 모든 컨트롤러에 다 적용

```java
@SpringBootApplication
public class ItemServiceApplication implements WebMvcConfigurer {
     public static void main(String[] args) {
        SpringApplication.run(ItemServiceApplication.class, args);
     }
     @Override
     public Validator getValidator() {
        return new ItemValidator();
     }
}
```
- 이렇게 글로벌 설정을 추가할 수 있다. 기존 컨트롤러의 @InitBinder 를 제거해도 글로벌 설정으로 정상 동작하는것을 확인할 수 있다.
- 글로벌 설정을 직접 사용하는 경우는 드물다.

✅ 참고
```text
검증시 @Validated @Valid 둘다 사용가능하다
@Validated - 스프링 전용 검증 애노테이션
@Valid - 자바 표준 검증 애노테이션
```