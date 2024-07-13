# 자동 구성 이해2 - ImportSelector

- @Import 에 설정 정보를 추가하는 방법은 2가지가 있다.
  - 정적인 방법: @Import (클래스) 이것은 정적이다. 코드에 대상이 딱 박혀 있다. 설정으로 사용할 대상을 동적으로 변경할 수 없다.
  - 동적인 방법: @Import (ImportSelector) 코드로 프로그래밍해서 설정으로 사용할 대상을 동적으로 선택할 수 있다.

정적인 방법
- 스프링에서 다른 설정 정보를 추가하고 싶으면 다음과 같이 @Import 를 사용하면 된다.
```java
@Configuration
@Import({AConfig.class, BConfig.class})
public class AppConfig {
    // ...
}
```

동적인 방법
- 스프링은 설정 정보 대상을 동적으로 선택할 수 있는 ImportSelector 인터페이스를 제공한다.
ImportSelector
```java
package org.springframework.context.annotation;
public interface ImportSelector {
     String[] selectImports(AnnotationMetadata importingClassMetadata);
     //...
}
```

### ImportSelector 예제

HelloBean
```java
package hello.selector;
public class HelloBean {
}
```
- 빈으로 등록할 대상이다.

HelloConfig
```java
@Configuration
public class HelloConfig {
    @Bean
    public HelloBean helloBean(){
        return new HelloBean();
    }
}
```
- 설정 정보이다. HelloBean 을 스프링 빈으로 등록한다.

HelloImportSelector
```java
public class HelloImportSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[]{"hello.selector.HelloConfig"};
    }
}
```
- 설정 정보를 동적으로 선택할 수 있게 해주는 ImportSelector 인터페이스를 구현했다
- 여기서는 단순히 hello.selector.HelloConfig 설정 정보를 반환한다.
- 이렇게 반환된 설정 정보는 선택되어서 사용된다.
- 여기에 설정 정보로 사용할 클래스를 동적으로 프로그래밍 하면 된다.

ImportSelectorTest
```java
public class ImportSelectorTest {
    @Test
    void staticConfig(){
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(StaticConfig.class);
        HelloBean bean = ac.getBean(HelloBean.class);
        assertThat(bean).isNotNull();
    }

    @Test
    void selectorConfig(){
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(SelectorConfig.class);
        HelloBean bean = ac.getBean(HelloBean.class);
        assertThat(bean).isNotNull();
    }

    @Configuration
    @Import(HelloImportSelector.class)
    public static class SelectorConfig{
    }

    @Configuration
    @Import(HelloConfig.class)
    public static class StaticConfig{
    }
}
```
selectorConfig()
- selectorConfig()는 SelectorConfig 를 초기 설정 정보로 사용한다.
- SelectorConfig 는 @Import(HelloImportSelector.class) 에서 ImportSelector 의 구현체인 HelloImportSelector 를 사용했다.
- 스프링은 HelloImportSelector 를 실행하고, "hello.selector.HelloConfig" 라는 문자를 반환 받는다.
- 스프링은 이 문자에 맞는 대상을 설정 정보로 사용한다. 따라서 hello.selector.HelloConfig 이 설정 정보로 사용된다.
- 그 결과 HelloBean 이 스프링 컨테이너에 잘 등록된 것을 확인할 수 있다.

### @EnableAutoConfiguration 동작 방식

@EnableAutoConfiguration
```java
@AutoConfigurationPackage
@Import(AutoConfigurationImportSelector.class)
public @interface EnableAutoConfiguration {
    // 생략 
}
```
- AutoConfigurationImportSelector 는 ImportSelector 의 구현체이다. 
  - 따라서 설정 정보를 동적으로 선택할 수 있다.
- 실제로 이 코드는 모든 라이브러리에 있는 다음 경로의 파일을 확인한다.
- META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
- 그리고 파일의 내용을 읽어서 설정 정보로 선택한다.

스프링 부트 자동 구성이 동작하는 방식은 다음 순서로 확인할 수 있다.
- @SpringBootApplication 
- @EnableAutoConfiguration 
- @Import(AutoConfigurationImportSelector.class)
- resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports 파일을 열어서 설정
  정보 선택
- 해당 파일의 설정 정보가 스프링 컨테이너에 등록되고 사용
