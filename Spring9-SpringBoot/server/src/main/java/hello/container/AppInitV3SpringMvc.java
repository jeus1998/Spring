package hello.container;

import hello.spring.HelloConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * 설정 없이 초기화 코드 자동 호출 - 스프링 MVC 제공 WebApplicationInitializer 활용
 * External Libraries -> Gradle: org.springframework:spring-web:6.0.4 -> META-INF/services
 * jakarta.servlet.ServletContainerInitializer
 * org.springframework.web.SpringServletContainerInitializer 서블릿 컨테이너 초기화 코드
 */
public class AppInitV3SpringMvc implements WebApplicationInitializer {
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        System.out.println("AppInitV3SpringMvc.onStartup");

        // 스프링 컨테이너 생성
        AnnotationConfigWebApplicationContext appContext = new AnnotationConfigWebApplicationContext();
        appContext.register(HelloConfig.class);

        // 스프링 MVC 디스패처 서블릿 생성, 스프링 컨테이너 연결
        DispatcherServlet dispatcherServlet = new DispatcherServlet(appContext);

        // 디스패처 서블릿을 서블릿 컨테이너에 등록
        servletContext
               .addServlet("dispatcherV3", dispatcherServlet)
               .addMapping("/"); // 모든 요청이 디스패처 서블릿을 통하도록 설정
    }
}
