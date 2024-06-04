
# 검증1 Validation - 검증 직접 처리(개발)

### ValidationItemControllerV1 - addItem() 수정

```java
    @PostMapping("/add")
    public String addItem(@ModelAttribute Item item, RedirectAttributes redirectAttributes, Model model) {

        // 검증 오류 결과를 보관
        Map<String, String> errors = new HashMap<>();

        // 검증 로직
        if(!StringUtils.hasText(item.getItemName())){
            errors.put("itemName", "상품 이름은 필수입니다.");
        }
        if(item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000){
            errors.put("price", "가격은 1,000 ~ 1,000,000 까지 허용합니다.");
        }
        if(item.getQuantity() == null || item.getQuantity() >= 9999){
            errors.put("quantity", "수량은 최대 9,999까지 허용합니다.");
        }

        // 특정 필드가 아닌 복합 룰 검증
        if(item.getPrice() != null && item.getQuantity() != null){
            int resultPrice = item.getPrice() * item.getQuantity();
            if(resultPrice < 10000){
                errors.putIfAbsent("globalError", "가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재 값 = " + resultPrice);
            }
        }

        // 검증에 실패하면 다시 입력 폼으로
        if(hasErrors(errors)){
            log.info("errors = {}", errors);
            model.addAttribute("errors", errors);
            return "validation/v1/addForm";
        }

        // 성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v1/items/{itemId}";
    }

    private static boolean hasErrors(Map<String, String> errors) {
        return !errors.isEmpty();
    }
```

- 검증 오류 보관
  - ```Map<String, String> errors = new HashMap<>();```
  - 만약 검증시 오류가 발생하면 어떤 검증에서 오류가 발생했는지 정보를 담아둔다.
- 검증 로직 
  - import org.springframework.util.StringUtils; 추가 필요
  - 검증시 오류가 발생하면 errors 에 담아둔다.
  - 이때 어떤 필드에서 오류가 발생했는지 구분하기 위해 오류가 발생한 필드명을 key로 사용한다.
  - 이후 뷰에서 이 데이터를 사용해서 고객에게 친절한 오류 메시지를 출력할 수 있다.
```java
if (!StringUtils.hasText(item.getItemName())) {
    errors.put("itemName", "상품 이름은 필수입니다.");
}
```  
- 특정 필드의 범위를 넘어서는 검증 로직
  - 특정 필드를 넘어서는 오류를 처리해야 할 수도 있다.
  - 이때는 필드 이름을 넣을 수 없으므로 globalError 라는 key 를 사용한다.
```java
// 특정 필드가 아닌 복합 룰 검증
if(item.getPrice() != null && item.getQuantity() != null){
    int resultPrice = item.getPrice() * item.getQuantity();
    if(resultPrice < 10000){
        errors.putIfAbsent("globalError", "가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재 값 = " + resultPrice);
    }
}
```

- 검증에 실패하면 다시 입력 폼으로
  - 만약 검증에서 오류 메시지가 하나라도 있으면 오류 메시지를 출력하기 위해 model 에 errors 를 담는다.
  - 입력 폼이 있는 뷰 템플릿으로 보낸다.

```java
 // 검증에 실패하면 다시 입력 폼으로
if(hasErrors(errors)){
    log.info("errors = {}", errors);
    model.addAttribute("errors", errors);
    return "validation/v1/addForm";
}

private static boolean hasErrors(Map<String, String> errors) {
        return !errors.isEmpty();
}
```

### addForm.html 수정

```html
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="utf-8">
    <link th:href="@{/css/bootstrap.min.css}"
          href="../css/bootstrap.min.css" rel="stylesheet">
    <style>
        .container {
            max-width: 560px;
        }
        .field-error{
            border-color: #dc3545;
            color: #dc3545;
        }
    </style>
</head>
<body>

<div class="container">

    <div class="py-5 text-center">
        <h2 th:text="#{page.addItem}">상품 등록</h2>
    </div>

    <form action="item.html" th:action th:object="${item}" method="post">
        <!--global error 출력 -->
        <div th:if="${errors?.containsKey('globalError')}">
             <p class="field-error" th:text="${errors['globalError']}">전체 오류 메시지</p>
        </div>

        <div>
            <label for="itemName" th:text="#{label.item.itemName}">상품명</label>
            <input type="text" id="itemName" th:field="*{itemName}"
                   th:class="${errors?.containsKey('itemName')} ? 'form-control field-error' : 'form-control'"
                   class="form-control" placeholder="이름을 입력하세요">
            <div class="field-error" th:if="${errors?.containsKey('itemName')}" th:text="${errors['itemName']}">
            </div>
        </div>
        <div>
            <label for="price" th:text="#{label.item.price}">가격</label>
            <input type="text" id="price" th:field="*{price}"
                   th:class="${errors?.containsKey('price')}? 'form-control field-error': 'form-control'"
                   class="form-control" placeholder="가격을 입력하세요">
            <div class="field-error" th:if="${errors?.containsKey('price')}" th:text="${errors['price']}">
            </div>
        </div>
        <div>
            <label for="quantity" th:text="#{label.item.quantity}">수량</label>
            <input type="text" id="quantity" th:field="*{quantity}"
                   th:class="${errors?.containsKey('quantity')}? 'form-control field-error': 'form-control'"
                   class="form-control" placeholder="수량을 입력하세요">
            <div class="field-error" th:if="${errors?.containsKey('quantity')}" th:text="${errors['quantity']}"></div>
        </div>

        <hr class="my-4">

        <div class="row">
            <div class="col">
                <button class="w-100 btn btn-primary btn-lg" type="submit" th:text="#{button.save}">상품 등록</button>
            </div>
            <div class="col">
                <button class="w-100 btn btn-secondary btn-lg"
                        onclick="location.href='items.html'"
                        th:onclick="|location.href='@{/validation/v1/items}'|"
                        type="button" th:text="#{page.items}">취소</button>
            </div>
        </div>

    </form>

</div> <!-- /container -->
</body>
</html>
```

- css 추가
  - 이 부분은 오류 메시지를 빨간색으로 강조하기 위해 추가했다.
```html
```css
.field-error {
 border-color: #dc3545;
 color: #dc3545;
}
```

- 글로벌 오류 메시지
  - 오류 메시지는 errors 에 내용이 있을 때만 출력하면 된다. 
  - 타임리프의 th:if 를 사용하면 조건에 만족할 때만 해당 HTML 태그를 출력할 수 있다. 
```html
<div th:if="${errors?.containsKey('globalError')}">
     <p class="field-error" th:text="${errors['globalError']}">전체 오류 메시지</p>
</div>
```

- 가격 1000원, 수량 1개를 선택하면 다음과 같은 HTML 결과 화면을 볼 수 있다.
```html
<div>
 <p class="field-error">가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재 값 = 1000</p>
</div>
```

- ✅ Safe Navigation Operator 
  - https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#expressionsoperator-safe-navigation
```text
만약 여기에서 errors 가 null 이라면 어떻게 될까?
생각해보면 등록폼에 진입한 시점에는 errors가 없다.
따라서 errors.containsKey() 를 호출하는 순간 NullPointerException 이 발생한다.

errors?. 은 errors 가 null 일때 NullPointerException 이 발생하는 대신, null 을 반환하는 문법
이다.
th:if 에서 null 은 실패로 처리되므로 오류 메시지가 출력되지 않는다.

이것은 스프링의 SpringEL이 제공하는 문법이다.
``` 

- 필드 오류 처리
  - classappend 를 사용해서 해당 필드에 오류가 있으면 field-error 라는 클래스 정보를 더해서 폼의 색깔을 빨간색으로 강조한다.
  - 만약 값이 없으면 _ (No-Operation)을 사용해서 아무것도 하지 않는다.
```html
<input type="text" th:classappend="${errors?.containsKey('itemName')} ? 'field-error' : _" class="form-control">
```


정리 
- 만약 검증 오류가 발생하면 입력 폼을 다시 보여준다.
- 검증 오류들을 고객에게 친절하게 안내해서 다시 입력할 수 있게 한다.
- 검증 오류가 발생해도 고객이 입력한 데이터가 유지된다.

남은 문제점
```text
뷰 템플릿에서 중복 처리가 많다. 뭔가 비슷하다.

타입 오류 처리가 안된다. Item 의 price , quantity 같은 숫자 필드는 타입이 Integer 이므로 문자 타입
으로 설정하는 것이 불가능하다. 숫자 타입에 문자가 들어오면 오류가 발생한다. 그런데 이러한 오류는 스프링
MVC에서 컨트롤러에 진입하기도 전에 예외가 발생하기 때문에, 컨트롤러가 호출되지도 않고, 400 예외가 발생
하면서 오류 페이지를 띄워준다.

Item 의 price 에 문자를 입력하는 것 처럼 타입 오류가 발생해도 고객이 입력한 문자를 화면에 남겨야 한다. 
만약 컨트롤러가 호출된다고 가정해도 Item 의 price 는 Integer 이므로 문자를 보관할 수가 없다. 결국 문
자는 바인딩이 불가능하므로 고객이 입력한 문자가 사라지게 되고, 고객은 본인이 어떤 내용을 입력해서 오류가
발생했는지 이해하기 어렵다.

결국 고객이 입력한 값도 어딘가에 별도로 관리가 되어야 한다.
```









