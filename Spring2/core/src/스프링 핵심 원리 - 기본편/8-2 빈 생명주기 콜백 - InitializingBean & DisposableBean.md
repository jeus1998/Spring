
# 빈 생명주기 콜백  - 인터페이스 InitializingBean, DisposableBean

```java
public class NetworkClient implements InitializingBean, DisposableBean {

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

    // 인터페이스 InitializingBean method
    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("NetworkClient.afterPropertiesSet");
        connect();
        call("초기화 연결 메시지");
    }
    // 인터페이스 DisposableBean method
    @Override
    public void destroy() throws Exception {
        System.out.println("NetworkClient.destroy");
        disconnect();
    }
}
```

- InitializingBean 은 afterPropertiesSet() 메서드로 초기화를 지원한다.
- DisposableBean 은 destroy() 메서드로 소멸을 지원한다.

- test/java/hello/core/lifecycle/BeanLifeCycleTest.java 실행

```text
생성자 호출, url = null
NetworkClient.afterPropertiesSet
connect: http://hello-spring.dev
call: = http://hello-spring.dev message = 초기화 연결 메시지
NetworkClient.destroy
close: http://hello-spring.dev
```

- 출력 결과를 보면 초기화 메서드가 주입 완료 후에 적절하게 호출 된 것을 확인할 수 있다.
- 그리고 스프링 컨테이너의 종료가 호출되자 소멸 메서드가 호출 된 것도 확인할 수 있다.

### 초기화, 소멸 인터페이스 단점

- 이 인터페이스는 스프링 전용 인터페이스다. 해당 코드가 스프링 전용 인터페이스에 의존한다.
- 초기화, 소멸 메서드의 이름을 변경할 수 없다.
- 내가 코드를 고칠 수 없는 외부 라이브러리에 적용할 수 없다.
- 인터페이스를 사용하는 초기화, 종료 방법은 스프링 초창기에 나온 방법들이고, 지금은 다음의 더 나은 방법
  들이 있어서 거의 사용하지 않는다.
