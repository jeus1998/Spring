# 외부 설정 사용 - @ConfigurationProperties 시작

### Type-safe Configuration Properties

- 스프링은 외부 설정의 묶음 정보를 객체로 변환하는 기능을 제공한다.
- 이것을 타입 안전한 설정 속성이라 한다.
- 객체를 사용하면 타입을 사용할 수 있다
- 따라서 실수로 잘못된 타입이 들어오는 문제도 방지할 수 있고, 객체를 통해서 활용할 수 있는 부분들이 많아진다. 
- 쉽게 이야기해서 외부 설정을 자바 코드로 관리할 수 있는 것이다.
- 그리고 설정 정보 그 자체도 타입을 가지게 된다.

### MyDataSourcePropertiesV1

```java
@Data
@ConfigurationProperties("my.datasource")
public class MyDataSourcePropertiesV1 {
    private String url;
    private String username;
    private String password;
    private Etc etc;

    @Data
    public static class Etc {
        private int maxConnection;
        private Duration timeout;
        private List<String> options;
    }
}
```
- 외부 설정을 주입 받을 객체를 생성한다. 그리고 각 필드를 외부 설정의 키 값에 맞추어 준비한다.
- ```@ConfigurationProperties```이 있으면 외부 설정을 주입 받는 객체라는 뜻이다.
- 여기에 외부 설정 KEY의 묶음 시작점인 ``my.datasource``를 적어준다.
- 기본 주입 방식은 자바빈 프로퍼티 방식이다. ``Getter , Setter``가 필요하다. 

### MyDataSourceConfigV1

```java
@Slf4j
@EnableConfigurationProperties(MyDataSourcePropertiesV1.class)
public class MyDataSourceConfigV1 {
    private final MyDataSourcePropertiesV1 properties;
    public MyDataSourceConfigV1(MyDataSourcePropertiesV1 properties) {
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
- ``@EnableConfigurationProperties(MyDataSourcePropertiesV1.class)``
  - 스프링에게 사용할 ```@ConfigurationProperties```를 지정해주어야 한다.
  - 이렇게 하면 해당 클래스는 스프링 빈으로 등록되고, 필요한 곳에서 주입 받아서 사용할 수 있다.
- ``private final MyDataSourcePropertiesV1 properties`` 설정 속성을 생성자를 통해 주입 받아서 사용한다.

### ExternalReadApplication - 수정

```java
@Import(MyDataSourceConfigV1.class)
@SpringBootApplication(scanBasePackages = "hello.datasource")
public class ExternalReadApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExternalReadApplication.class, args);
    }
}
```

### 실행 

실행 결과
```text
url=local.db.com
username=local_user
password=local_pw
maxConnection=1
timeout=PT3.5S
options=[CACHE, ADMIN]
```

타입 안전
- ``ConfigurationProperties``를 사용하면 타입 안전한 설정 속성을 사용할 수 있다.
- ``maxConnection=abc``로 입력하고 실행해보자.

실행 결과
```text
Failed to bind properties under 'my.datasource.etc.max-connection' to int:
Property: my.datasource.etc.max-connection
Value: "abc"
Origin: class path resource [application.properties] - 4:34
Reason: failed to convert java.lang.String to int ...
```
- 실행 결과를 보면 숫자가 들어와야 하는데 문자가 들어와서 오류가 발생한 것을 확인할 수 있다.
- 타입이 다르면 오류가 발생하는 것이다.
- 실수로 숫자를 입력하는 곳에 문자를 입력하는 문제를 방지해준다.
- 그래서 타입 안전한 설정 속성이라고 한다.
- ``ConfigurationProperties``로 만든 외부 데이터는 타입에 대해서 믿고 사용할 수 있다.
- ```@Value``` 또한 타입 안전한 설정 

정리
- ``application.properties``에 필요한 외부 설정을 추가
- ```@ConfigurationProperties```를 통해서 ``MyDataSourcePropertiesV1``에 외부 설정의 값들을 설정
- 그리고 해당 값들을 읽어서 ``MyDataSource``를 만들었다. 

표기법 변환
- ``maxConnection``은 표기법이 서로 다르다. 
- 스프링은 캐밥 표기법을 자바 낙타 표기법으로 중간에서 자동으로 변환해준다.
  - ``application.properties``에서는 ``max-connection``
  - 자바 코드에서는 ``maxConnection``
  - ``application.properties``에서도 ``maxConnection``이렇게 해도 딱히 문제는 없지만 관례이다.

### @ConfigurationPropertiesScan

- ```@ConfigurationProperties```를 하나하나 직접 등록할 때는 ```@EnableConfigurationProperties```를 사용한다.
  - ``@EnableConfigurationProperties(MyDataSourcePropertiesV1.class)``
- ```@ConfigurationProperties```를 특정 범위로 자동 등록할 때는 ```@ConfigurationPropertiesScan```을 사용하면 된다.

@ConfigurationPropertiesScan 예시
```java
@Import(MyDataSourceConfigV1.class)
@ConfigurationPropertiesScan
@SpringBootApplication(scanBasePackages = "hello.datasource")
public class ExternalReadApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExternalReadApplication.class, args);
    }
}
```
- 빈을 직접 등록하는 것과 컴포넌트 스캔을 사용하는 차이와 비슷하다

### 문제 

- ``MyDataSourcePropertiesV1``은 스프링 빈으로 등록된다. 
- 그런데 ``Setter``를 가지고 있기 때문에 누군가 실수로 값을 변경하는 문제가 발생할 수 있다.
- 여기에 있는 값들은 외부 설정값을 사용해서 초기에만 설정되고, 이후에는 변경하면 안된다. 
- 이럴 때 ``Setter``를 제거하고 대신에 생성자를 사용하면 중간에 데이터를 변경하는 실수를 근본적으로 방지할 수 있다.
- 이런 문제가 없을 것 같지만, 한번 발생하면 정말 잡기 어려운 버그가 만들어진다.

