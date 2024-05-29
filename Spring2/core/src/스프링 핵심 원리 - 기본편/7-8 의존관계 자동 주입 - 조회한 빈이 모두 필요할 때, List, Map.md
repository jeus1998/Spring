
# 의존관계 자동 주입 - 조회한 빈이 모두 필요할 때, List, Map

의도적으로 정말 해당 타입의 스프링 빈이 다 필요한 경우도 있다.
예를 들어서 할인 서비스를 제공하는데, 클라이언트가 할인의 종류(rate, fix)를 선택할 수 있다고 가정해보자.
스프링을 사용하면 소위 말하는 전략 패턴을 매우 간단하게 구현할 수 있다.

-test/java/hello/core/autowired/AllBeanTest.java

```java
public class AllBeanTest {
    @Test
    void findAllBean(){
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(AutoAppConfig.class, DiscountService.class);

    }
    static class DiscountService{
        private final Map<String, DiscountPolicy> discountPolicyMap;
        private final List<DiscountPolicy> discountPolicyList;

        @Autowired
        public DiscountService(Map<String, DiscountPolicy> discountPolicyMap, List<DiscountPolicy> discountPolicyList) {
            this.discountPolicyMap = discountPolicyMap;
            this.discountPolicyList = discountPolicyList;
            System.out.println("discountPolicyMap = " + discountPolicyMap);
            System.out.println("discountPolicyList = " + discountPolicyList);
        }
    }
}
```

```text
discountPolicyMap = {fixDiscountPolicy=hello.core.discount.FixDiscountPolicy@4a9419d7, rateDiscountPolicy=hello.core.discount.RateDiscountPolicy@2f3c6ac4}
discountPolicyList = [hello.core.discount.FixDiscountPolicy@4a9419d7, hello.core.discount.RateDiscountPolicy@2f3c6ac4]
```

- AutoAppConfig : @ComponentScan 애노테이션으로 FixDiscountPolicy, RateDiscountPolicy 모두 스프링빈으로 등록 
- DiscountService : 스프링빈으로 등록되면서 DiscountPolicy 자식 2개를 각각 map & list 생성자 의존 관계 주입


```java
public class AllBeanTest {
    @Test
    void findAllBean(){
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(AutoAppConfig.class, DiscountService.class);
        DiscountService discountService = ac.getBean( DiscountService.class);

        assertThat(discountService).isInstanceOf(DiscountService.class);

        Member member = new Member(1L, "userA", Grade.VIP);
        int fixDiscountPrice = discountService.discount(member, 20000, "fixDiscountPolicy");

        assertThat(fixDiscountPrice).isEqualTo(1000);

        int rateDiscountPrice = discountService.discount(member, 20000, "rateDiscountPolicy");

        assertThat(rateDiscountPrice).isEqualTo(2000);

    }
    static class DiscountService{
        private final Map<String, DiscountPolicy> discountPolicyMap;
        private final List<DiscountPolicy> discountPolicyList;

        @Autowired
        public DiscountService(Map<String, DiscountPolicy> discountPolicyMap, List<DiscountPolicy> discountPolicyList) {
            this.discountPolicyMap = discountPolicyMap;
            this.discountPolicyList = discountPolicyList;
            System.out.println("discountPolicyMap = " + discountPolicyMap);
            System.out.println("discountPolicyList = " + discountPolicyList);
        }

        public int discount(Member member, int price, String discountCode){
            DiscountPolicy discountPolicy = discountPolicyMap.get(discountCode);
            return discountPolicy.discount(member, price);
        }
    }
}
```

- discount() 메서드는 discountCode로 "fixDiscountPolicy"가 넘어오면 map에서 fixDiscountPolicy 스프링 빈을 찾아서 실행한다.
- 물론 “rateDiscountPolicy”가 넘어오면 rateDiscountPolicy 스프링 빈을 찾아서 실행한다.

✅ 참고
- new AnnotationConfigApplicationContext(AutoAppConfig.class,DiscountService.class);
- new AnnotationConfigApplicationContext() 를 통해 스프링 컨테이너를 생성한다.
- AutoAppConfig.class , DiscountService.class 를 파라미터로 넘기면서 해당 클래스를 자동으로 스
  프링 빈으로 등록한다.