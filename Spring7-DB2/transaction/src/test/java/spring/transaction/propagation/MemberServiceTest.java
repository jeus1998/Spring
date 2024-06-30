package spring.transaction.propagation;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.*;
@Slf4j
@SpringBootTest
class MemberServiceTest {
    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
    @Autowired LogRepository logRepository;

    /**
     *  memberService     @Transactional: OFF
     *  memberRepository  @Transactional: ON
     *  logRepository     @Transactional: ON
     */
    @Test
    void outerTxOff_success(){
        // given
        String username = "outerTxOff_success";

        // when
        memberService.joinV1(username);

        // then : 모든 데이터 정상 저장
        assertThat(memberRepository.find(username).isPresent()).isTrue();
        assertThat(logRepository.find(username).isPresent()).isTrue();
    }


    /**
     *  memberService     @Transactional: OFF
     *  memberRepository  @Transactional: ON
     *  logRepository     @Transactional: ON Exception
     */
    @Test
    void outerTxOff_fail(){
        // given
        String username = "로그예외_outerTxOff_success";

        // when
        assertThatThrownBy(()->memberService.joinV1(username))
                .isInstanceOf(RuntimeException.class);

        // then : 멤버 데이터: 커밋 로그 데이터: 롤백
        assertThat(memberRepository.find(username).isPresent()).isTrue();
        assertThat(logRepository.find(username).isPresent()).isFalse();
    }
}
