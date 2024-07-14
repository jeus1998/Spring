package hello.external;

import lombok.extern.slf4j.Slf4j;

import java.util.Properties;

/**
 * 외부 설정 - 자바 시스템 속성
 */
@Slf4j
public class JavaSystemProperties {
    public static void main(String[] args) {
        Properties properties = System.getProperties();
        for (Object key : properties.keySet()) {
            log.info("properties {}={}", key, System.getProperty(String.valueOf(key)));
        }

        String url = System.getProperty("url");
        String username = System.getProperty("username");
        String password = System.getProperty("password");

        log.info("url={}", url);
        log.info("username={}", username);
        log.info("password={}", password);

        // 코드 안에서 Property 설정하기
        // 코드 안에서 하기 때문에 외부로 설정을 분리하는 효과는 없다.

        System.setProperty("MyKey", "MyValue");
        String myValue = System.getProperty("MyKey");
        log.info("MyValue={}", myValue);
    }
}
