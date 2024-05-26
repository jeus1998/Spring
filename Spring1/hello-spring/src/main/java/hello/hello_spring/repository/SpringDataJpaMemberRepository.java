package hello.hello_spring.repository;

import hello.hello_spring.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * JpaRepository<Entity, Entity pk type>
 * Spring Jpa Data가 자동으로 해당 repository 인터페이스를 스프링 빈으로 등록
 */

public interface SpringDataJpaMemberRepository extends MemberRepository , JpaRepository<Member, Long> {

    // JPQL select m from Member m where m.name = ?
    @Override
    Optional<Member> findByName(String name);

}
