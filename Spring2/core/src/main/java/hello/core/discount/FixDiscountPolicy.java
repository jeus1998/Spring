package hello.core.discount;

import hello.core.member.Grade;
import hello.core.member.Member;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * 고정 할인 정책
 */
@Component
@Qualifier(Policy.FIX_DISCOUNT)
public class FixDiscountPolicy implements DiscountPolicy{
    private final int discountFixAmount = 1000; // 고정 금액 할인 (1000원)
    @Override
    public int discount(Member member, int price) {
        if(member.getGrade() == Grade.VIP){
            return discountFixAmount;
        }
        return 0;
    }
}
