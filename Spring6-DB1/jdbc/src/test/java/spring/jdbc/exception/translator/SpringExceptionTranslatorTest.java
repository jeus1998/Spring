package spring.jdbc.exception.translator;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static spring.jdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.*;

/**
 * 스프링이 제공하는 SQL 예외 변환기
 */
@Slf4j
public class SpringExceptionTranslatorTest{
    DataSource dataSource;
    @BeforeEach
    void init(){
       dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
    }
    @Test
    void sqlExceptionErrorCode(){
        String sql = "select bad grammer";

        try {
            Connection con = dataSource.getConnection();
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.executeQuery();
        }
        catch (SQLException e){
            assertThat(e.getErrorCode()).isEqualTo(42122);
            log.info("errorCode={}", e.getErrorCode());

            //org.h2.jdbc.JdbcSQLSyntaxErrorException
            log.info("error", e);
        }
    }

    @Test
    void exceptionTranslator(){
        String sql = "select bad grammar";

        try {
            Connection con = dataSource.getConnection();
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.executeQuery();
        }
        catch (SQLException e){
            assertThat(e.getErrorCode()).isEqualTo(42122);
            //org.springframework.jdbc.support.sql-error-codes.xml

            // 스프링이 제공하는 SQL 예외 변환기
            SQLExceptionTranslator exTranslator = new SQLErrorCodeSQLExceptionTranslator(dataSource);

            //org.springframework.jdbc.BadSqlGrammarException
            DataAccessException resultEx = exTranslator.translate("select", sql, e);
            log.info("resultEx", resultEx);

            assertThat(resultEx.getClass()).isSameAs(BadSqlGrammarException.class);
        }
    }

}
