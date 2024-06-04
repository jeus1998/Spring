
# Bean Validation - groups

동일한 모델 객체를 등록할 때와 수정할 때 각각 다르게 검증하는 방법을 알아보자.

방법 2가지
- BeanValidation의 groups 기능을 사용한다
- ItemSaveForm, ItemUpdateForm 같은 폼 전송을 위한 별도의 모델 객체를 만들어서 사용한다.

### BeanValidation groups 기능 사용

이런 문제를 해결하기 위해 Bean Validation은 groups라는 기능을 제공한다.
예를 들어서 등록시에 검증할 기능과 수정시에 검증할 기능을 각각 그룹으로 나누어 적용할 수 있다.

groups 적용 

저장용 groups 생성
```java
public interface SaveCheck {}
```

수정용 groups 생성
```java
public interface UpdateCheck {}
```

tem - groups 적용
```java
@Data
public class Item {

    @NotNull(groups = UpdateCheck.class)
    private Long id;
    
    @NotBlank(groups = {SaveCheck.class, UpdateCheck.class})
    private String itemName;

    @NotNull(groups = {SaveCheck.class, UpdateCheck.class})
    @Range(min = 1000, max = 1000000)
    private Integer price;

    @NotNull(groups = {SaveCheck.class, UpdateCheck.class})
    @Max(value = 9999, groups = {SaveCheck.class})
    private Integer quantity;
    
}
```

ValidationItemControllerV3 - 저장 로직에 SaveCheck Groups 적용
```java
/**
 * Bean Validation groups 기능 활용하기
 */
@PostMapping("/add")
public String addItem2(@Validated(SaveCheck.class) @ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

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

ValidationItemControllerV3 - 수정 로직에 UpdateCheck Groups 적용

```java
/**
* Bean Validation groups 기능 활용하기
*/
@PostMapping("/{itemId}/edit")
public String edit2(@PathVariable Long itemId, @Validated(UpdateCheck.class) @ModelAttribute Item item, BindingResult bindingResult) {

if(item.getPrice() != null && item.getQuantity() != null){
    int resultPrice = item.getPrice() * item.getQuantity();
    if(resultPrice < 10000){
        bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
    }
}
// 검증에 실패하면 다시 입력 폼으로
if(bindingResult.hasErrors()){
    log.info("errors = {}", bindingResult);
    return "validation/v3/editForm";
}

itemRepository.update(itemId, item);
return "redirect:/validation/v3/items/{itemId}";
}
```

✅참고 
- @Valid 에는 groups를 적용할 수 있는 기능이 없다. 
- groups를 사용하려면 @Validated 를 사용해야 한다.


💯정리
- groups 기능을 사용해서 등록과 수정시에 각각 다르게 검증을 할 수 있었다.
- 그런데 groups 기능을 사용하니 Item은 물론이고, 전반적으로 복잡도가 올라갔다.
- 실무에서는 주로 다음에 등장하는 등록용 폼 객체와 수정용 폼 객체를 분리해서 사용한다.



