package spring.exception.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.exception.exception.UserException;
import spring.exception.exhandler.ErrorResult;

@Slf4j
@RestController
public class ApiExceptionV2Controller {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResult illegalExceptionHandler(IllegalArgumentException e){
        log.error("[IllegalArgumentException handler] ex", e);
        return new ErrorResult("BAD", e.getMessage());
    }


    // @ExceptionHandler(UserException.class) // 빼도 동일하게 동작
    @ExceptionHandler
    public ResponseEntity<ErrorResult> userExceptionHandler(UserException e){
        log.error("[UserException handler] ex", e);
        ErrorResult errorResult = new ErrorResult("user-ex", e.getMessage());
        return new ResponseEntity(errorResult, HttpStatus.BAD_GATEWAY);
    }
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResult exHandler(Exception e){
        log.error("[exception handler] ex", e);
        return new ErrorResult("all ex", e.getMessage());
    }

    @GetMapping("/api2/members/{id}")
    public MemberDto getMember(@PathVariable String id){
        log.info("id={}", id);

        if(id.equals("ex")){
            throw new RuntimeException("잘못된 사용자");
        }
        if(id.equals("bad")){
            throw new IllegalArgumentException("잘못된 입력 값");
        }
        if(id.equals("user-ex")){
            throw new UserException("사용자 오류");
        }

        return new MemberDto(id, "hello" + id);
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
       private String memberId;
       private String name;
    }
}
