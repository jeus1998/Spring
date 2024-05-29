package hello.core.scan.filter;
import java.lang.annotation.*;

/**
 * MyIncludeComponent <- Component 스캔 대상으로 인식하기 위한 Custom 애노테이션
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyIncludeComponent {
}

