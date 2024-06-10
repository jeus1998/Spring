package spring.jdbc.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import spring.jdbc.domain.Member;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
class MemberRepositoryV0Test {
    MemberRepositoryV0 repository = new MemberRepositoryV0();
    @Test
    void crud() throws SQLException {
        // save
        Member member = new Member("memberV2", 10000);
        Member saveMember = repository.save(member);
        assertThat(saveMember).isSameAs(member);

        // findById
        Member findMember = repository.findById(member.getMemberId());
        log.info("findMember={}", findMember);
        assertThat(findMember).isEqualTo(member);

        // update
        repository.update(findMember.getMemberId(), 20000);
        Member afterUpdate = repository.findById(findMember.getMemberId());
        assertThat(afterUpdate.getMoney()).isEqualTo(20000);

        // delete
        repository.delete(afterUpdate.getMemberId());
        assertThatThrownBy(()-> repository.findById(afterUpdate.getMemberId()))
                .isInstanceOf(NoSuchElementException.class);
    }
}