
# 검증1 Validation - Validator 분리1

- 컨트롤러에서 검증 로직이 차지하는 부분은 매우 크다.
- 이런 경우 별도의 클래스로 역할을 분리하는 것이 좋다.
- 그리고 이렇게 분리한 검증 로직을 재사용 할 수도 있다.
- 복잡한 검증 로직을 별도로 분리하자

### ItemValidator 

스프링은 검증을 체계적으로 제공하기 위해 다음 인터페이스를 제공한다.
```java
public interface Validator {
    boolean supports(Class<?> clazz);
    void validate(Object target, Errors errors);
}
```

- supports() {} : 해당 검증기를 지원하는 여부 확인(뒤에서 설명)
- validate(Object target, Errors errors) : 검증 대상 객체와 BindingResult


```java
@Component
public class ItemValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return Item.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Item item = (Item) target;

        if(!StringUtils.hasText(item.getItemName())){
            errors.rejectValue("itemName", "required");
        }

        if(item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000){
            errors.rejectValue("price", "range", new Object[]{1000, 1000000}, null);
        }
        if(item.getQuantity() == null || item.getQuantity() >= 9999){
            errors.rejectValue("quantity", "max", new Object[]{9999}, null);
        }

        // 특정 필드가 아닌 복합 룰 검증
        if(item.getPrice() != null && item.getQuantity() != null){
            int resultPrice = item.getPrice() * item.getQuantity();
            if(resultPrice < 10000){
                errors.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }
    }
}
```

### ValidationItemControllerV2 - addItemV5() - ItemValidator 직접 호출하기

```java
@PostMapping("/add")
public String addItemV5(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

    itemValidator.validate(item, bindingResult);

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
- ItemValidator 를 스프링 빈으로 주입 받아서 직접 호출했다. 
- 실행해보면 기존과 완전히 동일하게 동작하는 것을 확인할 수 있다. 검증과 관련된 부분이 깔끔하게 분리되었다.


