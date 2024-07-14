# 외부 설정 사용 - Environment

### 외부 설정 사용

- 다음과 같은 외부 설정들은 스프링이 제공하는 ``Environment``를 통해서 일관된 방식으로 조회할 수 있다.
- 외부 설정
  - 설정 데이터(application.properties)
  - OS 환경변수
  - 자바 시스템 속성
  - 커맨드 라인 옵션 인수

다양한 외부 설정 읽기
- 스프링은 ``Environment``는 물론이고 ``Environment``를 활용해서 더 편리하게 외부 설정을 읽는 방법들을 제공한다.

스프링이 지원하는 다양한 외부 설정 조회 방법
- ``Environment``
- ```@Value```- 값 주입
- ```@ConfigurationProperties```- 타입 안전한 설정 속성

예제 설명 
- 조금 복잡한 예제를 가지고 외부 설정을 읽어서 활용하는 다양한 방법들을 학습해보자.
- 예제에서는 가상의 데이터소스를 하나 만들고, 여기에 필요한 속성들을 외부 설정값으로 채운 다음 스프링 빈으로 등록

### MyDataSource

```java
@Slf4j
public class MyDataSource {
    private String url;
    private String username;
    private String password;
    private int maxConnection;
    private Duration timeout;
    private List<String> options;

    public MyDataSource(String url, String username, String password, int maxConnection, Duration timeout, List<String> options) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.maxConnection = maxConnection;
        this.timeout = timeout;
        this.options = options;
    }
    @PostConstruct
    public void init(){
        log.info("url={}",url); 
        log.info("username={}",username);
        log.info("password={}",password);
        log.info("maxConnection={}",maxConnection);
        log.info("timeout={}",timeout);
        log.info("options={}",options);
    }
}
```
- ``url , username , password``: 접속 url, 이름, 비밀번호
- ``maxConnection``: 최대 연결 수
- ``timeout``: 응답 지연시 타임아웃
- ``options``: 연결시 사용하는 기타 옵션들
- @PostConstruct 에서 확인을 위해 설정된 값을 출력한다.

### application.properties

```text
my.datasource.url=local.db.com
my.datasource.username=username
my.datasource.password=password
my.datasource.etc.max-connection=1
my.datasource.etc.timeout=3500ms
my.datasource.etc.options=CACHE,ADMIN
```
- 외부 속성은 설정 데이터(application.properties)를 사용한다.
- 여기서는 별도의 프로필은 사용하지 않았다. 환경에 따라서 다른 설정값이 필요하다면 각 환경에 맞는 프로필을 적용하면 된다.

참고 - properties 캐밥 표기법
- ``properties``는 자바의 낙타 표기법(maxConnection)이 아니라 소문자와 - (dash)를 사용하는 캐밥 표기법(max-connection)을 
  주로 사용한다. 
-  스프링은 ``properties``에 캐밥 표기법을 권장한다.

### MyDataSourceEnvConfig

```java
@Slf4j
@Configuration
@RequiredArgsConstructor
public class MyDataSourceEnvConfig {
    private final Environment env;
    @Bean
    public MyDataSource myDataSource(){
        String url = env.getProperty("my.datasource.url");
        String username = env.getProperty("my.datasource.username");
        String password = env.getProperty("my.datasource.password");
        int maxConnection = env.getProperty("my.datasource.etc.max-connection", Integer.class);
        Duration timeout = env.getProperty("my.datasource.etc.timeout", Duration.class);
        List<String> options = env.getProperty("my.datasource.etc.options", List.class);
        return new MyDataSource(url, username, password, maxConnection, timeout, options);
    }
}
```
- ``Environment``를 사용하면 외부 설정의 종류와 관계없이 코드 안에서 일관성 있게 외부 설정을 조회할 수 있다.
- ``Environment.getProperty(key, Type)``를 호출할 때 타입 정보를 주면 해당 타입으로 변환해준다.(스프링 내부 변환기가 작동한다.)
  - env.getProperty("my.datasource.etc.max-connection", Integer.class): 문자 ➡️ 숫자로 변환 
  - env.getProperty("my.datasource.etc.timeout", Duration.class): 문자 ➡️ Duration (기간) 변환
  - env.getProperty("my.datasource.etc.options", List.class): 문자 ➡️ List 변환 (A,B [A,B])
- 스프링은 다양한 타입들에 대해서 기본 변환 기능을 제공한다.
- [속성 변환기 - 스프링 공식 문서](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config.typesafe-configuration-properties.conversion)

### ExternalReadApplication - 수정

```java
@Import(MyDataSourceEnvConfig.class)
@SpringBootApplication(scanBasePackages = "hello.datasource")
public class ExternalReadApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExternalReadApplication.class, args);
    }
}
```
- 설정 정보를 빈으로 등록해서 사용하기 위해 ```@Import(MyDataSourceEnvConfig.class)```를 추가했다.
- ```@SpringBootApplication(scanBasePackages = "hello.datasource")```
  - 예제에서는 ```@Import```로 설정 정보를 계속 변경할 예정이므로, 설정 정보를 바꾸면서 사용하기 위해
    ``hello.config``의 위치를 피해서 컴포넌트 스캔 위치를 설정했다.

### 실행 결과

```text
url=local.db.com
username=local_user
password=local_pw
maxConnection=1
timeout=PT3.5S
options=[CACHE, ADMIN]
```

정리
- ``application.properties``에 필요한 외부 설정을 추가하고, ``Environment``를 통해서 해당 값들을 읽어서, 
  ``MyDataSource``를 만들었다.
- 향후 외부 설정 방식이 달라져도, 예를 들어서 설정 데이터(application.properties)를 사용하다가 
  커맨드 라인 옵션 인수나 자바 시스템 속성으로 변경해도 애플리케이션 코드를 그대로 유지할 수 있다.

단점
- 이 방식의 단점은 ``Environment``를 직접 주입받고, env.getProperty(key)를 통해서 값을 꺼내는 과정을 반복 해야 한다는 점이다.
- 스프링은 ```@Value```를 통해서 외부 설정값을 주입 받는 더욱 편리한 기능을 제공한다.

