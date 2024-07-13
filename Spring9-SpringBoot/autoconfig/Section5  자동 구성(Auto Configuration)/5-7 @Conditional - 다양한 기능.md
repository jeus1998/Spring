# @Conditional - 다양한 기능

- 지금까지 ``Condition``인터페이스를 직접 구현해서 ``MemoryCondition``이라는 구현체를 만들었다.
- 스프링은 이미 필요한 대부분의 구현체를 만들어두었다.
- 이번에는 스프링이 제공하는 편리한 기능을 사용해보자.

### MemoryConfig - 수정

```java
@Configuration
// @Conditional(MemoryCondition.class)
@ConditionalOnProperty(name = "memory", havingValue = "on")
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
- ```@ConditionalOnProperty(name = "memory", havingValue = "on")```를 추가
  - 환경 정보가 ``memory=on``이라는 조건에 맞으면 동작하고, 그렇지 않으면 동작하지 않는다.

@ConditionalOnProperty
```java
package org.springframework.boot.autoconfigure.condition;

@Conditional(OnPropertyCondition.class)
public @interface ConditionalOnProperty { 
    // ...
}
```
- ```@ConditionalOnProperty```도 우리가 만든 것과 동일하게 내부에는 ```@Conditional```을 사용한다. 
- 그리고 그 안에 ``Condition``인터페이스를 구현한 ``OnPropertyCondition``를 가지고 있다.

### @ConditionalOnXxx

- 스프링은 ```@Conditional```과 관련해서 개발자가 편리하게 사용할 수 있도록 수 많은 ```@ConditionalOnXxx```를 제공한다.

종류
- @ConditionalOnClass , @ConditionalOnMissingClass
  - 클래스가 있는 경우 동작한다. 나머지는 그 반대
- @ConditionalOnBean , @ConditionalOnMissingBean
  - 빈이 등록되어 있는 경우 동작한다. 나머지는 그 반대
- @ConditionalOnProperty
  - 환경 정보가 있는 경우 동작한다.
- @ConditionalOnResource
  - 리소스가 있는 경우 동작한다.
- @ConditionalOnWebApplication , @ConditionalOnNotWebApplication
  - 웹 애플리케이션인 경우 동작한다.
- @ConditionalOnExpression
  - SpEL 표현식에 만족하는 경우 동작한다.

ConditionalOnXxx 공식 메뉴얼
- https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.developing-auto-configuration.condition-annotations