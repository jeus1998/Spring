package spring.servlet.domain.member;

/**
 *  동시성 문제가 고려되어 있지 않음
 *  실무: ConcurrentHashMap, AtomicLong 사용 고려
 *  싱글톤
 */
import java.util.*;
public class MemberRepository {
    private static Map<Long, Member> store  = new HashMap<>();
    private static Long sequence = 0L;

    private static final MemberRepository instance = new MemberRepository();
    public static MemberRepository getInstance(){
        return instance;
    }

    private MemberRepository(){
    }
    public Member save(Member member){
        member.setId(++sequence);
        store.put(member.getId(), member);
        return member;
    }

    public Member findById(Long id){
        return store.get(id);
    }
    public List<Member> findAll(){
        return new ArrayList<>(store.values());
    }
    public void clearStore() {
        store.clear();
    }
}
