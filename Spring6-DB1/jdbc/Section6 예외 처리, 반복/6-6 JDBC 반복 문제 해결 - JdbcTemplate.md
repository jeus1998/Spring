# JDBC 반복 문제 해결 - JdbcTemplate

- 지금까지 서비스 계층의 순수함을 유지하기 위해 수 많은 노력을 했고, 덕분에 서비스 계층의 순수함을 유지하게 되었다.
- 이번에는 리포지토리에서 JDBC를 사용하기 때문에 발생하는 반복 문제를 해결해보자.

JDBC 반복 문제
- 커넥션 조회, 커넥션 동기화
- PreparedStatement 생성 및 파라미터 바인딩
- 쿼리 실행
- 결과 바인딩
- 예외 발생시 스프링 예외 변환기 실행
- 리소스 종료

```text
리포지토리의 각각의 메서드를 살펴보면 상당히 많은 부분이 반복된다. 이런 반복을 효과적으로 처리하는 방법이 바로
템플릿 콜백 패턴이다.
스프링은 JDBC의 반복 문제를 해결하기 위해 JdbcTemplate 이라는 템플릿을 제공한다.
JdbcTemplate 에 대한 자세한 사용법은 뒤에서 설명하겠다. 지금은 전체 구조와, 이 기능을 사용해서 반복 코드를 제
거할 수 있다는 것에 초점을 맞추자.
```

### MemberRepositoryV5
```java
/**
 * JdbcTemplate
 */
@Slf4j
public class MemberRepositoryV5 implements MemberRepository{
    private final JdbcTemplate template;
    public MemberRepositoryV5(DataSource dataSource) {
         this.template = new JdbcTemplate(dataSource);
    }
    @Override
    public Member save(Member member){
        String sql = "insert into member(member_id, money) values (?, ?)";

        template.update(sql, member.getMemberId(), member.getMoney());
        return member;
    }
    @Override
    public Member findById(String memberId){
        String sql = "select * from member where member_id = ?";
        return template.queryForObject(sql, memberRowMapper(), memberId);
    }
    @Override
    public void update(String memberId, int money){
        String sql = "update member set money=? where member_id = ?";
        template.update(sql, money, memberId);
    }
    @Override
    public void delete(String memberId){
        String sql = "delete member where member_id = ?";
        template.update(sql, memberId);
    }
    public RowMapper<Member> memberRowMapper(){
       return (rs, rowNum) -> {
           Member member = new Member();
           member.setMemberId(rs.getString("member_id"));
           member.setMoney(rs.getInt("money"));
           return member;
       };
   }
}
```

### MemberServiceV4Test - 수정

```java
@Bean
MemberRepository memberRepository() {
    return new MemberRepositoryV5(dataSource); // JdbcTemplate
}
```

```text
JdbcTemplate 은 JDBC로 개발할 때 발생하는 반복을 대부분 해결해준다. 그 뿐만 아니라 지금까지 학습했던, 트랜
잭션을 위한 커넥션 동기화는 물론이고, 예외 발생시 스프링 예외 변환기도 자동으로 실행해준다
```

### 정리 

- 서비스 계층의 순수성
  - 트랜잭션 추상화 + 트랜잭션 AOP 덕분에 서비스 계층의 순수성을 최대한 유지하면서 서비스 계층에서 트랜잭션을 사용할 수 있다.
  - 스프링이 제공하는 예외 추상화와 예외 변환기 덕분에, 데이터 접근 기술이 변경되어도 서비스 계층의 순수성을 유지하면서 예외도 사용할 수 있다.
  - 서비스 계층이 리포지토리 인터페이스에 의존한 덕분에 향후 리포지토리가 다른 구현 기술로 변경되어도 서비스 계층을 순수하게 유지할 수 있다.
- 리포지토리에서 JDBC를 사용하는 반복 코드가 ``JdbcTemplate`` 으로 대부분 제거되었다.

