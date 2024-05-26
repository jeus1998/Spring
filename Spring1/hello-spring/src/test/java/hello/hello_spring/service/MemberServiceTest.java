package hello.hello_spring.service;

import hello.hello_spring.domain.Member;
import hello.hello_spring.repository.MemoryMemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 테스트 쉽게 만드는 방법
 * Member Service 에서 클래스 이름에 커서를 올리고 Ctrl + Shift + T
 * 테스트 대상 클레스와 동일한 경로로 테스트 클래스가 자동으로 만들어진다.
 */
class MemberServiceTest {
    MemberService memberService;
    MemoryMemberRepository repository;

    /**
     * @BeforeEach : 각 테스트 실행 전에 진행 로직
     * DI (Dependency Injection)
     * memberService가 사용하는 memberRepository와 생성된 memberRepository를 동일하게 하기 위해서
     */
    @BeforeEach
    public void beforeEach(){
        repository = new MemoryMemberRepository();
        memberService = new MemberService(repository);
    }

    @AfterEach
    public void afterEach(){
        repository.clear();
    }

    /**
     * 테스트의 이름은 한글로 해도 괜찮다.
     * 회원가입
     * given when then
     */
    @Test
    void 회원가입() {

        // given
        Member member = new Member();
        member.setName("hello");

        // when
        Long saveId = memberService.join(member);

        // then
        Member findMember = memberService.findOne(saveId).get();
        assertThat(member.getName()).isEqualTo(findMember.getName());
    }
    @Test
    void 중복회원예외(){
        // given
        Member member1 = new Member();
        member1.setName("spring");

        Member member2 = new Member();
        member2.setName("spring");

        // when
        memberService.join(member1);
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> memberService.join(member2));

        // then
        assertThat(e.getMessage()).isEqualTo("이미 존재하는 회원입니다.");

    }

}