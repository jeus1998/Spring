package spring.exception.resolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@Slf4j
public class MyHandlerExceptionResolver implements HandlerExceptionResolver {
    @Override
    public ModelAndView resolveException(HttpServletRequest request,
                                         HttpServletResponse response,
                                         Object handler, Exception ex) {

       try{
           log.info("check");
           if(ex instanceof IllegalArgumentException){
              log.info("IllegalArgumentException resolver to 400");

             /*
              was 까지 전파 x json 응답하기
              Map<String, String> responseJson = new HashMap<>();
              responseJson.put("message", "writer 사용하기");

              ObjectMapper objectMapper = new ObjectMapper();
              String jsonData = objectMapper.writeValueAsString(responseJson);

              response.setContentType("application/json");
              response.setCharacterEncoding("UTF-8");
              response.getWriter().write(jsonData);
              return new ModelAndView();
              */

              // return new ModelAndView("test"); // 바로 뷰 랜더링

               response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
               return new ModelAndView();
           }
       }
       catch (Exception e){
            log.error("resolver ex", e);
       }
        return null;
    }
}
