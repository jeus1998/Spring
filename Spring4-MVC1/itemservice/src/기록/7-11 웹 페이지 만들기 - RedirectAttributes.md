
# 웹 페이지 만들기 - RedirectAttributes

- 상품을 저장하고 상품 상세 화면으로 리다이렉트 한 것 까지는 좋았다. 
- 그런데 고객 입장에서 저장이 잘 된 것인지 안 된것인지 확신이 들지 않는다.
- 그래서 저장이 잘 되었으면 상품 상세 화면에 "저장되었습니다"라는 메시지를 보여달라는 요구사항이 왔다.

### RedirectAttributes addItemPRG2() 

```java
    @PostMapping("/add")
    public String addItemPRG2(Item item, RedirectAttributes redirectAttributes){
        Item savedItem = itemRepository.save(item);

        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);

        return "redirect:/basic/items/{itemId}";
    }
```

### 뷰 템플릿 메시지 추가

```html
<h2 th:if="${param.status}" th:text="|상품 등록 완료!|"></h2>
```
- th:if : 해당 조건이 참이면 실행
- ${param.status} : 타임리프에서 쿼리 파라미터를 편리하게 조회하는 기능
  - 원래는 컨트롤러에서 모델에 직접 담고 값을 꺼내야 한다.
  - 쿼리 파라미터는 자주 사용해서 타임리프에서 직접 지원한다.

### 실행
- 실행해보면 다음과 같은 리다이렉트 결과가 나온다.
- http://localhost:8080/basic/items/3?status=true
- 뷰 템플릿에 메시지를 추가하고 실행해보면 "저장 완료!" 라는 메시지가 나오는 것을 확인할 수 있다. 
- 물론 상품 목록에서 상품 상세로 이동한 경우에는 해당 메시지가 출력되지 않는다.

### RedirectAttributes
- RedirectAttributes 를 사용하면 URL 인코딩도 해주고, pathVariable , 쿼리 파라미터까지 처리해준다.
  - redirect:/basic/items/{itemId}
  - pathVariable 바인딩: {itemId}
  - 나머지는 쿼리 파라미터로 처리: ?status=true


