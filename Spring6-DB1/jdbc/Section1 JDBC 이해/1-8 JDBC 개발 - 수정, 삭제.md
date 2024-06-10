
# JDBC 개발 - 수정, 삭제

### MemberRepositoryV0 - 회원 수정 추가
```java
public void update(String memberId, int money) throws SQLException{
        String sql = "update member set money=? where  member_id = ?";

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
```

### MemberRepositoryV0Test - 회원 수정 추가
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

        // update
        repository.update(findMember.getMemberId(), 20000);
        Member afterUpdate = repository.findById(findMember.getMemberId());
        assertThat(afterUpdate.getMoney()).isEqualTo(20000);
    }
}
```

### MemberRepositoryV0 - 회원 삭제 추가
```java
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
```

### MemberRepositoryV0Test - 회원 삭제 추가
```java
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
```
