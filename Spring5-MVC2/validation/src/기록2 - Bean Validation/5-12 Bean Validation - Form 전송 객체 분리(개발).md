
# Bean Validation - Form 전송 객체 분리(개발)

이제 Item 의 검증은 사용하지 않으므로 검증 코드를 제거해도 된다.

Item 원복
```java
@Data
public class Item {

    private Long id;
    
    private String itemName;
    
    private Integer price;
    
    private Integer quantity;

    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
```

ItemSaveForm - ITEM 저장용 폼
```java
@Data
public class ItemSaveForm {
     @NotBlank
     private String itemName;

     @NotNull
     @Range(min = 1000, max = 1000000)
     private Integer price;

     @NotNull
     @Max(value = 9999)
     private Integer quantity;
}
```

ItemUpdateForm - ITEM 수정용 폼
```java
@Data
public class ItemUpdateForm {

     @NotNull
     private Long id;

     @NotBlank
     private String itemName;

     @NotNull
     @Range(min = 1000, max = 1000000)
     private Integer price;

     //수정에서는 수량은 자유롭게 변경할 수 있다.
     private Integer quantity;
}
```

ValidationItemControllerV4
```java
/**
 * SaveForm UpdateFrom 분리 적용하기
 */
@PostMapping("/add")
public String addItem(@Validated @ModelAttribute("item") ItemSaveForm form, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

    if(form.getPrice() != null && form.getQuantity() != null){
        int resultPrice = form.getPrice() * form.getQuantity();
        if(resultPrice < 10000){
            bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
        }
    }

    // 검증에 실패하면 다시 입력 폼으로
    if(bindingResult.hasErrors()){
        log.info("errors = {}", bindingResult);
        return "validation/v4/addForm";
    }

    // 성공 로직

    Item item = new Item();
    item.setItemName(form.getItemName());
    item.setPrice(form.getPrice());
    item.setQuantity(form.getQuantity());

    Item savedItem = itemRepository.save(item);
    redirectAttributes.addAttribute("itemId", savedItem.getId());
    redirectAttributes.addAttribute("status", true);
    return "redirect:/validation/v4/items/{itemId}";
}

/**
 * SaveForm UpdateFrom 분리 적용하기
 */
@PostMapping("/{itemId}/edit")
public String edit(@PathVariable Long itemId, @Validated @ModelAttribute("item") ItemUpdateForm form, BindingResult bindingResult) {

    if(form.getPrice() != null && form.getQuantity() != null){
        int resultPrice = form.getPrice() * form.getQuantity();
        if(resultPrice < 10000){
            bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
        }
    }

    // 검증에 실패하면 다시 입력 폼으로
    if(bindingResult.hasErrors()){
        log.info("errors = {}", bindingResult);
        return "validation/v4/editForm";
    }

    // 성공 로직
    Item item = new Item();
    item.setItemName(form.getItemName());
    item.setPrice(form.getPrice());
    item.setQuantity(form.getQuantity());

    itemRepository.update(itemId, item);
    return "redirect:/validation/v4/items/{itemId}";
}
```

❗️주의
- @ModelAttribute("item") 에 item 이름을 넣어준 부분을 주의하자. 
- 이것을 넣지 않으면 ItemSaveForm 의 경우 규칙에 의해 itemSaveForm 이라는 이름으로 MVC Model에 담기게 된다.
- 이렇게 되면 뷰 템플릿에서 접근하는 th:object 이름도 함께 변경해주어야 한다.

