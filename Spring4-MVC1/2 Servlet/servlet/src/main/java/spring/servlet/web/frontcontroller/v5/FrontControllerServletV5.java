package spring.servlet.web.frontcontroller.v5;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import spring.servlet.web.frontcontroller.ModelView;
import spring.servlet.web.frontcontroller.MyView;
import spring.servlet.web.frontcontroller.v3.controller.MemberFormControllerV3;
import spring.servlet.web.frontcontroller.v3.controller.MemberListControllerV3;
import spring.servlet.web.frontcontroller.v3.controller.MemberSaveControllerV3;
import spring.servlet.web.frontcontroller.v5.adapter.ControllerV3HandlerAdapter;

import java.io.IOException;
import java.util.*;

@WebServlet(name = "frontControllerServletV5", urlPatterns = "/front-controller/v5/*")
public class FrontControllerServletV5 extends HttpServlet {
    private final Map<String , Object> handlerMappingMap = new HashMap<>();
    private final List<MyHandlerAdapter> handlerAdapters = new ArrayList<>();

    public FrontControllerServletV5() {
        initHandlerMappingMap();
        initHandlerAdapters();
    }
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Object handler = getHandler(request);
        if(handler == null){
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        MyHandlerAdapter adapter =  getHandlerAdapter(handler);

        ModelView mv = adapter.handle(request, response, handler);
        MyView myView = new MyView(viewResolver(mv));
        myView.render(mv.getModel(), request, response);
    }

    private static String viewResolver(ModelView mv) {
        return "/WEB-INF/views/" + mv.getViewName() + ".jsp";
    }

    private MyHandlerAdapter getHandlerAdapter(Object handler) {
        for (MyHandlerAdapter handlerAdapter : handlerAdapters) {
              if(handlerAdapter.supports(handler)){
                  return handlerAdapter;
              }
        }
        throw new IllegalArgumentException("handler adapter not found. handler=" + handler);
    }

    private Object getHandler(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        return handlerMappingMap.get(requestURI);
    }

    private void initHandlerAdapters() {
        handlerAdapters.add(new ControllerV3HandlerAdapter());
    }

    private void initHandlerMappingMap() {
        handlerMappingMap.put("/front-controller/v5/v3/members/new-form", new MemberFormControllerV3());
        handlerMappingMap.put("/front-controller/v5/v3/members/save" , new MemberSaveControllerV3());
        handlerMappingMap.put("/front-controller/v5/v3/members", new MemberListControllerV3());
    }
}
