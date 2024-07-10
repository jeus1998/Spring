package hello.boot;

import hello.spring.HelloConfig;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class MySpringApplication {
    public static void run(Class configClass, String [] args){
        System.out.println("MySpringApplication.main args=" + List.of(args));

        // 내장 톰캣 생성, 설정
        Tomcat tomcat = new Tomcat();
        Connector connector = new Connector();
        connector.setPort(8080);
        tomcat.setConnector(connector);

        // 스프링 컨테이너 생성
        AnnotationConfigWebApplicationContext appContext = new AnnotationConfigWebApplicationContext();

        appContext.register(configClass);

        // 스프링 MVC 디스패처 서블릿 생성, 스프링 컨테이너 연결
        DispatcherServlet dispatcher = new DispatcherServlet(appContext);

        String docBase = null;
        try {
            docBase = Files.createTempDirectory("tomcat-basedir").toString();
        }
        catch (IOException e){
            System.out.println("e");
            throw new RuntimeException(e);
        }

        // 디스패처 서블릿 등록 시작
        Context context = tomcat.addContext("", docBase);
        // 서블릿 추가
        tomcat.addServlet("", "dispatcher", dispatcher);
        // 서블릿 경로 지정
        context.addServletMappingDecoded("/", "dispatcher");

        try {
            tomcat.start();
        }
        catch (LifecycleException e){
            throw new RuntimeException(e);
        }
    }
}
