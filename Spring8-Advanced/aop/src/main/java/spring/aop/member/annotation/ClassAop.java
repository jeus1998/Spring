package spring.aop.member.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE) // 클래스 레벨에 붙이는 애노테이션
@Retention(RetentionPolicy.RUNTIME) // 애플리케이션 런타임 동안 살아 있다.
public @interface ClassAop {

}
