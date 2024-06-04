
# 검증1 Validation - 오류 코드와 메시지 처리2

### 목표
- FieldError , ObjectError 는 다루기 너무 번거롭다
- 오류 코드도 좀 더 자동화

### BindingResult

- 컨트롤러에서 BindingResult 는 검증해야 할 객체인 target 바로 다음에 온다
- 따라서 BindingResult 는 이미 본인이 검증해야 할 객체인 target 을 알고 있다.

다음을 컨트롤러에서 실행해보자
```text
log.info("objectName={}", bindingResult.getObjectName());
log.info("target={}", bindingResult.getTarget());
```

출력 결과
```text
objectName=item 
target=Item(id=null, itemName=상품, price=100, quantity=1234)
```

### ValidationItemControllerV2 - addItemV4() 추가

rejectValue() , reject()
- BindingResult 가 제공하는 rejectValue() , reject() 를 사용하면 FieldError , ObjectError 를 
  직접 생성하지 않고, 깔끔하게 검증 오류를 다룰 수 있다.

```java
/**
 * 목표: BindingResult 제공 rejectValue(), reject() 활용해서 코드 단순화 하기
 * rejectValue() -> new FiledError() / reject() -> new ObjectError()
 * rejectValue(fieldName, errorCode(errors.properties 첫 글자, Object[] args, default message)
 * reject(errorCode, Object[] args, default message)
 * errors.properties message 코드를 직접 입력 하지 않아도 동작 -> messageResolver
 */
@PostMapping("/add")
public String addItemV4(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
    
    if(!StringUtils.hasText(item.getItemName())){
        bindingResult.rejectValue("itemName", "required");
    }
    if(item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000){
        bindingResult.rejectValue("price", "range", new Object[]{1000, 1000000}, null);
    }
    if(item.getQuantity() == null || item.getQuantity() >= 9999){
        bindingResult.rejectValue("quantity", "max", new Object[]{9999}, null);
    }

    // 특정 필드가 아닌 복합 룰 검증
    if(item.getPrice() != null && item.getQuantity() != null){
        int resultPrice = item.getPrice() * item.getQuantity();
        if(resultPrice < 10000){
            bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
        }
    }

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
실행
- 오류 메시지가 정상 출력된다.
- errors.properties 에 있는 코드를 직접 입력하지 않았는데 어떻게 된 것일까?

rejectValue() - new FiledError() 
```text
void rejectValue(@Nullable String field, String errorCode,
@Nullable Object[] errorArgs, @Nullable String defaultMessage);
```
- field : 오류 필드명
- errorCode : 오류 코드
  - 이 오류 코드는 메시지에 등록된 코드가 아니다.
  - messageResolver 위한 오류 코드
- errorArgs : 오류 메시지에서 {0} 을 치환하기 위한 값
- defaultMessage : 오류 메시지를 찾을 수 없을 때 사용하는 기본 메시지

```text
bindingResult.rejectValue("price", "range", new Object[]{1000, 1000000}, null)

앞에서 BindingResult 는 어떤 객체를 대상으로 검증하는지 target을 이미 알고 있다고 했다.
따라서 target(item)에 대한 정보는 없어도 된다. 오류 필드명은 동일하게 price 를 사용했다.
```

축약된 오류 코드 
```text
FieldError() 를 직접 다룰 때는 오류 코드를 range.item.price 와 같이 모두 입력했다. 그런데
rejectValue() 를 사용하고 부터는 오류 코드를 range 로 간단하게 입력했다. 그래도 오류 메시지를 잘 찾아서 출
력한다. 무언가 규칙이 있는 것 처럼 보인다. 이 부분을 이해하려면 MessageCodesResolver 를 이해해야 한다.
```

reject() - new ObjectError()
```text
void reject(String errorCode, @Nullable Object[] errorArgs, 
@Nullable String defaultMessage);
```

