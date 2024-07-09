package hello.container;

import jakarta.servlet.ServletContainerInitializer;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.HandlesTypes;

import java.util.Set;

/**
 * @HandlesTypes(AppInit.class) 애플리케이션 초기화 인터페이스 지정
 * Set<Class<?>> c : @HandlesTypes(AppInit.class)의 구현체들을 모두 찾아서 클래스 정보로 전달
 */
@HandlesTypes(AppInit.class)
public class MyContainerInitV2 implements ServletContainerInitializer {
    @Override
    public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
        System.out.println("MyContainerInitV2.onStartup");
        System.out.println("MyContainerInitV2 c = " + c);
        System.out.println("MyContainerInitV2 ctx = " + ctx);

        for (Class<?> appInitClass : c) {
            try {
                // new AppInitV1Servlet()과 같은 코드
                AppInit appInit = (AppInit) appInitClass.getDeclaredConstructor().newInstance();
                appInit.onStartup(ctx);
            }
            catch (Exception e){
                throw new RuntimeException();
            }
        }
    }
}
