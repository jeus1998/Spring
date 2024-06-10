# JDBC 개발 - 조회

이번에는 JDBC를 통해 이전에 저장한 데이터를 조회하는 기능을 개발해보자.

### MemberRepositoryV0 - 회원 조회 추가
```java
public Member findById(String memberId) throws SQLException {

    String sql = "select * from member where member_id = ?";

    Connection con = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {
        con = getConnection();
        pstmt = con.prepareStatement(sql);
        pstmt.setString(1, memberId);
        rs = pstmt.executeQuery();
        if(rs.next()){
            Member member = new Member();
            member.setMemberId(rs.getString("member_id"));
            member.setMoney(rs.getInt("money"));
            return member;
        }
        else{
            throw new NoSuchElementException("member not found memberId=" + memberId);
        }
    }
    catch (SQLException e){
        log.error("db error", e);
        throw  e;
    }
    finally {
        close(con,pstmt,rs);
    }
}
```
findById() - 쿼리 실행
- sql : 데이터 조회를 위한 select SQL을 준비한다.
- ``rs = pstmt.executeQuery()``
  - 데이터를 변경할 때는 ``executeUpdate()``를 사용
  - 데이터를 조회 할 때는 ``executeQuery()``를 사용
  - executeQuery()는 결과를 ResultSet 에 담아서 반환

ResultSet
- select 쿼리의 결과가 순서대로 들어간다.
  - 예를 들어서 select member_id, money 라고 지정
  - member_id , money 라는 이름으로 데이터가 저장
- ResultSet 내부에 있는 커서(cursor)를 이동해서 다음 데이터를 조회할 수 있다
- ``rs.next()``:이것을 호출하면 커서가 다음으로 이동한다.
- 참고로 최초의 커서는 데이터를 가리키고 있지 않다.
- ``rs.next()``를 최초 한번은 호출해야 데이터를 조회할 수 있다.
  - ``rs.next()``의 결과가 true 면 커서의 이동 결과 데이터가 있다는 뜻이다.
  - ``rs.next()``의 결과가 false 면 더이상 커서가 가리키는 데이터가 없다는 뜻이다.
- ``rs.getString("member_id")``:현재 커서가 가리키고 있는 위치의 member_id 데이터를 String 타입으로 반환한다.
- ``rs.getInt("money")``:현재 커서가 가리키고 있는 위치의 money 데이터를 int 타입으로 반환한다.

![12.JPG](Image%2F12.JPG)

- findById()에서는 회원 하나를 조회하는 것이 목적이다.
- 따라서 조회 결과가 항상 1건이므로 while 대신에 if를 사용한다.

### MemberRepositoryV0Test - 회원 조회 추가

```java
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
    }
}
```

실행 결과
- ```MemberRepositoryV0Test - findMember=Member(memberId=memberV0, money=10000)```
- 회원을 등록하고 그 결과를 바로 조회해서 확인해보았다.
- 참고로 실행 결과에 member 객체의 참조 값이 아니라 실제 데이터가 보이는 이유는 롬복의 @Data가 
  toString() 을 적절히 오버라이딩 해서 보여주기 때문이다.
- isEqualTo() : findMember.equals(member) 를 비교한다.
- 결과가 참인 이유는 롬복의 @Data는 해당 객체의 모든 필드를 사용하도록 equals()를 오버라이딩 하기 때문이다.