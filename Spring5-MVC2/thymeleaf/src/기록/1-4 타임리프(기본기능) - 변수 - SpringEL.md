
# 타임리프(기본기능) - 변수 - SpringEL

타임리프(기본기능) - 변수 - SpringEL

변수 표현식: ```${...}```

그리고 이 변수 표현식에는 스프링 EL이라는 스프링이 제공하는 표현식을 사용할 수 있다.

### BasicController 추가

```java
@Controller
@RequestMapping("/basic")
public class BasicController {
    
    // ,,, 생략
    
    @GetMapping("/variable")
    public String variable(Model model){
        User userA = new User("userA", 10);
        User userB = new User("userB", 20);

        List<User> list = new ArrayList<>();
        list.add(userA);
        list.add(userB);

        Map<String, User> map = new HashMap<>();
        map.put("userA", userA);
        map.put("userB", userB);

        model.addAttribute("user", userA);
        model.addAttribute("users", list);
        model.addAttribute("userMap", map);

        return "basic/variable";
    }
    @Data
    static class User{
        private String username;
        private int age;

        public User(String username, int age){
            this.username = username;
            this.age = age;
        }
    }
}
```

### SpringEL 다양한 표현식 사용

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
 <meta charset="UTF-8">
 <title>Title</title>
</head>
<body>
<h1>SpringEL 표현식</h1>
<ul>Object
 <li>${user.username} = <span th:text="${user.username}"></span></li>
 <li>${user['username']} = <span th:text="${user['username']}"></span></li>
 <li>${user.getUsername()} = <span th:text="${user.getUsername()}"></span></li>
</ul>
<ul>List
 <li>${users[0].username} = <span th:text="${users[0].username}"></span></li>
 <li>${users[0]['username']} = <span th:text="${users[0]['username']}"></span></li>
 <li>${users[0].getUsername()} = <span th:text="${users[0].getUsername()}"></span></li>
</ul>
<ul>Map
 <li>${userMap['userA'].username} = <span th:text="${userMap['userA'].username}"></span></li>
 <li>${userMap['userA']['username']} = <span th:text="${userMap['userA']['username']}"></span></li>
 <li>${userMap['userA'].getUsername()} = <span th:text="${userMap['userA'].getUsername()}"></span></li>
</ul>
</body>
</html>
```

### 지역 변수 선언 

```th:with``` 를 사용하면 지역 변수를 선언해서 사용할 수 있다. 지역 변수는 선언한 테그 안에서만 사용할 수 있다.

```html
<h2>지역 변수 - th:with </h2>
<div th:with="first=${user[0]}">
    <p>처음 사람의 나이는 <span th:text="${first.age}"></span></p>
</div>
```


