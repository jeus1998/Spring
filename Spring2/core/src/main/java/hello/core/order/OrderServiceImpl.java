package hello.core.order;

import hello.core.discount.DiscountPolicy;
import hello.core.member.MemberRepository;
import hello.core.member.MemoryMemberRepository;

public class OrderServiceImpl implements OrderService{
    /*
    private MemberRepository memberRepository = new MemoryMemberRepository();

    할인 정책을 변경하려면 클라이언트인 OrderServiceImpl 코드를 고쳐야 한다.
    private DiscountPolicy discountPolicy = new FixDiscountPolicy();
    private DiscountPolicy discountPolicy = new RateDiscountPolicy();
    인터페이스에만 의존하다록 설계 변경
     */
    private final MemberRepository memberRepository;
    private final DiscountPolicy discountPolicy;

    public OrderServiceImpl(MemberRepository memberRepository, DiscountPolicy discountPolicy) {
        this.memberRepository = memberRepository;
        this.discountPolicy = discountPolicy;
    }
    @Override
    public Order createOrder(Long memberId, String itemName, int itemPrice) {
        int discount = discountPolicy.discount(memberRepository.findById(memberId), itemPrice);
        return new Order(memberId, itemName, itemPrice, discount);
    }
}
