package hello.hello_spring.service;

import hello.hello_spring.domain.Member;
import hello.hello_spring.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @SpringBootTest
 * 테스트 코드는 제일 간단한 방법으로 해도 괜찮다.(다른 용도로 사용 x just test) -> 필드 주입
 * 스프링 통합 테스트 이전에 했던 테스트는 스프링(컨테이너)와 관련이 없는 순수한 자바 코드 테스트였다. -> 단위 테스트
 * 이제 스프링 컨테이너가 함께 테스트
 * 보통 테스트용 DB를 따로 둔다.
 * @Transactional
 * DB에는 트랜잭션이라는 개념이 있다. 실제 DB에 트랜잭션에서 수행했던 결과(쿼리)를 저장하려면
 * commit을 해야 최종적으로 DB에 저장이 된다.
 * 해당 애노테이션이 붙어 있으면 commit을 안하고 rollback을 한다.
 * 정리 : rollback을 통해 테스트케이스 실행 이후 db에 반영 x
 * @Commit <- rollback x commit o
 */
@SpringBootTest
@Transactional
class MemberServiceIntegrationTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
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
