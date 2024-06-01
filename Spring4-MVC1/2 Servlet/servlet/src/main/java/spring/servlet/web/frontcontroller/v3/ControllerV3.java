package spring.servlet.web.frontcontroller.v3;

import spring.servlet.web.frontcontroller.ModelView;

import java.util.*;
public interface ControllerV3 {
    ModelView process(Map<String, String> paramMap);
}
