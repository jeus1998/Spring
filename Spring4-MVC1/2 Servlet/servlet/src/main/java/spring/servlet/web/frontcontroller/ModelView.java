package spring.servlet.web.frontcontroller;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Setter @Getter
public class ModelView {
    private String viewName;
    private Map<String, Object> model = new HashMap<>();
    public ModelView(String viewName) {
        this.viewName = viewName;
    }
}
