# @Conditional

- 앞서 만든 메모리 조회 기능을 항상 사용하는 것이 아니라 특정 조건일 때만 해당 기능이 활성화 되도록 해보자.
- 예를 들어서 개발 서버에서 확인 용도로만 해당 기능을 사용하고, 운영 서버에서는 해당 기능을 사용하지 않는 것이다.
- 여기서 핵심은 소스코드를 고치지 않고 이런 것이 가능해야 한다는 점이다.
  - 프로젝트를 빌드해서 나온 빌드 파일을 개발 서버에도 배포하고, 같은 파일을 운영서버에도 배포해야 한다.
- 같은 소스 코드인데 특정 상황일 때만 특정 빈들을 등록해서 사용하도록 도와주는 기능이 바로 ```@Conditional```이다.
- 이 기능은 스프링 부트 자동 구성에서 자주 사용한다.

### Condition

```java
package org.springframework.context.annotation;
public interface Condition {
    boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata);
}
```
- matches() 메서드가 ``true``를 반환하면 조건에 만족해서 동작하고, ``false``를 반환하면 동작하지 않는다.
- ConditionContext : 스프링 컨테이너, 환경 정보등을 담고 있다.
- AnnotatedTypeMetadata : 애노테이션 메타 정보를 담고 있다.


기능 적용 
- Condition 인터페이스를 구현해서 다음과 같이 자바 시스템 속성이 memory=on 이라고 되어 있을 때만 메모리 기능이 
동작하도록 만들어보자.
```text
#VM Options
#java -Dmemory=on -jar project.jar
```

### MemoryCondition

```java
@Slf4j
public class MemoryCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String memory = context.getEnvironment().getProperty("memory");
        log.info("memory={}", memory);
        return "on".equals(memory);
    }
}
```
- 환경 정보에 memory=on 이라고 되어 있는 경우에만 ``true``를 반환한다.

### MemoryConfig - 수정

```java
@Configuration
@Conditional(MemoryCondition.class)
public class MemoryConfig {
    @Bean
    public MemoryController memoryController(){
        return new MemoryController(memoryFinder());
    }
    @Bean
    public MemoryFinder memoryFinder(){
        return new MemoryFinder();
    }
}
```
- ``@Conditional(MemoryCondition.class)``
  - 이제 ``MemoryConfig``의 적용 여부는 ``@Conditional``에 지정한 ``MemoryCondition``의 조건에 따라 달라진다.
  - ``MemoryCondition``의 matches()를 실행해보고 그 결과가 ``true``이면 ``MemoryConfig``는 정상 동작한다.
  - ``MemoryCondition``의 실행결과가 ``false``이면 ``MemoryConfig``는 무효화 된다.

### 실행 

실행
- 먼저 아무 조건을 주지 않고 실행해보자.
- http://localhost:8080/memory

결과 
- Whitelabel Error Page
- memory=on 을 설정하지 않았기 때문에 동작하지 않는다.

실행
- 이번에는 memory=on 조건을 주고 실행해보자.
![1.png](Image%2F1.png)
- VM 옵션을 추가하는 경우 ```-Dmemory=on```를 사용해야 한다.
- http://localhost:8080/memory

결과
- ``{"used":24385432,"max":8589934592}``
- ``MemoryCondition``조건이 ``true``를 반환해서 빈이 정상 등록된다.

### 참고 

```text
스프링은 외부 설정을 추상화해서 Environment 로 통합했다.
그래서 다음과 같은 다양한 외부 환경 설정을 Environment 하나로 읽어들일 수 있다.
```

외부 설정 
```text
#VM Options
java -Dmemory=on -jar project.jar

#Program arguments
# -- 가 있으면 스프링이 환경 정보로 사용
java -jar project.jar --memory=on

#application.properties
#application.properties에 있으면 환경 정보로 사용
memory=on
```
