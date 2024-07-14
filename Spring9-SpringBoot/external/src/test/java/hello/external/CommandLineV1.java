package hello.external;

import lombok.extern.slf4j.Slf4j;

/**
 * 자바 커맨드 라인 인수 main String[] args
 */
@Slf4j
public class CommandLineV1 {
    public static void main(String[] args) {
        for (String arg : args) {
            log.info("arg {}", arg);
        }
    }
}
