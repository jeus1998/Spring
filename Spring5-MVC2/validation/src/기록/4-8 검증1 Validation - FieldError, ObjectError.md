
# 검증1 Validation - FieldError, ObjectError

### 목표

- 사용자 입력 오류 메시지가 화면에 남도록 하자.
  - 예) 가격을 1000원 미만으로 설정시 입력한 값이 남아있어야 한다.
- FieldError , ObjectError 에 대해서 더 자세히 알아보자.

### ValidationItemControllerV2 - addItemV2

```java
@PostMapping("/add")
public String addItemV2(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

    // 검증 로직: Binding 성공한 값들 = 비즈니스에서 검증 -> bindingFailure(false)
    if(!StringUtils.hasText(item.getItemName())){
        bindingResult.addError(new FieldError("item", "itemName", item.getItemName(), false, null, null,"상품 이름은 필수입니다."));
    }
    if(item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000){
        bindingResult.addError(new FieldError("item", "price", item.getPrice(), false, null, null, "가격은 1,000 ~ 1,000,000 까지 허용합니다."));
    }
    if(item.getQuantity() == null || item.getQuantity() >= 9999){
        bindingResult.addError(new FieldError("item", "quantity", item.getQuantity(), false, null, null, "수량은 최대 9,999까지 허용합니다."));
    }

    // 특정 필드가 아닌 복합 룰 검증
    // ObjectError 바인딩에 실패를 안하기 때문에 bindingFailure가 없다. 또한 실패 값 또한 넘겨주지 않는다.
    if(item.getPrice() != null && item.getQuantity() != null){
        int resultPrice = item.getPrice() * item.getQuantity();
        if(resultPrice < 10000){
            bindingResult.addError(new ObjectError("item", null, null, "가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재 값 = " + resultPrice));
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

FieldError 생성자
- FieldError 는 두 가지 생성자를 제공한다.
```java
public FieldError(String objectName, String field, String defaultMessage);
public FieldError(String objectName, String field, @Nullable Object rejectedValue, 
        boolean bindingFailure, @Nullable String[] codes, @Nullable Object[] arguments, 
        @Nullable String defaultMessage)
```

파라미터 목록
- objectName : 오류가 발생한 객체 이름
- field : 오류 필드
- rejectedValue : 사용자가 입력한 값(거절된 값)
- bindingFailure : 타입 오류 같은 바인딩 실패인지, 검증 실패인지 구분 값
- codes : 메시지 코드
- arguments : 메시지에서 사용하는 인자
- defaultMessage : 기본 오류 메시지

ObjectError 생성자
- ObjectError 는 두 가지 생성자를 제공한다.
```java
public ObjectError(String objectName, String defaultMessage) 

public ObjectError( String objectName, @Nullable String[] codes, 
@Nullable Object[] arguments, @Nullable String defaultMessage)
```

```text
사용자의 입력 데이터가 컨트롤러의 @ModelAttribute 에 바인딩되는 시점에 오류가 발생하면 모델 객체에 사용자
입력 값을 유지하기 어렵다. 예를 들어서 가격에 숫자가 아닌 문자가 입력된다면 가격은 Integer 타입이므로 문자를
보관할 수 있는 방법이 없다. 그래서 오류가 발생한 경우 사용자 입력 값을 보관하는 별도의 방법이 필요하다. 그리고 이
렇게 보관한 사용자 입력 값을 검증 오류 발생시 화면에 다시 출력하면 된다.
FieldError 는 오류 발생시 사용자 입력 값을 저장하는 기능을 제공한다.

여기서 rejectedValue 가 바로 오류 발생시 사용자 입력 값을 저장하는 필드다.
bindingFailure 는 타입 오류 같은 바인딩이 실패했는지 여부를 적어주면 된다. 여기서는 바인딩이 실패한 것은 아
니기 때문에 false 를 사용한다.
```

타임리프의 사용자 입력 값 유지
- ```th:field="*{price}"```
- 타임리프의 th:field 는 매우 똑똑하게 동작하는데, 정상 상황에는 모델 객체의 값을 사용하지만, 오류가 발생하면
  FieldError 에서 보관한 값을 사용해서 값을 출력한다.

스프링의 바인딩 오류 처리
- 타입 오류로 바인딩에 실패하면 스프링은 FieldError 를 생성하면서 사용자가 입력한 값을 넣어둔다.
- 그리고 해당 오류를 BindingResult 에 담아서 컨트롤러를 호출한다.
- 따라서 타입 오류 같은 바인딩 실패시에도 사용자의 오류 메시지를 정상 출력할 수 있다.




