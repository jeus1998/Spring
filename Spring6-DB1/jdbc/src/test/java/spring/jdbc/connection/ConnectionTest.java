package spring.jdbc.connection;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.*;
import static spring.jdbc.connection.ConnectionConst.*;

@Slf4j
class ConnectionTest {
    @Test
    void driverManager() throws SQLException {
        Connection con1 = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        Connection con2 = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        log.info("connection={}, class={}", con1, con1.getClass());
        log.info("connection={}, class={}", con2, con2.getClass());
        assertThat(con1).isNotSameAs(con2);
    }
    @Test
    void dataSourceDriverManager() throws SQLException{
        // DriverManagerDataSource - 항상 새로운 커넥션 획득
        DataSource datasource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        useDataSource(datasource);
    }

    @Test
    void dataSourceConnectionPool() throws SQLException, InterruptedException{
        // 커넥션 풀링
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);
        dataSource.setMaximumPoolSize(10);
        dataSource.setPoolName("OceanWorld");

        useDataSource(dataSource);
        Thread.sleep(1000); // 커넥션 풀에서 커넥션 생성 대기
    }

    private void useDataSource(DataSource dataSource) throws SQLException{
        Connection con1 = dataSource.getConnection();
        Connection con2 = dataSource.getConnection();
        log.info("connection={}, class={}", con1, con1.getClass());
        log.info("connection={}, class={}", con2, con2.getClass());
    }
}
