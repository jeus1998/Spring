package hello.core.discount;

import hello.core.member.Grade;
import hello.core.member.Member;

/**
 * 정률%할인 정책
 */
public class RateDiscountPolicy implements DiscountPolicy{
    private int discountPercent = 10; // 10퍼 할인
    @Override
    public int discount(Member member, int price) {
        if(member.getGrade() == Grade.VIP){
            return price * discountPercent / 100;
        }
        return 0;
    }
}
