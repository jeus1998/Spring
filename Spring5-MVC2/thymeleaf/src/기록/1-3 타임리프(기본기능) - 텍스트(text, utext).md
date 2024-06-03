
# 타임리프(기본기능) - 텍스트(text, utext)

- 타임리프는 기본적으로 HTML 테그의 속성에 기능을 정의해서 동작한다.
- HTML의 콘텐츠(content)에 데이터를 출력 할 때는 다음과 같이 th:text 를 사용하면 된다.
   - ```<span th:text="${data}">```
- HTML 테그의 속성이 아니라 HTML 콘텐츠 영역안에서 직접 데이터를 출력하고 싶으면 다음과 같이 [[...]] 를 
  사용하면 된다.
   - 컨텐츠 안에서 직접 출력하기 = ```[[${data}]]```


### BasicController & text 활용 

```java
@Controller
@RequestMapping("/basic")
public class BasicController {
    @GetMapping("text-basic")
    public String textBasic(Model model){
        model.addAttribute("data", "Hello Spring!");
        return "basic/text-basic";
    }

}
```

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>

<h1>컨텐츠에 데이터 출력하기</h1>
<ul>
    <li>th:text 사용<span th:text="${data}"></span> </li>
    <li>컨텐츠 안에서 직접 출력하기 = [[${data}]] </li>
</ul>
</body>
</html>
```

### Escape

HTML 문서는 ```< , >``` 같은 특수 문자를 기반으로 정의된다.
따라서 뷰 템플릿으로 HTML 화면을 생성할 때는 출력하는
데이터에 이러한 특수 문자가 있는 것을 주의해서 사용해야 한다.

기존 데이터를 ```"Hello Spring!"``` ➡️ ```"<b>Hello Spring!</b>"```
웹 브라우저에서 실행결과를 보자.

- 웹 브라우저: ```<b>Hello Spring!</b>```
- 소스보기: ```&lt;b&gt;Hello Spring!&lt;/b&gt;```

개발자가 의도한 것은 ```<b>```가 있으면 해당 부분을 강조하는 것이 목적이었다.
그런데 ```<b>``` 테그가 그대로 나온다. 
소스보기를 하면 ```<``` 부분이 ```&lt;``` 로 변경된 것을 확인할 수 있다.

### HTML 엔티티

웹 브라우저는 ```<```를 HTML 테그의 시작으로 인식한다. 따라서 ```<```를 태그의 시작이 아니라
문자로 표현할 수 있는 방법이 필요한데, 이것을 HTML 엔티티라 한다.
그리고 이렇게 HTML에서 사용하는 특수 문자를 HTML 엔티티로 변경하는 것을 이스케이프(escape)라 한다. 
그리고 타임리프가 제공하는 ```th:text , [[...]]```는 기본적으로 이스케이프(escape)를 제공한다.

- ```<``` ➡️ ```&lt;```
- ```>``` ➡️ ```&gt;```
- 기타 수 많은 HTML 엔티티가 있다.

### Unescape

이스케이프 기능을 사용하지 않으려면 어떻게 해야할까?
- ```th:text``` ➡️ ```th:utext```
- ```[[...]]``` ➡️ ```[(...)]```


BasicController 추가

```java
    @GetMapping("text-unescaped")
    public String textUnescaped(Model model){
        model.addAttribute("data", "<b>Hello Spring!</b>");
        return "basic/text-unescaped";
    }
```

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
<h1>text vs utext</h1>
<ul>
     <li>th:text = <span th:text="${data}"></span></li>
     <li>th:utext = <span th:utext="${data}"></span></li>
</ul>
<h1><span th:inline="none">[[...]] vs [(...)]</span></h1>
<ul>
     <li><span th:inline="none">[[...]] = </span>[[${data}]]</li>
     <li><span th:inline="none">[(...)] = </span>[(${data})]</li>
</ul>
</body>
</html>
```
- ```th:inline="none"```: 타임리프는 ```[[...]]```를 해석하기 때문에, 화면에 ```[[...]]``` 글자를
  보여줄 수 없다.
- 이 테그 안에서는 타임리프가 해석하지 말라는 옵션이다.

이제 정상 수행되는 것을 확인할 수 있다.




