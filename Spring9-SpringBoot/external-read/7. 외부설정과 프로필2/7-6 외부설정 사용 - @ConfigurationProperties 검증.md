# 외부설정 사용 - @ConfigurationProperties 검증

### 자바 빈 검증기(java bean validation)

- ``@ConfigurationProperties``를 통해서 숫자가 들어가야 하는 부분에 문자가 입력되는 문제와 같은 타입이 맞지
  않는 데이터를 입력하는 문제는 예방할 수 있다.
- 그런데 문제는 숫자의 범위라던가, 문자의 길이 같은 부분은 검증이 어렵다.
- 예를 들어서 최대 커넥션 숫자는 최소 1 최대 999 라는 범위를 가져야 한다면 어떻게 검증할 수 있을까?
- 이메일을 외부 설정에 입력했는데, 만약 이메일 형식에 맞지 않는다면 어떻게 검증할 수 있을까?
- 개발자가 직접 하나하나 검증 코드를 작성해도 되지만, 자바에는 자바 빈 검증기(java bean validation)이라는 훌륭한
  표준 검증기가 제공된다.
- ``@ConfigurationProperties``은 자바 객체이기 때문에 스프링이 자바 빈 검증기를 사용할 수 있도록 지원한다.

자바 빈 검증기를 사용
- 자바 빈 검증기를 사용하려면 ``spring-boot-starter-validation``이 필요하다.
- build.gradle 추가 
  - ``implementation 'org.springframework.boot:spring-boot-starter-validation'``

### MyDataSourcePropertiesV3 - @ConfigurationProperties , 검증기 사용 

```java
@Getter
@ConfigurationProperties("my.datasource")
@Validated
public class MyDataSourcePropertiesV3 {
    @NotEmpty
    private String url;
    @NotEmpty
    private String username;
    @NotEmpty
    private String password;
    private Etc etc;
    public MyDataSourcePropertiesV3(String url, String username, String password, Etc etc) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.etc = etc;
    }

    @Getter
    public static class Etc {
        @Min(1)
        @Max(999)
        private int maxConnection;
        @DurationMin(seconds = 1)
        @DurationMax(seconds = 60)
        private Duration timeout;
        private List<String> options;
        @ConstructorBinding
        public Etc(int maxConnection, Duration timeout, List<String> options) {
            this.maxConnection = maxConnection;
            this.timeout = timeout;
            this.options = options;
        }
    }
}
```
- ``@NotEmpty`` ``url, username, password``는 항상 값이 있어야 한다. 필수 값이 된다.
- ``@Min(1) @Max(999) maxConnection``: 최소 1 , 최대 999 의 값을 허용한다.
- ```@DurationMin(seconds = 1) @DurationMax(seconds = 60)```: 최소 1, 최대 60초를 허용한다.

### MyDataSourceConfigV3, ExternalReadApplication - 수정

MyDataSourceConfigV3
```java
@Slf4j
@EnableConfigurationProperties(MyDataSourcePropertiesV3.class)
public class MyDataSourceConfigV3 {
    private final MyDataSourcePropertiesV3 properties;
    public MyDataSourceConfigV3(MyDataSourcePropertiesV3 properties) {
        this.properties = properties;
    }
    @Bean
    public MyDataSource dataSource(){
        return new MyDataSource(
                properties.getUrl(),
                properties.getUsername(),
                properties.getPassword(),
                properties.getEtc().getMaxConnection(),
                properties.getEtc().getTimeout(),
                properties.getEtc().getOptions());
    }

}
```

ExternalReadApplication - 수정
```java
@Import(MyDataSourceConfigV3.class)
@ConfigurationPropertiesScan
@SpringBootApplication(scanBasePackages = "hello.datasource")
public class ExternalReadApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExternalReadApplication.class, args);
    }
}
```

### 테스트(실행)

값이 검증 범위를 넘어서게 설정해보자
- maxConnection=0 으로 설정한 예
```text
 Property: my.datasource.etc.maxConnection
 Value: "0"
 Origin: class path resource [application.properties] - 4:34
 Reason: 1 이상이어야 합니다
```

정상 실행 결과
```text
url=local.db.com
username=local_user
password=local_pw
maxConnection=1
timeout=PT3.5S
options=[CACHE, ADMIN]
```

### 정리

- ``ConfigurationProperties``덕분에 타입 안전하고, 또 매우 편리하게 외부 설정을 사용할 수 있다.
- 그리고 검증기 덕분에 쉽고 편리하게 설정 정보를 검증할 수 있다.
- 가장 좋은 예외는 컴파일 예외, 그리고 애플리케이션 로딩 시점에 발생하는 예외이다.
- 가장 나쁜 예외는 고객 서비스 중에 발생하는 런타임 예외이다.

ConfigurationProperties 장점
- 외부 설정을 객체로 편리하게 변환해서 사용할 수 있다.
- 외부 설정의 계층을 객체로 편리하게 표현할 수 있다
- 외부 설정을 타입 안전하게 사용할 수 있다.
- 검증기를 적용할 수 있다.
