package spring.aop.member.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Target(ElementType.METHOD) : 메소드에만 적용될 수 있음을 지정
 * @Retention(RetentionPolicy.RUNTIME): 프로그램이 실행되는 동안 애노테이션 정보를 유지 -> 런타임에 리플렉션을 사용가능
 * 해당 애노테이션은 String 값 value를 가진다.
 */
@Target(ElementType.METHOD) // 메소드 레벨에 붙이는 애노테이션
@Retention(RetentionPolicy.RUNTIME) // 애플리케이션 런타임 동안 살아 있다.
public @interface MethodAop {
    String value();
}
