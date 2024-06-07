package spring.exception.exhandler.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import spring.exception.exception.UserException;
import spring.exception.exhandler.ErrorResult;

@Slf4j
@RestControllerAdvice
public class ExControllerAdvice {
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
}
