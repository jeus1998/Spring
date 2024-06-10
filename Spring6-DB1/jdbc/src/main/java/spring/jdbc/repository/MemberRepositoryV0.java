package spring.jdbc.repository;

import lombok.extern.slf4j.Slf4j;
import spring.jdbc.connection.DBConnectionUtil;
import spring.jdbc.domain.Member;

import java.sql.*;
import java.util.NoSuchElementException;

/**
 * JDBC- DriverManager 사용
 * PrepareStatement <- Statement 자식 파라미터 바인딩 기능 추가
 */
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

    /**
     * 라소스 정리: 항상 역순으로 해야한다. con -> pstmt -> rs 정리: rs -> pstmt -> con
     */
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
