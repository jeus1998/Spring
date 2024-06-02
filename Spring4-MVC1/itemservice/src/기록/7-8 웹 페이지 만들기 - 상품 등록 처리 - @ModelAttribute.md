
# 웹 페이지 만들기 - 상품 등록 처리 - @ModelAttribute

- 이제 상품 등록 폼에서 전달된 데이터로 실제 상품을 등록 처리해보자.
- 상품 등록 폼은 다음 방식으로 서버에 데이터를 전달한다.

POST - HTML Form
- content-type: application/x-www-form-urlencoded
- 메시지 바디에 쿼리 파리미터 형식으로 전달 itemName=itemA&price=10000&quantity=10
- 예) 회원 가입, 상품 주문, HTML Form 사용

요청 파라미터 형식을 처리해야 하므로 @RequestParam 을 사용하자.

### 상품 등록 처리 - @RequestParam

```java
    @PostMapping("/add")
    public String addItemV1(@RequestParam String itemName,
                       @RequestParam Integer price,
                       @RequestParam Integer quantity,
                       Model model){
        Item item = new Item(itemName, price, quantity);
        itemRepository.save(item);

        model.addAttribute("item", item);
        return "basic/item";
    }
```

- 먼저 @RequestParam 요청 파라미터 데이터를 해당 변수에 받는다.
- Item 객체를 생성하고 itemRepository 를 통해서 저장한다
- 저장된 item 을 모델에 담아서 뷰에 전달한다.

### 상품 등록 처리 - @ModelAttribute

- @RequestParam 으로 변수를 하나하나 받아서 Item 을 생성하는 과정은 불편했다.
- 이번에는 @ModelAttribute 를 사용해서 한번에 처리해보자.

```java
    /**
     * @ModelAttribute("item")
     * model에 "item" 이름으로 들어간다.
     */
    @PostMapping("/add")
    public String addItemV2(@ModelAttribute("item")Item item, Model model){
        itemRepository.save(item);

        // model.addAttribute("item", item);

        return "basic/item";
    }
```

@ModelAttribute - 요청 파라미터 처리
- @ModelAttribute 는 Item 객체를 생성하고, 요청 파라미터의 값을 프로퍼티 접근법(setXxx)으로 입력해준다.

@ModelAttribute - Model 추가
- @ModelAttribute 는 중요한 한가지 기능이 더 있는데, 바로 모델(Model)에 @ModelAttribute 로 지정한 객체를 
  자동으로 넣어준다.
- 모델에 데이터를 담을 때는 이름이 필요하다.
- 이름은 @ModelAttribute 에 지정한 name(value) 속성을 사용한다.
- 만약 다음과 같이 @ModelAttribute 의 이름을 다르게 지정하면 다른 이름으로 모델에 포함된다
   - @ModelAttribute("hello") Item item 이름을 hello 로 지정


### addItemV3 - 상품 등록 처리 - ModelAttribute 이름 생략

```java
     @PostMapping("/add")
     public String addItemV3(@ModelAttribute Item item){
        itemRepository.save(item);

        return "basic/item";
    }
```

- @ModelAttribute 의 이름을 생략하면 모델에 저장될 때 클래스명을 사용한다
- 이때 클래스의 첫글자만 소문자로 변경해서 등록한다.
- 예) @ModelAttribute 클래스명 모델에 자동 추가되는 이름
   - Item ➡️ item
   - HelloWorld ➡️ helloWorld

### addItemV4 - 상품 등록 처리 - ModelAttribute 전체 생략

```java
    @PostMapping("/add")
    public String addItemV4(Item item){
        itemRepository.save(item);

        return "basic/item";
    }
```

@ModelAttribute 자체도 생략가능하다. 대상 객체는 모델에 자동 등록된다. 나머지 사항은 기존과 동일하다.







