
# 빈 생명주기 콜백 - @PostConstruct & @PreDestroy

### NetworkClient @PostConstruct @PreDestroy 적용

```java
public class NetworkClient {
    private String url;
    public NetworkClient() {
        System.out.println("생성자 호출, url = " + url);
    }
    public void setUrl(String url){
        this.url = url;
    }
    // 서비스 시작시 호출
    public void connect(){
        System.out.println("connect: " + url);
    }
    public void call(String message){
        System.out.println("call: = " + url + " message = " + message);
    }
    // 서비스 종료시 호출
    public void disconnect(){
        System.out.println("close: " + url);
    }

    @PostConstruct
    public void init() throws Exception {
        System.out.println("NetworkClient.init");
        connect();
        call("초기화 연결 메시지");
    }

    @PreDestroy
    public void close() throws Exception {
        System.out.println("NetworkClient.close");
        disconnect();
    }
}
```

```java
public class BeanLifeCycleTest {
    /**
     * ApplicationContext -> ConfigurableApplicationContext -> AnnotationConfigApplicationContext
     */
    @Test
    public void lifeCycleTest(){
        ConfigurableApplicationContext ac = new AnnotationConfigApplicationContext(LifeCycleConfig.class);
        NetworkClient client = ac.getBean(NetworkClient.class);
        ac.close();
    }
    @Configuration
    static class LifeCycleConfig{
        @Bean
        public NetworkClient networkClient(){
            NetworkClient networkClient = new NetworkClient();
            networkClient.setUrl("http://hello-spring.dev");
            return networkClient;
        }
    }
}
```

```text
생성자 호출, url = null
NetworkClient.init
connect: http://hello-spring.dev
call: = http://hello-spring.dev message = 초기화 연결 메시지
NetworkClient.close
close: http://hello-spring.dev
```
- @PostConstruct , @PreDestroy 이 두 애노테이션을 사용하면 가장 편리하게 초기화와 종료를 실행할 수 있다.

### @PostConstruct, @PreDestroy 애노테이션 특징

- 최신 스프링에서 가장 권장하는 방법이다.
- 애노테이션 하나만 붙이면 되므로 매우 편리하다.
- 패키지를 잘 보면 javax.annotation.PostConstruct 이다. 스프링에 종속적인 기술이 아니라 JSR-250
  라는 자바 표준이다. 따라서 스프링이 아닌 다른 컨테이너에서도 동작한다.
- 컴포넌트 스캔과 잘 어울린다.
- 유일한 단점은 외부 라이브러리에는 적용하지 못한다는 것이다. 외부 라이브러리를 초기화, 종료 해야 하면
  @Bean의 기능을 사용하자.

### 💯 정리

- @PostConstruct, @PreDestroy 애노테이션을 사용하자
- 코드를 고칠 수 없는 외부 라이브러리를 초기화, 종료해야 하면 @Bean의 initMethod , destroyMethod를
  사용하자.