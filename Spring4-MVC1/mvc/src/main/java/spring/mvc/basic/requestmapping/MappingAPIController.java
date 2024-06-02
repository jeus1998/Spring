package spring.mvc.basic.requestmapping;

import org.springframework.web.bind.annotation.*;

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
