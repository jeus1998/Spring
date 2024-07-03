# CGLIB - 예제 코드

### MethodInterceptor 

- JDK 동적 프록시에서 실행 로직을 위해 ``InvocationHandler``를 제공했듯이, CGLIB는 ``MethodInterceptor``를 제공한다.

MethodInterceptor -CGLIB 제공
```java
public interface MethodInterceptor extends Callback {
    Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable;
}
```
- obj : CGLIB가 적용된 객체
- method : 호출된 메서드
- args : 메서드를 호출하면서 전달된 인수
- proxy : 메서드 호출에 사용

### TimeMethodInterceptor

```java
@Slf4j
public class TimeMethodInterceptor implements MethodInterceptor {
    private final Object target;
    public TimeMethodInterceptor(Object target) {
        this.target = target;
    }
    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        log.info("TimeProxy 실행");
        long startTime = System.currentTimeMillis();

        Object result = methodProxy.invoke(target, args);

        long endTime = System.currentTimeMillis();
        long resultTime = endTime - startTime;

        log.info("TimeProxy 종료 resultTime={}ms", resultTime);
        return result;
    }
}
```
- ``TimeMethodInterceptor``는 ``MethodInterceptor``인터페이스를 구현해서 CGLIB 프록시의 실행 로직을 정의한다.
- JDK 동적 프록시를 설명할 때 예제와 거의 같은 코드이다.
- ``Object target``: 프록시가 호출할 실제 대상
- ``proxy.invoke(target, args)``: 실제 대상을 동적으로 호출한다.
  - 참고로 ``method``를 사용해도 되지만, CGLIB는 성능상 ``MethodProxy proxy``를 사용하는 것을 권장한다.

### CglibTest

```java
@Slf4j
public class CglibTest {
    @Test
    void cglib(){
        ConcreteService target = new ConcreteService();
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(ConcreteService.class);
        enhancer.setCallback(new TimeMethodInterceptor(target));
        ConcreteService proxy = (ConcreteService)enhancer.create();
        log.info("targetClass={}", target.getClass());
        log.info("proxyClass={}", proxy.getClass());

        proxy.call();
    }
}
```
- ``ConcreteService``는 인터페이스가 없는 구체 클래스이다. 여기에 CGLIB를 사용해서 프록시를 생성해보자.
- ``Enhancer``: CGLIB는 ``Enhancer``를 사용해서 프록시를 생성한다
- ``enhancer.setSuperclass(ConcreteService.class)``: CGLIB는 구체 클래스를 상속 받아서 프록시를 생성할 수 있다. 
  어떤 구체 클래스를 상속 받을지 지정한다.
- ``enhancer.setCallback(new TimeMethodInterceptor(target))``
  - 프록시에 적용할 실행 로직을 할당한다.
- ``enhancer.create()``: 프록시를 생성한다. 앞서 설정한 ``enhancer.setSuperclass(ConcreteService.class)``에서 
  지정한 클래스를 상속 받아서 프록시가 만들어진다.
- JDK 동적 프록시는 인터페이스를 구현(implement)해서 프록시를 만든다. 
- CGLIB는 구체 클래스를 상속(extends)해서 프록시를 만든다.

실행 결과
```text
CglibTest - targetClass=class hello.proxy.common.service.ConcreteService
CglibTest - proxyClass=class hello.proxy.common.service.ConcreteService$
$EnhancerByCGLIB$$25d6b0e3
TimeMethodInterceptor - TimeProxy 실행
ConcreteService - ConcreteService 호출
TimeMethodInterceptor - TimeProxy 종료 resultTime=9
```

CGLIB가 생성한 프록시 클래스 이름
- ``ConcreteService$$EnhancerByCGLIB$$25d6b0e3``

CGLIB가 동적으로 생성하는 클래스 이름은 다음과 같은 규칙으로 생성된다.
- ``대상클래스$$EnhancerByCGLIB$$임의코드``

JDK Proxy가 생성한 클래스 이름
- ``proxyClass=class com.sun.proxy.$Proxy1``

### 그림으로 정리

클래스 의존 관계
![9.png](Image%2F9.png)

런타임 객체 의존 관계 - CGLIB
![10.png](Image%2F10.png)

CGLIB 제약
- 클래스 기반 프록시는 상속을 사용하기 때문에 몇가지 제약이 있다
  - 부모 클래스의 생성자를 체크해야 한다. ➡️ CGLIB는 자식 클래스를 동적으로 생성하기 때문에 기본 생성자가 필요하다.
  - 클래스에 ``final`` 키워드가 붙으면 상속이 불가능하다. ️ ➡️ CGLIB에서는 예외가 발생한다. 
  - 메서드에 ``final`` 키워드가 붙으면 해당 메서드를 오버라이딩 할 수 없다. ➡️ CGLIB에서는 프록시 로직이 동작하지 않는다.

참고
- CGLIB를 사용하면 인터페이스가 없는 V2 애플리케이션에 동적 프록시를 적용할 수 있다
- 그런데 지금 당장 적용 하기에는 몇가지 제약이 있다. V2 애플리케이션에 기본 생성자를 추가하고, 의존관계를 ``setter``를 사용해서 
  주입하면 CGLIB를 적용할 수 있다. 
- ``ProxyFactory``를 통해서 CGLIB를 적용하면 이런 단점을 해결하고 또 더 편리하다.

남은 문제
- 인터페이스가 있는 경우에는 JDK 동적 프록시를 적용하고, 그렇지 않은 경우에는 CGLIB를 적용하려면 어떻게 해야할까?
- 두 기술을 함께 사용할 때 부가 기능을 제공하기 위해서 JDK 동적 프록시가 제공하는 ``InvocationHandler``와 
  CGLIB가 제공하는 ``MethodInterceptor``를 각각 중복으로 만들어서 관리해야 할까?
- 특정 조건에 맞을 때 프록시 로직을 적용하는 기능도 공통으로 제공되었으면?

