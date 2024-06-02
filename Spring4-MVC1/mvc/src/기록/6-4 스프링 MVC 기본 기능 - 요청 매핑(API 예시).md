
# 스프링 MVC 기본 기능 - 요청 매핑(API 예시)

회원 관리를 HTTP API로 만든다 생각하고 매핑을 어떻게 하는지 알아보자.
(실제 데이터가 넘어가는 부분은 생략하고 URL 매핑만)

### MappingAPIController

```java
/**
 * 회원 관리 API
 * 회원 목록 조회: GET /users
 * 회원 등록: POST /users
 * 회원 조회: GET /users/{userId}
 * 회원 수정: PATCH /users/{userId}
 * 회원 삭제: DELETE /users/{userId}
 */
@RestController
@RequestMapping("/users")
public class MappingAPIController {
    @GetMapping
    public String users(){
        return "회원 목록 조회";
    }
    @PostMapping
    public String userSave(){
        return "회원 등록";
    }
    @GetMapping("/{userId}")
    public String user(@PathVariable int userId){
        return "회원 조회";
    }
    @PatchMapping("/{userId")
    public String userUpdate(@PathVariable int userId){
        return "회원 수정";
    }
    @DeleteMapping("/{userId")
    public String userDelete(@PathVariable int userId){
        return "회원 삭제";
    }
}
```

