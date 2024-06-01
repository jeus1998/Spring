package spring.servlet.web.frontcontroller.v5.adapter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import spring.servlet.web.frontcontroller.ModelView;
import spring.servlet.web.frontcontroller.v3.ControllerV3;
import spring.servlet.web.frontcontroller.v5.MyHandlerAdapter;

import java.io.IOException;
import java.util.HashMap;

public class ControllerV3HandlerAdapter implements MyHandlerAdapter {
    @Override
    public boolean supports(Object handler) {
        return (handler instanceof ControllerV3);
    }
    @Override
    public ModelView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServletException, IOException {
        ControllerV3 controller = (ControllerV3) handler;
        HashMap<String ,String> paramMap = new HashMap<>();
        createParamMap(request, paramMap);

        return controller.process(paramMap);
    }
    private static void createParamMap(HttpServletRequest request, HashMap<String, String> paramMap) {
        request.getParameterNames().asIterator()
                .forEachRemaining(paramName -> paramMap.put(paramName, request.getParameter(paramName)));
    }
}
