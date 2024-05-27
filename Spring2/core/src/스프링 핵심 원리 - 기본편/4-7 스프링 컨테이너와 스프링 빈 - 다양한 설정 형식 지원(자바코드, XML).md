
# 다양한 설정 형식 지원 - 자바 코드, XML

![다양한 설정 형식 지원 (xml, 자바코드).JPG](0%20%EC%9D%B4%EB%AF%B8%EC%A7%80%2F%EB%8B%A4%EC%96%91%ED%95%9C%20%EC%84%A4%EC%A0%95%20%ED%98%95%EC%8B%9D%20%EC%A7%80%EC%9B%90%20%28xml%2C%20%EC%9E%90%EB%B0%94%EC%BD%94%EB%93%9C%29.JPG)

- 스프링 컨테이너는 다양한 형식의 설정 정보를 받아들일 수 있게 유연하게 설계되어 있다.
- 자바 코드, XML, Groovy 등등

## 애노테이션 기반 자바 코드 설정 사용

- 지금까지 했던 것이다.
- new AnnotationConfigApplicationContext(AppConfig.class)
- AnnotationConfigApplicationContext 클래스를 사용하면서 자바 코드로된 설정 정보를 넘기면 된다.

## XML 설정 사용

- 최근에는 스프링 부트를 많이 사용하면서 XML기반의 설정은 잘 사용하지 않는다.
- 아직 많은 레거시 프로젝트 들이 XML로 되어 있고, 또 XML을 사용하면 컴파일 없이 빈 설정 정보를 변경할 수 있는 장점도 있으므로 한번쯤 배워두는 것도 괜찮다.
- GenericXmlApplicationContext 를 사용하면서 xml 설정 파일을 넘기면 된다
- ApplicationContext ac = new GenericXmlApplicationContext("appConfig.xml");

### GenericXmlApplicationContext
```java
class XmlAppContext {
    @Test
    void xmlAppContext(){
        ApplicationContext ac = new GenericXmlApplicationContext("appConfig.xml");
        MemberService memberService = ac.getBean("memberService", MemberService.class);
        assertThat(memberService).isInstanceOf(MemberService.class);
    }
}
```

### xml 기반의 스프링 빈 설정 정보

- src/main/resources/appConfig.xml
- xml 기반의 appConfig.xml 스프링 설정 정보와 자바 코드로 된 AppConfig.java 설정 정보를 비교해보면 거의 비슷하다는 것을 알 수 있다.
- 더 자세한 내용은 스프링 공식 레퍼런스 문서를 확인하자.
- https://spring.io/projects/spring-framework

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="memberService" class="hello.core.member.MemberServiceImpl">
        <constructor-arg name="memberRepository" ref="memberRepository" />
    </bean>

    <bean id="memberRepository" class="hello.core.member.MemoryMemberRepository" />

    <bean id="orderService" class="hello.core.order.OrderServiceImpl">
        <constructor-arg name="memberRepository" ref="memberRepository" />
        <constructor-arg name="discountPolicy" ref="discountPolicy" />
    </bean>

    <bean id="discountPolicy" class="hello.core.discount.RateDiscountPolicy" />
</beans>
```

