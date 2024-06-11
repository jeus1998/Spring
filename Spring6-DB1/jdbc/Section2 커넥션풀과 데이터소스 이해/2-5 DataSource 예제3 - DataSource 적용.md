# DataSource 예제3 - DataSource 적용

이번에는 애플리케이션에 DataSource 를 적용해보자.

### MemberRepositoryV1

```java
/**
 * JDBC- DataSource 사용, JdbcUtils 사용
 */
@Slf4j
@RequiredArgsConstructor
public class MemberRepositoryV1 {

    private final DataSource dataSource;

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
    public void update(String memberId, int money) throws SQLException{
        String sql = "update member set money=? where member_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, money);
            pstmt.setString(2, memberId);
            pstmt.executeUpdate();
        }
        catch (SQLException e){
            log.error("db error", e);
            throw e;
        }
        finally {
            close(con, pstmt, rs);
        }
    }

    public void delete(String memberId) throws SQLException{
        String sql = "delete member where member_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);
            pstmt.executeUpdate();
        }
        catch (SQLException e){
            log.error("db error", e);
            throw e;
        }
        finally {
            close(con, pstmt, rs);
        }
    }

    /**
     * 라소스 정리: 항상 역순으로 해야한다. con -> pstmt -> rs 정리: rs -> pstmt -> con
     */
    private void close(Connection con, Statement stmt, ResultSet rs) throws SQLException{
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
        JdbcUtils.closeConnection(con);
    }
    private Connection getConnection() throws SQLException{
        Connection con = dataSource.getConnection();
        log.info("get connection={}, class={}", con, con.getClass());
        return con;
    }
}
```

- DataSource 의존관계 주입
  - 외부에서 DataSource 를 주입 받아서 사용한다. 이제 직접 만든 DBConnectionUtil 을 사용하지 않아도 된다.
  - DataSource 는 표준 인터페이스 이기 때문에 DriverManagerDataSource 에서 HikariDataSource 로 변경되어도 
    해당 코드를 변경하지 않아도 된다.
- JdbcUtils 편의 메서드
  - 스프링은 JDBC를 편리하게 다룰 수 있는 JdbcUtils 라는 편의 메서드를 제공한다.
  - JdbcUtils 을 사용하면 커넥션을 좀 더 편리하게 닫을 수 있다.
  - JdbcUtils.closeResultSet(rs);
  - JdbcUtils.closeStatement(stmt);
  - JdbcUtils.closeConnection(con);

### MemberRepositoryV1Test

```java
@Slf4j
class MemberRepositoryV1Test {
    MemberRepositoryV1 repository;

    @BeforeEach
    void beforeEach(){
        // 기본 DriverManager - 항상 새로운 커넥션을 획득
        // DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);

        // 커넥션 풀링
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setPoolName("MyPool");
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);
        dataSource.setMaximumPoolSize(10);
        repository = new MemberRepositoryV1(dataSource);
    }

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

        // update
        repository.update(findMember.getMemberId(), 20000);
        Member afterUpdate = repository.findById(findMember.getMemberId());
        assertThat(afterUpdate.getMoney()).isEqualTo(20000);

        // delete
        repository.delete(afterUpdate.getMemberId());
        assertThatThrownBy(()-> repository.findById(afterUpdate.getMemberId()))
                .isInstanceOf(NoSuchElementException.class);
    }
}
```
- MemberRepositoryV1 은 DataSource 의존관계 주입이 필요하다.

DriverManagerDataSource 사용

```text
get connection=conn0: url=jdbc:h2:.. user=SA class=class org.h2.jdbc.JdbcConnection
get connection=conn1: url=jdbc:h2:.. user=SA class=class org.h2.jdbc.JdbcConnection
get connection=conn2: url=jdbc:h2:.. user=SA class=class org.h2.jdbc.JdbcConnection
get connection=conn3: url=jdbc:h2:.. user=SA class=class org.h2.jdbc.JdbcConnection
get connection=conn4: url=jdbc:h2:.. user=SA class=class org.h2.jdbc.JdbcConnection
get connection=conn5: url=jdbc:h2:.. user=SA class=class org.h2.jdbc.JdbcConnection
```
- DriverManagerDataSource 를 사용하면 conn0~5 번호를 통해서 항상 새로운 커넥션이 생성되어서 
  사용 되는 것을 확인할 수 있다.


HikariDataSource 사용
```text
get connection=HikariProxyConnection@xxxxxxxx1 wrapping conn0: url=jdbc:h2:... user=SA
get connection=HikariProxyConnection@xxxxxxxx2 wrapping conn0: url=jdbc:h2:... user=SA
get connection=HikariProxyConnection@xxxxxxxx3 wrapping conn0: url=jdbc:h2:... user=SA
get connection=HikariProxyConnection@xxxxxxxx4 wrapping conn0: url=jdbc:h2:... user=SA
get connection=HikariProxyConnection@xxxxxxxx5 wrapping conn0: url=jdbc:h2:... user=SA
get connection=HikariProxyConnection@xxxxxxxx6 wrapping conn0: url=jdbc:h2:... user=SA
```
- 커넥션 풀 사용시 conn0 커넥션이 재사용 된 것을 확인할 수 있다.
- 테스트는 순서대로 실행되기 때문에 커넥션을 사용하고 다시 돌려주는 것을 반복한다. 
- 따라서 conn0 만 사용된다.
- 웹 애플리케이션에 동시에 여러 요청이 들어오면 여러 쓰레드에서 커넥션 풀의 커넥션을 다양하게 가져가는 
  상황을 확인할 수 있다.

DI
- DriverManagerDataSource ➡️ HikariDataSource 로 변경해도 MemberRepositoryV1 의 코드는 
  전혀 변경하지 않아도 된다. 
- MemberRepositoryV1 는 DataSource 인터페이스에만 의존하기 때문이다.
- 이것이 DataSource 를 사용하는 장점이다.(DI + OCP)

### 정리
```text
현재 동작을 보면 JdbcUtils를 통해서 ResultSet, Statement, Connection을 다 닫고 있다.
서로다른 DataSource 구현체에 맞게 JdbcUtils는 동작한다.
Connection이 HikariCP의 커넥션 프록시 객체라면 커넥션 풀로 반환
Connection이 DriverManagerDataSource 객체라면 커넥션 종료
```