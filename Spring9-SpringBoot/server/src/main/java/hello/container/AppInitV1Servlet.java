package hello.container;

import hello.servlet.HelloServlet;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletRegistration;

/**
 * AppInit 구현체 (서블릿 컨테이너 초기화를 담당하는 MyContainerInitV2 에 클래스 정보가 넘어간다.)
 * 애플리케이션 초기화 인터페이스 구현체
 * MyContainerInitV2에서 onStartup() 메서드를 실행시키면 서블릿 컨테이너에 HelloServlet(서블릿)을 등록한다.
 */
public class AppInitV1Servlet implements AppInit {
    @Override
    public void onStartup(ServletContext servletContext) {
        System.out.println("AppInitV1Servlet.onStartup");

        // 순수 서블릿 코드 등록
        ServletRegistration.Dynamic helloServlet =
                servletContext.addServlet("helloServlet", new HelloServlet());

        helloServlet.addMapping("/hello-servlet");
    }
}
