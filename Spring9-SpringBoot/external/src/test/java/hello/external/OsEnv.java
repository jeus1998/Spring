package hello.external;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * OS 환경 변수를 읽는법
 */
@Slf4j
public class OsEnv {
    public static void main(String[] args) {
        Map<String, String> envMap = System.getenv();
        for (String key : envMap.keySet()) {
            log.info("env {}={}", key, System.getenv(key));
        }
        // EX)
        // DBURL=dev.db.com 개발서버
        // DBURL=prod.db.com 개발서버
        String dbUrl = System.getenv("DBURL");
    }
}
