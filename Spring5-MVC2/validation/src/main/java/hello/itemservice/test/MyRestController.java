package hello.itemservice.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;

@Slf4j
@RestController
public class MyRestController {
    @GetMapping("/api/test")
    public String test(@Valid @RequestBody TestDto testDto){
        log.info("test method entry");
        return "success";
    }
}
