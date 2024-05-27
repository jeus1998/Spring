package hello.core.member;

/**
 * 관례상 인터페이스의 구현체가 1개라면 뒤에 'Impl'붙여서 구현체 클래스를 만든다.
 * join & findMember
 */
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository = new MemoryMemberRepository();
    @Override
    public void join(Member member) {
        memberRepository.save(member);
    }
    @Override
    public Member findMember(Long memberId) {
       return memberRepository.findById(memberId);
    }
}
