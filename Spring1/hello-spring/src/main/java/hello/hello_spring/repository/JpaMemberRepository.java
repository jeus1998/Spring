package hello.hello_spring.repository;

import hello.hello_spring.domain.Member;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

/**
 * JPA는 EntityManager를 통해 동작한다.
 * build.gradle에 data-jpa 라이브러리를 추가하면 스프링은 자동으로 EntityManager를 만들어 준다.
 * 정리 JPA를 사용하려면 EntityManager를 스프링 컨테이너로부터 주입 받아야 한다.
 */
public class JpaMemberRepository implements MemberRepository {
    private final EntityManager em;
    public JpaMemberRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public Member save(Member member) {
       em.persist(member); // persist 영속하다
       return member;
    }

    @Override
    public Optional<Member> findById(Long id) {
        Member member = em.find(Member.class, id);
        return Optional.ofNullable(member);
    }

    /**
     * PK 기반이 아닌 search는 JPQL을 사용한다.
     * JPQL ex) em.createQuery("select m from Member m", Member.class)
     */
    @Override
    public Optional<Member> findByName(String name) {
        List<Member> result = em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name" , name)
                .getResultList();
        return result.stream().findAny();
    }

    @Override
    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }
}
