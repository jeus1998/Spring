
# Bean Validation - 오브젝트 오류

- Bean Validation에서 특정 필드( FieldError )가 아닌 해당 오브젝트 관련 오류( ObjectError )는 어떻게 
  처리할 수 있을까?
- 다음과 같이 @ScriptAssert() 를 사용하면 된다.

@ScriptAssert()
```java
@ScriptAssert(lang = "javascript", script = "_this.price * _this.quantity >= 10000")
public class Item {
    //...
}
```

실행해보면 정상 수행되는 것을 확인할 수 있다. 메시지 코드도 다음과 같이 생성된다.

메시지 코드
- ScriptAssert.item
- ScriptAssert
- default message 추가
  - @ScriptAssert(lang = "javascript", script = "_this.price * _this.quantity >= 10000", message = "df")

@ScriptAssert() 실무 한계
- 실제 사용해보면 제약이 많고 복잡하다. 
- 실무에서는 검증 기능이 해당 객체의 범위를 넘어서는 경우들도 종종 등장하는데, 그런 경우 대응이 어렵다
- 오브젝트 오류 관련 부분만 직접 자바 코드로 작성하는 것을 권장한다.

### ValidationItemControllerV3 - 글로벌 오류 추가

```java
@PostMapping("/add")
public String addItem(@Validated @ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
    
    // 글로벌 오류 추가 
    if(item.getPrice() != null && item.getQuantity() != null){
        int resultPrice = item.getPrice() * item.getQuantity();
        if(resultPrice < 10000){
            bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
        }
    }

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
