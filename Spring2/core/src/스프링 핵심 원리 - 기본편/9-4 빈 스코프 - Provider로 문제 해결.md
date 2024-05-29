
# 프로토타입 스코프 - 싱글톤 빈과 함께 사용시 Provider로 문제 해결

싱글톤 빈과 프로토타입 빈을 함께 사용할 때, 어떻게 하면 사용할 때 마다 항상 새로운 프로토타입 빈을 생성할 수 있을
까?

### 스프링 컨테이너에 요청

가장 간단한 방법은 싱글톤 빈이 프로토타입을 사용할 때 마다 스프링 컨테이너에 새로 요청하는 것이다.

- /test/java/hello/core/scope/SingletonWithPrototypeTestDL.java

```java
public class SingletonWithPrototypeTestDL {
    @Test
    void test(){
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(ClientBean.class, PrototypeBean.class);

        ClientBean clientBean1 = ac.getBean(ClientBean.class);
        int count1 = clientBean1.logic();

        ClientBean clientBean2 = ac.getBean(ClientBean.class);
        int count2 = clientBean2.logic();

        assertThat(count1).isEqualTo(1);
        assertThat(count2).isEqualTo(1);
    }

    @Scope("singleton")
    @RequiredArgsConstructor
    static class ClientBean{
        
        private final ApplicationContext ac;
        
        public int logic(){
            PrototypeBean prototypeBean = ac.getBean(PrototypeBean.class);
            prototypeBean.addCount();
            return prototypeBean.getCount();
        }
    }

    @Scope("prototype")
    static class PrototypeBean{
        private int count = 0;
        public void addCount(){
            this.count++;
        }
        private int getCount(){
            return this.count;
        }
        @PostConstruct
        public void init(){
            System.out.println("PrototypeBean.init " + this);
        }
        @PreDestroy
        public void destroy(){
            System.out.println("PrototypeBean.destroy");
        }
    }
}
```
- 실행해보면 ac.getBean() 을 통해서 항상 새로운 프로토타입 빈이 생성되는 것을 확인할 수 있다.
- 의존관계를 외부에서 주입(DI) 받는게 아니라 이렇게 직접 필요한 의존관계를 찾는 것을 Dependency Lookup
  (DL) 의존관계 조회(탐색) 이라한다.
- 그런데 이렇게 스프링의 애플리케이션 컨텍스트 전체를 주입받게 되면, 스프링 컨테이너에 종속적인 코드가 되고,
  단위 테스트도 어려워진다.
- 지금 필요한 기능은 지정한 프로토타입 빈을 컨테이너에서 대신 찾아주는 딱! DL 정도의 기능만 제공하는 무언가
  가 있으면 된다. 

### ObjectFactory, ObjectProvider

지정한 빈을 컨테이너에서 대신 찾아주는 DL 서비스를 제공하는 것이 바로 ObjectProvider 이다. 참고로 과거에는
ObjectFactory 가 있었는데, 여기에 편의 기능을 추가해서 ObjectProvider 가 만들어졌다.

- /test/java/hello/core/scope/SingletonWithPrototypeTestProvider.java

```java
public class SingletonWithPrototypeTestProvider {

    @Test
    void singletonClientUsePrototypeProvider(){
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(ClientBean.class, PrototypeBean.class);

        ClientBean clientBean1 = ac.getBean(ClientBean.class);
        int count1 = clientBean1.logic();
        assertThat(count1).isEqualTo(1);

        ClientBean clientBean2 = ac.getBean(ClientBean.class);
        int count2 = clientBean2.logic();
        assertThat(count2).isEqualTo(1);
    }

    /**
     * ObjectFactory -> ObjectProvider(ObjectFactory + 편의 기능 추가)
     * DL = Dependency LookUp 
     */
    @Scope("singleton")
    static class ClientBean{
        
        @Autowired private ObjectProvider<PrototypeBean> objectProvider;
        
        public int logic(){
            PrototypeBean prototypeBean = objectProvider.getObject();
            prototypeBean.addCount();
            return prototypeBean.getCount();
        }
    }

    @Scope("prototype")
    static class PrototypeBean{
        private int count = 0;
        public void addCount(){
            this.count++;
        }
        private int getCount(){
            return this.count;
        }
        @PostConstruct
        public void init(){
            System.out.println("PrototypeBean.init " + this);
        }
        @PreDestroy
        public void destroy(){
            System.out.println("PrototypeBean.destroy");
        }
    }
}
```
```text
PrototypeBean.init hello.core.scope.SingletonWithPrototypeTestProvider$PrototypeBean@21d8bcbe
PrototypeBean.init hello.core.scope.SingletonWithPrototypeTestProvider$PrototypeBean@c65a5ef
```

- 실행해보면 prototypeBeanProvider.getObject() 을 통해서 항상 새로운 프로토타입 빈이 생성되는 것
  을 확인할 수 있다
- ObjectProvider 의 getObject() 를 호출하면 내부에서는 스프링 컨테이너를 통해 해당 빈을 찾아서 반환
  한다. (DL)
- 스프링이 제공하는 기능을 사용하지만, 기능이 단순하므로 단위테스트를 만들거나 mock 코드를 만들기는 훨씬
  쉬워진다.
- ObjectProvider 는 지금 딱 필요한 DL 정도의 기능만 제공한다.

### ObjectFactory, ObjectProvider 특징 

- ObjectFactory: 기능이 단순, 별도의 라이브러리 필요 없음, 스프링에 의존
- ObjectProvider: ObjectFactory 상속, 옵션, 스트림 처리등 편의 기능이 많고, 별도의 라이브러리 필요 없음,
  스프링에 의존

### JSR-330 Provider

마지막 방법은 javax.inject.Provider 라는 JSR-330 자바 표준을 사용하는 방법이다.
스프링 부트 3.0은 jakarta.inject.Provider 사용한다

사용하려면 다음 라이브러리를 build.gradle 추가해야 한다.

스프링 부트 3.0 미만 
```text
implementation 'javax.inject:javax.inject:1'
```
스프링 부트 3.0 이상 
```text
implementation 'jakarta.inject:jakarta.inject-api:2.0.1'
```

```java
import jakarta.inject.Provider;

public class SingletonWithPrototypeTestJSR_330Provider {
    @Test
    void singletonClientUsePrototypeProvider(){
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(ClientBean.class, PrototypeBean.class);

       ClientBean clientBean1 = ac.getBean(ClientBean.class);
        int count1 = clientBean1.logic();
        assertThat(count1).isEqualTo(1);

       ClientBean clientBean2 = ac.getBean(ClientBean.class);
        int count2 = clientBean2.logic();
        assertThat(count2).isEqualTo(1);
    }

    @Scope("singleton")
    static class ClientBean{

        @Autowired
        private Provider<PrototypeBean> provider;

        public int logic(){
            PrototypeBean prototypeBean = provider.get();
            prototypeBean.addCount();
            return prototypeBean.getCount();
        }
    }

    @Scope("prototype")
    static class PrototypeBean{
        private int count = 0;
        public void addCount(){
            this.count++;
        }
        private int getCount(){
            return this.count;
        }
        @PostConstruct
        public void init(){
            System.out.println("PrototypeBean.init " + this);
        }
        @PreDestroy
        public void destroy(){
            System.out.println("PrototypeBean.destroy");
        }
    }
}
```
- 실행해보면 provider.get() 을 통해서 항상 새로운 프로토타입 빈이 생성되는 것을 확인할 수 있다.
- provider 의 get() 을 호출하면 내부에서는 스프링 컨테이너를 통해 해당 빈을 찾아서 반환한다. (DL)
- 자바 표준이고, 기능이 단순하므로 단위테스트를 만들거나 mock 코드를 만들기는 훨씬 쉬워진다.
- Provider 는 지금 딱 필요한 DL 정도의 기능만 제공한다.

특징
- get() 메서드 하나로 기능이 매우 단순하다.
- 별도의 라이브러리가 필요하다.
- 자바 표준이므로 스프링이 아닌 다른 컨테이너에서도 사용할 수 있다.

### 💯 정리 

- 그러면 프로토타입 빈을 언제 사용할까? 매번 사용할 때 마다 의존관계 주입이 완료된 새로운 객체가 필요하면 사
  용하면 된다.
- 그런데 실무에서 웹 애플리케이션을 개발해보면, 싱글톤 빈으로 대부분의 문제를 해결할 수 있기 
  때문에 프로토타입 빈을 직접적으로 사용하는 일은 매우 드물다.
- ObjectProvider , JSR330 Provider 등은 프로토타입 뿐만 아니라 DL이 필요한 경우는 언제든지 사용할
  수 있다.

### ✅ 참고

- 실무에서 자바 표준인 JSR-330 Provider를 사용할 것인지, 아니면 스프링이 제공하는 ObjectProvider
  를 사용할 것인지 고민이 될 것이다.
- ObjectProvider는 DL을 위한 편의 기능을 많이 제공해주고 스프링 외에 별도의 의존관계 추가가 필요 없기 때문에 편리하다. 
  만약(정말 그럴일은 거의 없겠지만) 코드를 스프링이 아닌 다른 컨테이너에서도 사용할 수 있어야 한다면 JSR-330 Provider를 사용해야한다. 


