
# Configuration & 바이트코드 조작

스프링 컨테이너는 싱글톤 레지스트리다. 따라서 스프링 빈이 싱글톤이 되도록 보장해주어야 한다
그런데 스프링이 자바 코드까지 어떻게 하기는 어렵다.
AppConfig에서 memberRepository()는 3번 호출되어야 하는 것이 맞다.
그래서 스프링은 클래스의 바이트코드를 조작하는 라이브러리를 사용한다.
모든 비밀은 @Configuration 을 적용한 AppConfig 에 있다.

### AppConfig 분석하기 

- AnnotationConfigApplicationContext 에 파라미터로 넘긴 값은 스프링 빈으로 등록된다. 👉 AppConfig 또한 스프링 빈이 된다.
- AppConfig 스프링 빈을 조회해서 클래스 정보를 출력해보자.
- test/hello.core/singleton/ConfigurationSingletonTest.java

```java
    @Test
    void configurationDeep(){
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);
        AppConfig bean = ac.getBean(AppConfig.class);
        System.out.println("bean = " + bean.getClass());
    }
```

```text
bean = class hello.core.AppConfig$$SpringCGLIB$$0
```

- 순수한 클래스라면 class hello.core.AppConfig 이렇게 출력되어야 한다.
- 그런데 예상과는 다르게 클래스 명에 xxxCGLIB가 붙으면서 상당히 복잡해진 것을 볼 수 있다.
- 이것은 내가 만든 클래스가 아니라 스프링이 CGLIB라는 바이트코드 조작 라이브러리를 사용해서 
  AppConfig 클래스를 상속받은 임의의 다른 클래스를 만들고, 그 다른 클래스를 스프링 빈으로 등록한 것이다!

![AppConfig CGLIB.JPG](0%20%EC%9D%B4%EB%AF%B8%EC%A7%80%2FAppConfig%20CGLIB.JPG)

- 그 임의의 다른 클래스가 바로 싱글톤이 보장되도록 해준다. 아마도 다음과 같이 바이트 코드를 조작해서 작성되어 있을
  것이다.
- 실제 CGLIB의 내부 기술은 매우 복잡하다.

### AppConfig@CGLIB 예상 코드

```java
@Bean
public MemberRepository memberRepository() {
        if(memoryMemberRepository가 이미 스프링 컨테이너에 등록되어 있으면?) {
                return 스프링 컨테이너에서 찾아서 반환;
        } 
        else { //스프링 컨테이너에 없으면
            기존 로직을 호출해서 MemoryMemberRepository를 생성하고 스프링 컨테이너에 등록
                return 반환
         }
}
```

- @Bean이 붙은 메서드마다 이미 스프링 빈이 존재하면 존재하는 빈을 반환하고, 스프링 빈이 없으면 생성해서 스
  프링 빈으로 등록하고 반환하는 코드가 동적으로 만들어진다. 👉 싱글톤이 보장

✅ 참고: AppConfig@CGLIB는 AppConfig의 자식 타입이므로, AppConfig 타입으로 조회 할 수 있다.

## @Configuration을 적용하지 않고, @Bean만 적용하면 어떻게 될까?

- @Configuration을 붙이면 바이트코드를 조작하는 CGLIB 기술을 사용해서 싱글톤을 보장하지만, 만약 @Bean만
  적용하면 어떻게 될까?

```java
// @Configuration 삭제
public class AppConfig {
}
```

```text
bean = class hello.core.AppConfig
```

- 이 출력 결과를 통해서 AppConfig가 CGLIB 기술 없이 순수한 AppConfig로 스프링 빈에 등록된 것을 확인할 수 있
  다.

### 인스턴스가 같은지 테스트 결과

- test/hello.core/singleton/ConfigurationSingletonTest.java

```java
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
```

```text
memberService -> memberRepository = hello.core.member.MemoryMemberRepository@6239aba6
orderService -> memberRepository = hello.core.member.MemoryMemberRepository@3e6104fc
memberRepository = hello.core.member.MemoryMemberRepository@12359a82
```

- 당연히 인스턴스가 같은지 테스트 하는 코드도 실패하고, 각각 다 다른 MemoryMemberRepository 인스턴스를 가
  지고 있다.

💯 정리
- @Bean만 사용해도 스프링 빈으로 등록되지만, 싱글톤을 보장하지 않는다.
- memberRepository() 처럼 의존관계 주입이 필요해서 메서드를 직접 호출할 때 싱글톤을 보장하지 않
  는다.
- 크게 고민할 것이 없다. 스프링 설정 정보는 항상 @Configuration을 사용하자.
- 최종적으로 MemberRepository 인스턴스는 스프링 빈으로 1개 일반 객체로 2개가 생긴다.

