package hello.hello_spring.repository;

import hello.hello_spring.domain.Member;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MemoryMemberRepositoryTest {
    MemoryMemberRepository repository = new MemoryMemberRepository();

    /**
     * Test 순서 보장 x
     * 각각의 Test는 다른 Test에 영향을 주면 안 된다. 그래서 각 Test를 진행한 이후 clear를 통해서 repository를 비워준다.
     * TDD : 이런 테스트 단위를 먼저 만들어 놓고 해당 테스트를 통과하게 코드를 작성하는 방법 -> 테스트 기반 개발
     */
    @AfterEach
    public void afterEach(){
        repository.clear();
    }

    @Test
    public void save(){
        Member member = new Member();
        member.setName("배제우");
        repository.save(member);

        Member result = repository.findById(member.getId()).get();
        assertThat(result).isEqualTo(member);
    }

    @Test
    public void findByName(){
        Member member1 = new Member();
        member1.setName("배제우1");
        repository.save(member1);
        Member member2 = new Member();
        member2.setName("배제우2");
        repository.save(member2);

        Member result = repository.findByName("배제우1").get();
        assertThat(result).isEqualTo(member1);
    }

    @Test
    public void findAll(){
        Member member1 = new Member();
        member1.setName("배제우1");
        repository.save(member1);
        Member member2 = new Member();
        member2.setName("배제우2");
        repository.save(member2);

        List<Member> result = repository.findAll();

        assertThat(result.size()).isEqualTo(2);

    }
}
