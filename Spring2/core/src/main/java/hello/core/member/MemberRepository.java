package hello.core.member;

/**
 * <<interface>
 *  save & findById
 */
public interface MemberRepository {
    void save(Member member);
    Member findById(Long memberId);
}
