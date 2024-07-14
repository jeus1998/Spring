# 외부 설정 사용 - @Value

- ```@Value```를 사용하면 외부 설정값을 편리하게 주입받을 수 있다.
- 참고로 ```@Value```도 내부에서는 ``Environment``를 사용한다.

### MyDataSourceValueConfig

```java
@Slf4j
@Configuration
public class MyDataSourceValueConfig {

    @Value("${my.datasource.url}")
    private String url;
    @Value("${my.datasource.username}")
    private String username;
    @Value("${my.datasource.password}")
    private String password;
    @Value("${my.datasource.etc.max-connection:2}") // default 값 적용 
    private int maxConnection;
    @Value("${my.datasource.etc.timeout}")
    private Duration timeout;
    @Value("${my.datasource.etc.options}")
    private List<String> options;

    @Bean
    public MyDataSource myDataSource1(){
        return new MyDataSource(url, username, password, maxConnection, timeout, options);
    }
    @Bean
    public MyDataSource myDataSource2(
            @Value("${my.datasource.url}") String url,
            @Value("${my.datasource.username}") String username,
            @Value("${my.datasource.password}") String password,
            @Value("${my.datasource.etc.max-connection:2}") int maxConnection, // default 값 적용 
            @Value("${my.datasource.etc.timeout}") Duration timeout,
            @Value("${my.datasource.etc.options}") List<String> options) {
           return new MyDataSource(url, username, password, maxConnection, timeout, options);
    }

}
```
- ```@Value```에 ```${}```를 사용해서 외부 설정의 키 값을 주면 원하는 값을 주입 받을 수 있다.
- ```@Value```는 필드에 사용할 수도 있고, 파라미터에 사용할 수도 있다.

기본값 
- 만약 키를 찾지 못할 경우 코드에서 기본값을 사용하려면 다음과 같이 ```:```뒤에 기본값을 적어주면 된다
- ``@Value("${my.datasource.etc.max-connection:2}") int maxConnection``
- ``key``가 없는 경우 2 을 사용한다.

### ExternalReadApplication - 수정, 실행 

```java
@Import(MyDataSourceValueConfig.class)
@SpringBootApplication(scanBasePackages = "hello.datasource")
public class ExternalReadApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExternalReadApplication.class, args);
    }
}
```

실행 결과
```text
url=local.db.com
username=local_user
password=local_pw
maxConnection=2
timeout=PT3.5S
options=[CACHE, ADMIN]
```
- 스프링 빈을 2개 등록해서 같은 실행 결과가 두 번 나온다.

### 정리

- ``application.properties``에 필요한 외부 설정을 추가하고, ``@Value``를 통해서 해당 값들을 읽어서, 
  ``MyDataSource``를 만들었다.

단점 
- ```@Value```를 사용하는 방식도 좋지만, ```@Value```로 하나하나 외부 설정 정보의 키 값을 입력받고, 주입 받아와야 하는 부분이 번거롭다. 
- 그리고 설정 데이터를 보면 하나하나 분리되어 있는 것이 아니라 정보의 묶음으로 되어 있다
- 여기서는 ``my.datasource``부분으로 묶여있다. 
- 이런 부분을 객체로 변환해서 사용할 수 있다면 더 편리하고 더 좋을 것이다.

