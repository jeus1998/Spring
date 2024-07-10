package hello.embed;

import hello.spring.HelloConfig;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import java.io.IOException;
import java.nio.file.Files;

public class EmbedTomcatSpringMain {
    public static void main(String[] args) throws LifecycleException, IOException {
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

        String docBase = Files.createTempDirectory("tomcat-basedir").toString();

        // 디스패처 서블릿 등록 시작
        Context context = tomcat.addContext("", docBase);
        // 서블릿 추가
        tomcat.addServlet("", "dispatcher", dispatcherServlet);
        // 서블릿 경로 지정
        context.addServletMappingDecoded("/", "dispatcher");

        tomcat.start();
    }
}
