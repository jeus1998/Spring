package hello.hello_spring.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HelloController {

    @GetMapping("hello")
    public String hello(Model model){
        model.addAttribute("data", "hello");
        return "hello";
    }

    @GetMapping("hello-mvc")
    public String helloMvc(@RequestParam("name") String name, Model model){
        model.addAttribute("name", name);
        return "hello-template";
    }

    /**
     * @RespnoseBody 응답 메시지 body에 데이터를 넣는다.
     * @param name
     * @return "hello "+ name
     */
    @GetMapping("hello-string")
    @ResponseBody
    public String helloString(@RequestParam("name")String name){
        return "hello " + name;
    }

    /**
     * json type 으로 객체를 응답 -> api 방식
     * @return
     */

    @GetMapping("hello-api")
    @ResponseBody
    public Hello helloApi (@RequestParam("name")String name){
        Hello hello = new Hello();
        hello.setName(name);
        return hello;
    }

    /**
     * 자바 빈 표준 방식 private & getter & setter
     * 프로퍼티 접근 방식
     */
    static class Hello{
        private String name;
        public String getName() { return name; }
        public void setName(String name){ this.name = name; }
    }

}
