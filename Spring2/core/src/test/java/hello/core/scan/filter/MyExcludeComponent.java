package hello.core.scan.filter;

import java.lang.annotation.*;

/**
 * MyExcludeComponent <- Component 스캔 대상에서 제외하기 위한 Custom 애노테이션
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyExcludeComponent {
}
