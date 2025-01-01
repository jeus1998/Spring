package hello.itemservice.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.*;
@RestControllerAdvice
@Slf4j
public class MyRestControllerAdvice {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> methodArgumentExceptionHandler(MethodArgumentNotValidException e){
        Map<String, String> response = new HashMap<>();
        return response;
    }
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CustomException.class)
    public Map<String, String> formatExceptionHandler(CustomException e){
        Map<String, String> response = new HashMap<>();
        log.info("e={}", e.getMessage());
        return response;
    }
}
