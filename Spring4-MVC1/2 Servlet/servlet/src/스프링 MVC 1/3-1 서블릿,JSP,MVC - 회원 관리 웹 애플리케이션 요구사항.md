
# 회원 관리 웹 애플리케이션 요구사항

### 요구사항 
- 회원 정보 
  - 이름: username
  - 나이: age
- 기능 요구사항
  - 회원 저장
  - 회원 목록 조회

### Member 

/domain/member/Member.java

```java
@Getter @Setter
public class Member {
    private Long id;
    private String username;
    private int age;

    public Member() {
    }
    
    public Member(String username, int age) {
        this.username = username;
        this.age = age;
    }
}
```
### MemberRepository

/domain/member/MemberRepository.java

```java
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
```

### 테스트 MemberRepositoryTest - Test Package

/domain/member/MemberRepositoryTest.java

```java
class MemberRepositoryTest {
    MemberRepository memberRepository = MemberRepository.getInstance();
    @AfterEach
    void after(){
        memberRepository.clearStore();
    }
    @Test
    void save(){
        // given
        Member member = new Member("hello", 20);

        // when
        Member saveMember = memberRepository.save(member);

        // then
        Member findMember = memberRepository.findById(saveMember.getId());

        assertThat(findMember).isSameAs(saveMember);
    }
    @Test
    void findAll(){
        // given
        Member member1 = new Member("member1", 20);
        Member member2 = new Member("member2", 20);

        Member saveMember1 = memberRepository.save(member1);
        Member saveMember2 = memberRepository.save(member2);

        // when
        List<Member> result = memberRepository.findAll();

        // then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result).contains(saveMember1, saveMember2);
    }
}
```

