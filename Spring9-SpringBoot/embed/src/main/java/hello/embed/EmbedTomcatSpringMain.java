package hello.embed;

import hello.spring.HelloConfig;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class EmbedTomcatSpringMain {
    public static void main(String[] args) throws LifecycleException {
        // 내장 톰캣 생성, 설정
        Tomcat tomcat = new Tomcat();
        Connector connector = new Connector();
        connector.setPort(8080);
        tomcat.setConnector(connector);

        // 스프링 컨테이너 생성
        AnnotationConfigWebApplicationContext appContext = new AnnotationConfigWebApplicationContext();

        // HelloConfig 스프링 빈으로 등록
        appContext.register(HelloConfig.class);

        // 스프링 MVC 디스패처 서블릿 생성, 스프링 컨테이너 연결
        DispatcherServlet dispatcherServlet = new DispatcherServlet(appContext);
        // 디스패처 서블릿 등록 시작
        Context context = tomcat.addContext("", "/");
        // 서블릿 추가
        tomcat.addServlet("", "dispatcher", dispatcherServlet);
        // 서블릿 경로 지정
        context.addServletMappingDecoded("/", "dispatcher");

        tomcat.start();
    }
}
