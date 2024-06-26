package spring.transaction.propagation;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.UnexpectedRollbackException;

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
        String username = "로그예외_outerTxOff_fail";

        // when
        assertThatThrownBy(()->memberService.joinV1(username))
                .isInstanceOf(RuntimeException.class);

        // then : 멤버 데이터: 커밋 로그 데이터: 롤백
        assertThat(memberRepository.find(username).isPresent()).isTrue();
        assertThat(logRepository.find(username).isPresent()).isFalse();
    }

    /**
     *  memberService     @Transactional: ON
     *  memberRepository  @Transactional: OFF
     *  logRepository     @Transactional: OFF
     */
    @Test
    void singleTx(){
        // given
        String username = "singleTx";

        // when
        memberService.joinV1(username);

        // then : 모든 데이터 정상 저장
        assertThat(memberRepository.find(username).isPresent()).isTrue();
        assertThat(logRepository.find(username).isPresent()).isTrue();
    }

    /**
     *  memberService     @Transactional: ON
     *  memberRepository  @Transactional: ON
     *  logRepository     @Transactional: ON
     */
    @Test
    void outerTxOn_success(){
        // given
        String username = "outerTxOn_success";

        // when
        memberService.joinV1(username);

        // then : 모든 데이터 정상 저장
        assertThat(memberRepository.find(username).isPresent()).isTrue();
        assertThat(logRepository.find(username).isPresent()).isTrue();
    }
    /**
     *  memberService     @Transactional: ON
     *  memberRepository  @Transactional: ON
     *  logRepository     @Transactional: ON
     */
    @Test
    void outerTxOn_fail(){
        // given
        String username = "로그예외_outerTxOn_fail";

        // when
        assertThatThrownBy(()->memberService.joinV1(username))
                       .isInstanceOf(RuntimeException.class);

        // then : 모든 데이터 롤백
        assertThat(memberRepository.find(username).isPresent()).isFalse();
        assertThat(logRepository.find(username).isPresent()).isFalse();
    }
    /**
     *  memberService     @Transactional: ON
     *  memberRepository  @Transactional: ON
     *  logRepository     @Transactional: ON Exception
     */
    @Test
    void recoverException_fail(){
        // given
        String username = "로그예외_recoverException_fail";

        // when
        assertThatThrownBy(()->memberService.joinV2(username))
                       .isInstanceOf(UnexpectedRollbackException.class);

        // then : 모든 데이터 롤백
        assertThat(memberRepository.find(username).isPresent()).isFalse();
        assertThat(logRepository.find(username).isPresent()).isFalse();
    }

    /**
    *  memberService     @Transactional: ON
    *  memberRepository  @Transactional: ON
    *  logRepository     @Transactional: ON(REQUIRES_NEW) Exception
    */
    @Test
    void recoverException_success(){
        // given
        String username = "로그예외_recoverException_success";

        // when
        memberService.joinV2(username);

        // then : memberRepository 커밋, logRepository 롤백
        assertThat(memberRepository.find(username).isPresent()).isTrue();
        assertThat(logRepository.find(username).isEmpty()).isTrue();
    }
}
