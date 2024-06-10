package spring.jdbc.connection;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection; // JDBC 제공 표준 인터페이스
import java.sql.DriverManager;
import java.sql.SQLException;

import static spring.jdbc.connection.ConnectionConst.*;

@Slf4j
public class DBConnectionUtil {
    public static Connection getConnection(){
        try {
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            log.info("get connection={}, class={}", connection, connection.getClass());
            return connection;
        }
        catch (SQLException e){
            log.error(e.getMessage());
            throw new IllegalStateException(e);
        }
    }
}
