package spring.servlet.web.frontcontroller;

import lombok.Getter;
import lombok.Setter;

import java.util.*;


public class ModelView {
    private String viewName;
    private Map<String, Object> model = new HashMap<>();
    public ModelView(String viewName) {
        this.viewName = viewName;
    }
    public void setViewName(String viewName) {
        this.viewName = viewName;
    }
    public String getViewName() {
        return viewName;
    }
    public Map<String, Object> getModel() {
        return model;
    }
}
