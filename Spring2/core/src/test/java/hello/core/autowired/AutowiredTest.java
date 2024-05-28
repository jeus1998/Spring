package hello.core.autowired;

import hello.core.member.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.lang.Nullable;

import java.util.Optional;

/**
 * Autowired option test
 * 1. required = false
 * 2. @Nullable
 * 3. Optional<T>
 */
public class AutowiredTest {
    @Test
    @DisplayName("Autowired option test")
    void autowiredOption(){
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(Bean.class);
    }
    static class Bean{
        @Autowired(required = false)
        public void setNoBean1(Member noBean){
            System.out.println("Bean.setNoBean1 " + noBean);
        }
        @Autowired
        public void setNoBean2(@Nullable Member noBean){
            System.out.println("Bean.setNoBean2 " + noBean);
        }
        @Autowired
        public void setNoBean3(Optional<Member> noBean){
            System.out.println("Bean.setNoBean3 " + noBean);
        }
    }
}
