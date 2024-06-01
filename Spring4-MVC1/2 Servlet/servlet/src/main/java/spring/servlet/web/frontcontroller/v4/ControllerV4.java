package spring.servlet.web.frontcontroller.v4;

import java.util.*;
public interface ControllerV4 {
    String process(Map<String , String> paramMap, Map<String, Object> model );
}
