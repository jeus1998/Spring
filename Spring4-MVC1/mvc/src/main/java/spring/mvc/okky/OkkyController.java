package spring.mvc.okky;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@Slf4j
@RequestMapping("/okky")
public class OkkyController {
    @GetMapping
    public String multiForm()
    {
        log.info("input");
        return "/okky/form";
    }
    @PostMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public String multiAdd(
           @RequestParam List<String> A,
           @RequestParam List<String> B,
           @RequestParam List<String> C) {

       List<DTO> list = new ArrayList<>();
       for (int i = 0; i < A.size(); i++) {
           list.add(new DTO(A.get(i), B.get(i), C.get(i)));
       }
       for (DTO dto : list) {
           System.out.println(dto);
       }

       log.info("result={}", list);
       return "ok";
   }

   @ToString
   @AllArgsConstructor
   static class DTO {
       private String A;
       private String B;
       private String C;
   }
}