package hello.hello_spring.service;

import hello.hello_spring.domain.Member;
import hello.hello_spring.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

// @Service @Configuration 통해서 등록

/**
 * JPA를 사용하려면 항상 트랜잭션이 있어야 한다. (데이터 저장만)
 * 그래서 서비스 계층에 -> @Transactional
 */
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;
    @Autowired
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    /**
     * 회원 가입
     */
    public Long join (Member member){

        // 중복 회원 검증
        validateDuplicateMember(member);

        memberRepository.save(member);
        return member.getId();
    }

    /**
     * 중복 회원 검증 로직
     * Extract Method : Alt + Enter
     */
    private void validateDuplicateMember(Member member) {
        memberRepository.findByName(member.getName()).
                ifPresent( m -> {
                throw new IllegalStateException("이미 존재하는 회원입니다.");
        });
    }

    /**
     * 전체 회원 조회
     */
    public List<Member> findMembers(){
        return memberRepository.findAll();
    }

    /**
     * 회원 1명 id로 조회
     */
    public Optional<Member> findOne(Long memberId){
        return memberRepository.findById(memberId);
    }

}
