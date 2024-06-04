
# 검증1 Validation - 오류 코드와 메시지 처리1

### errors 메시지 파일 생성

- messages.properties 를 사용해도 되지만, 오류 메시지를 구분하기 쉽게 errors.properties 라는 별도의 
  파일로 관리해보자.
- 먼저 스프링 부트가 해당 메시지 파일을 인식할 수 있게 다음 설정을 추가한다.
  - application.properties
  ```spring.messages.basename=messages,errors```
- 이렇게하면 messages.properties , errors.properties 두 파일을 모두 인식한다. 
- 생략하면 messages.properties 를 기본으로 인식한다.

errors.properties 추가
- src/main/resources/errors.properties
- ✅참고: errors_en.properties 파일을 생성하면 오류 메시지도 국제화 처리를 할 수 있다.
```text
required.item.itemName=상품 이름은 필수입니다.
range.item.price=가격은 {0} ~ {1} 까지 허용합니다.
max.item.quantity=수량은 최대 {0} 까지 허용합니다.
totalPriceMin=가격 * 수량의 합은 {0}원 이상이어야 합니다. 현재 값 = {1}
```

### ValidationItemControllerV2 - addItemV3() 추가

```java
/**
 * 목표: errors.properties 활용 에러 메시지 규격화
 */
@PostMapping("/add")
public String addItemV3(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

    // 검증 로직: Binding 성공한 값들 = 비즈니스에서 검증 -> bindingFailure(false)
    if(!StringUtils.hasText(item.getItemName())){
        bindingResult.addError(new FieldError("item", "itemName", item.getItemName(), false, new String[]{"required.item.itemName"}, null,null));
    }
    if(item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000){
        bindingResult.addError(new FieldError("item", "price", item.getPrice(), false, new String[]{"range.item.price"}, new Object[]{1000, 1000000}, null));
    }
    if(item.getQuantity() == null || item.getQuantity() >= 9999){
        bindingResult.addError(new FieldError("item", "quantity", item.getQuantity(), false, new String[]{"max.item.quantity"}, new Object[]{9999}, null));
    }

    // 특정 필드가 아닌 복합 룰 검증
    // ObjectError 바인딩에 실패를 안하기 때문에 bindingFailure가 없다. 또한 실패 값 또한 넘겨주지 않는다.
    if(item.getPrice() != null && item.getQuantity() != null){
        int resultPrice = item.getPrice() * item.getQuantity();
        if(resultPrice < 10000){
            bindingResult.addError(new ObjectError("item", new String[]{"totalPriceMin"}, new Object[]{10000, resultPrice}, null));
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

```text
errors.properties: range.item.price=가격은 {0} ~ {1} 까지 허용합니다.
controller: new FieldError("item", "price", item.getPrice(), false, new String[]{"range.item.price"}, new Object[]{1000, 1000000}, null)
```

- codes : required.item.itemName 를 사용해서 메시지 코드를 지정한다
- 메시지 코드는 하나가 아니라 배열로 여러 값을 전달할 수 있는데, 순서대로 매칭해서 처음 매칭되는 메시지가 사용된다.
- arguments : Object[]{1000, 1000000} 를 사용해서 코드의 {0} , {1} 로 치환할 값을 전달한다.
- 동작순서: codes 배열 순서대로 errors.properties 에서 찾는다. 없으면 default message 만약 default message가 null 에러

실행
- 실행해보면 메시지, 국제화에서 학습한 MessageSource 를 찾아서 메시지를 조회하는 것을 확인할 수 있다.



