# JDBC 개발 - 등록

JDBC를 사용해서 회원( Member ) 데이터를 데이터베이스에 관리하는 기능을 개발해보자.

### schema.sql

```sql
drop table member if exists cascade;
create table member (
     member_id varchar(10),
     money integer not null default 0,
     primary key (member_id)
);
```

### Member

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Member {
    private String memberId;
    private int money;
}
```
- 회원의 ID와 해당 회원이 소지한 금액을 표현하는 단순한 클래스이다.
- 앞서 만들어둔 member 테이블에 데이터를 저장하고 조회할 때 사용한다.

### MemberRepositoryV0 - 회원 등록

```java
@Slf4j
public class MemberRepositoryV0 {
    public Member save(Member member) throws SQLException{
        String sql = "insert into member(member_id, money) values (?, ?)";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, member.getMemberId());
            pstmt.setInt(2, member.getMoney());
            int count = pstmt.executeUpdate(); // 영향을 받은 tuple(row) 숫자

            return member;
        }
        catch (SQLException e){
            log.error("db error", e.getMessage());
            throw e;
        }
        finally {
             close(con, pstmt, null);
        }
    }
    private void close(Connection con, Statement stmt, ResultSet rs) throws SQLException{
        if(rs != null){
           try {
               rs.close();
           }
           catch (SQLException e){
               log.error("db error", e);
               throw e;
           }
        }
        if(stmt != null){
            try {
                stmt.close();
            }
            catch (SQLException e){
                log.error("db error", e);
                throw e;
            }
        }
        if(con != null){
            try {
                con.close();
            }
            catch (SQLException e){
                log.error("db error", e);
                throw e;
            }
        }
    }
    private static Connection getConnection() {
        return DBConnectionUtil.getConnection();
    }
}
```

커넥션 획득
- getConnection() : 이전에 만들어둔 DBConnectionUtil 를 통해서 데이터베이스 커넥션을 획득한다.

save() - SQL 전달
- sql : 데이터베이스에 전달할 SQL을 정의한다. 여기서는 데이터를 등록해야 하므로 insert sql 을 준비했다
- con.prepareStatement(sql) : 데이터베이스에 전달할 SQL과 파라미터로 전달할 데이터들을 준비한다
   - sql : insert into member(member_id, money) values(?, ?)"
   - pstmt.setString(1, member.getMemberId()) : SQL의 첫번째 ? 에 값을 지정한다. 문자이므로 setString을 사용한다.
   - pstmt.setInt(2, member.getMoney()) : SQL의 두번째 ? 에 값을 지정한다. Int형 숫자이므로 setInt를 지정한다.
- pstmt.executeUpdate(): Statement 를 통해 준비된 SQL을 커넥션을 통해 실제 데이터베이스에 전달한다. 
- 참고로 executeUpdate() 은 int 를 반환하는데 영향받은 DB row 수를 반환한다.
  - 여기서는 하나의 row를 등록했으므로 1을 반환한다.

리소스 정리
```text
쿼리를 실행하고 나면 리소스를 정리해야 한다. 여기서는 Connection , PreparedStatement 를 사용했다. 리소
스를 정리할 때는 항상 역순으로 해야한다.
Connection 을 먼저 획득하고 Connection 을 통해
PreparedStatement 를 만들었기 때문에 리소스를 반환할 때는 PreparedStatement 를 먼저 종료하고, 그 다
음에 Connection 을 종료하면 된다. 참고로 여기서 사용하지 않은 ResultSet 은 결과를 조회할 때 사용한다.
```

❗️주의
```text
리소스 정리는 꼭! 해주어야 한다. 따라서 예외가 발생하든, 하지 않든 항상 수행되어야 하므로 finally 구문에
주의해서 작성해야한다. 만약 이 부분을 놓치게 되면 커넥션이 끊어지지 않고 계속 유지되는 문제가 발생할 수 있
다. 이런 것을 리소스 누수라고 하는데, 결과적으로 커넥션 부족으로 장애가 발생할 수 있다.
```

✅참고
```text
PreparedStatement 는 Statement 의 자식 타입인데, ? 를 통한 파라미터 바인딩을 가능하게 해준다.
참고로 SQL Injection 공격을 예방하려면 PreparedStatement 를 통한 파라미터 바인딩 방식을 사용해야
한다.
```

### MemberRepositoryV0Test - 회원 등록

```java
class MemberRepositoryV0Test {
    MemberRepositoryV0 repository = new MemberRepositoryV0();
    @Test
    void crud() throws SQLException {
        Member member = new Member("memberV0", 10000);
        Member saveMember = repository.save(member);
        assertThat(saveMember).isSameAs(member);
    }
}
```

실행 결과
- 데이터베이스에서 select * from member 쿼리를 실행하면 데이터가 저장된 것을 확인할 수 있다.
- 참고로 이 테스트는 2번 실행하면 PK 중복 오류가 발생한다. 
