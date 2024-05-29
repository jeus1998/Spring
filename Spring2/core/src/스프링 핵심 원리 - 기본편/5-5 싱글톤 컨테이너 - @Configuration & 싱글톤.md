
# @Configuration & 싱글톤

### AppConfig 분석

```java
@Configuration
public class AppConfig {
    
    @Bean
    public MemberService memberService(){
        return new MemberServiceImpl(memberRepository());
    }
    @Bean
    public OrderService orderService(){
        return new OrderServiceImpl(memberRepository(), discountPolicy());
    }

    @Bean
    public DiscountPolicy discountPolicy() {
        return new RateDiscountPolicy();
    }
    @Bean
    public MemberRepository memberRepository() {
        return new MemoryMemberRepository();
    }
}
```
❓ AppConfig 또한 자바 코드이다. memberService()를 실행하면 memberRepository()를 실행해서 파라미터로 넘긴다.
  orderService()를 실행하면 memberRepository(), discountPolicy()를 실행해서 파라미터로 넘긴다.
  그런데 벌써 memberRepository()를 2번 실행했는데 이럼 MemberRepository 객체가 2개 생겨서 싱글톤이 깨지는 것 
  아닌가 라는 의문점이 생긴다. 스프링 컨테이너는 이 문제를 어떻게 해결할까? 

### OrderServiceImpl & MemberServiceImpl 검증 로직 추가 

- OrderServiceImpl & MemberServiceImpl가 의존하는 MemberRepository의 인스턴스를 반환하는 로직 

```java
    // 테스트 용도 OrderServiceImpl & MemberServiceImpl 모두 추가 
    public MemberRepository getMemberRepository() {
        return memberRepository;
    }
````
### OrderServiceImpl & MemberServiceImpl 테스트

- test/hello.core/singleton/ConfigurationSingletonTest.java

```java
public class ConfigurationSingletonTest {
    @Test
    void configurationTest(){
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);

        MemberServiceImpl memberService = ac.getBean("memberService", MemberServiceImpl.class);
        OrderServiceImpl orderService = ac.getBean("orderService", OrderServiceImpl.class);
        MemberRepository memberRepository = ac.getBean("memberRepository", MemberRepository.class);

        MemberRepository memberRepository1 = memberService.getMemberRepository();
        MemberRepository memberRepository2 = orderService.getMemberRepository();

        System.out.println("memberService -> memberRepository = " + memberRepository1);
        System.out.println("orderService -> memberRepository = " + memberRepository2);
        System.out.println("memberRepository = " + memberRepository);

        assertThat(memberRepository1).isSameAs(memberRepository2);
    }
}
```

```text
memberService -> memberRepository = hello.core.member.MemoryMemberRepository@548e76f1
orderService -> memberRepository = hello.core.member.MemoryMemberRepository@548e76f1
memberRepository = hello.core.member.MemoryMemberRepository@548e76f1
```

- 확인해보면 memberRepository 인스턴스는 모두 같은 인스턴스가 공유되어 사용된다.
- AppConfig의 자바 코드를 보면 분명히 각각 3번 new MemoryMemberRepository 호출해서 다른 인스턴
  스가 생성되어야 하는데?
- @Configuration & 바이트코드 조작을 확인하자.
