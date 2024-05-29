package hello.core.autowired;

import hello.core.AutoAppConfig;
import hello.core.discount.DiscountPolicy;
import hello.core.discount.FixDiscountPolicy;
import hello.core.member.Grade;
import hello.core.member.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;
import java.util.Map;
import static org.assertj.core.api.Assertions.*;

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
