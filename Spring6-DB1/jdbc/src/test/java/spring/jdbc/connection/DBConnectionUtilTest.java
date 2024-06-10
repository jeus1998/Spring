package spring.jdbc.connection;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

import java.sql.Connection;

class DBConnectionUtilTest {
    @Test
    void getConnection() {
        Connection connection = DBConnectionUtil.getConnection();
        assertThat(connection).isNotNull();
    }
}