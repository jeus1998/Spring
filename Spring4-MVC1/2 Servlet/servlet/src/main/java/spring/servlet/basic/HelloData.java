package spring.servlet.basic;

import lombok.Getter;
import lombok.Setter;

/**
 * JSON Data 파싱용 객체
 */
@Setter @Getter
public class HelloData {
    private String username;
    private int age;
}
