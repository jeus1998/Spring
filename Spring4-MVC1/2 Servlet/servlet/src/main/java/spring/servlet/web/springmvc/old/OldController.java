package spring.servlet.web.springmvc.old;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

/**
 * url 자체가 스프링 빈 이름
 * dispatcherservlet은 핸들러 매핑을 스프링 빈 이름으로 핸들러를 찾고 해당 핸드러를 실행할 수 있는 어뎁터를 조회한다.
 * 요청은 /springmvc/old-controller
 */
@Component("/springmvc/old-controller")
public class OldController implements Controller {
    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        System.out.println("OldController.handleRequest");
        return new ModelAndView("new-form");
    }
}
