# DataSource 예제2 - 커넥션 풀

이번에는 DataSource 를 통해 커넥션 풀을 사용하는 예제를 알아보자.

### ConnectionTest - 데이터소스 커넥션 풀 추가

```java
@Slf4j
class ConnectionTest {
  
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
```
- HikariCP 커넥션 풀을 사용한다. HikariDataSource 는 DataSource 인터페이스를 구현하고 있다.
- 커넥션 풀 최대 사이즈를 10으로 지정하고, 풀의 이름을 ``OceanWorld`` 이라고 지정했다.
- 커넥션 풀에서 커넥션을 생성하는 작업은 애플리케이션 실행 속도에 영향을 주지 않기 위해 별도의 쓰레드에서 작동한다
- 별도의 쓰레드에서 동작하기 때문에 테스트가 먼저 종료되어 버린다.
- 예제처럼 Thread.sleep 을 통해 대기 시간을 주어야 쓰레드 풀에 커넥션이 생성되는 로그를 확인할 수 있다.

실행 결과
```text
#커넥션 풀 초기화 정보 출력
14:46:51.318 [Test worker] DEBUG com.zaxxer.hikari.HikariConfig -- OceanWorld - configuration:
14:46:51.348 [Test worker] DEBUG com.zaxxer.hikari.HikariConfig -- allowPoolSuspension.............false
14:46:51.349 [Test worker] DEBUG com.zaxxer.hikari.HikariConfig -- autoCommit......................true
14:46:51.350 [Test worker] DEBUG com.zaxxer.hikari.HikariConfig -- catalog.........................none
14:46:51.352 [Test worker] DEBUG com.zaxxer.hikari.HikariConfig -- connectionInitSql...............none
14:46:51.353 [Test worker] DEBUG com.zaxxer.hikari.HikariConfig -- connectionTestQuery.............none
14:46:51.354 [Test worker] DEBUG com.zaxxer.hikari.HikariConfig -- connectionTimeout...............30000
14:46:51.355 [Test worker] DEBUG com.zaxxer.hikari.HikariConfig -- dataSource......................none
14:46:51.355 [Test worker] DEBUG com.zaxxer.hikari.HikariConfig -- dataSourceClassName.............none
14:46:51.356 [Test worker] DEBUG com.zaxxer.hikari.HikariConfig -- dataSourceJNDI..................none
14:46:51.358 [Test worker] DEBUG com.zaxxer.hikari.HikariConfig -- dataSourceProperties............{password=<masked>}
14:46:51.359 [Test worker] DEBUG com.zaxxer.hikari.HikariConfig -- driverClassName.................none
14:46:51.361 [Test worker] DEBUG com.zaxxer.hikari.HikariConfig -- exceptionOverrideClassName......none
14:46:51.362 [Test worker] DEBUG com.zaxxer.hikari.HikariConfig -- healthCheckProperties...........{}
14:46:51.363 [Test worker] DEBUG com.zaxxer.hikari.HikariConfig -- healthCheckRegistry.............none
14:46:51.364 [Test worker] DEBUG com.zaxxer.hikari.HikariConfig -- idleTimeout.....................600000
14:46:51.367 [Test worker] DEBUG com.zaxxer.hikari.HikariConfig -- initializationFailTimeout.......1
14:46:51.369 [Test worker] DEBUG com.zaxxer.hikari.HikariConfig -- isolateInternalQueries..........false
14:46:51.371 [Test worker] DEBUG com.zaxxer.hikari.HikariConfig -- jdbcUrl.........................jdbc:h2:tcp://localhost/~/test
14:46:51.372 [Test worker] DEBUG com.zaxxer.hikari.HikariConfig -- keepaliveTime...................0
14:46:51.373 [Test worker] DEBUG com.zaxxer.hikari.HikariConfig -- leakDetectionThreshold..........0
14:46:51.374 [Test worker] DEBUG com.zaxxer.hikari.HikariConfig -- maxLifetime.....................1800000
14:46:51.375 [Test worker] DEBUG com.zaxxer.hikari.HikariConfig -- maximumPoolSize.................10
14:46:51.376 [Test worker] DEBUG com.zaxxer.hikari.HikariConfig -- metricRegistry..................none
14:46:51.377 [Test worker] DEBUG com.zaxxer.hikari.HikariConfig -- metricsTrackerFactory...........none
14:46:51.377 [Test worker] DEBUG com.zaxxer.hikari.HikariConfig -- minimumIdle.....................10
14:46:51.377 [Test worker] DEBUG com.zaxxer.hikari.HikariConfig -- password........................<masked>
14:46:51.378 [Test worker] DEBUG com.zaxxer.hikari.HikariConfig -- poolName........................"OceanWorld"
14:46:51.381 [Test worker] DEBUG com.zaxxer.hikari.HikariConfig -- readOnly........................false
14:46:51.381 [Test worker] DEBUG com.zaxxer.hikari.HikariConfig -- registerMbeans..................false
14:46:51.383 [Test worker] DEBUG com.zaxxer.hikari.HikariConfig -- scheduledExecutor...............none
14:46:51.383 [Test worker] DEBUG com.zaxxer.hikari.HikariConfig -- schema..........................none
14:46:51.384 [Test worker] DEBUG com.zaxxer.hikari.HikariConfig -- threadFactory...................internal
14:46:51.385 [Test worker] DEBUG com.zaxxer.hikari.HikariConfig -- transactionIsolation............default
14:46:51.386 [Test worker] DEBUG com.zaxxer.hikari.HikariConfig -- username........................"sa"
14:46:51.388 [Test worker] DEBUG com.zaxxer.hikari.HikariConfig -- validationTimeout...............5000
14:46:51.388 [Test worker] INFO  com.zaxxer.hikari.HikariDataSource -- OceanWorld - Starting...
14:46:51.415 [Test worker] DEBUG c.z.hikari.util.DriverDataSource -- Loaded driver with class name org.h2.Driver for jdbcUrl=jdbc:h2:tcp://localhost/~/test

#커넥션 풀 전용 쓰레드가 커넥션 풀에 커넥션을 (누적)1개 채움
14:46:51.700 [Test worker] INFO  com.zaxxer.hikari.pool.HikariPool -- OceanWorld - Added connection conn0: url=jdbc:h2:tcp://localhost/~/test user=SA
14:46:51.708 [Test worker] INFO  com.zaxxer.hikari.HikariDataSource -- OceanWorld - Start completed.

#커넥션 풀 전용 쓰레드가 커넥션 풀에 커넥션을 (누적)2개 채움
14:46:51.733 [OceanWorld connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool -- OceanWorld - Added connection conn1: url=jdbc:h2:tcp://localhost/~/test user=SA
#커넥션 풀에서 커넥션 획득1
14:46:51.733 [Test worker] INFO  s.jdbc.connection.ConnectionTest -- connection=HikariProxyConnection@321772459 wrapping conn0: url=jdbc:h2:tcp://localhost/~/test user=SA, class=class com.zaxxer.hikari.pool.HikariProxyConnection
#커넥션 풀에서 커넥션 획득2
14:46:51.744 [Test worker] INFO  s.jdbc.connection.ConnectionTest -- connection=HikariProxyConnection@1667534569 wrapping conn1: url=jdbc:h2:tcp://localhost/~/test user=SA, class=class com.zaxxer.hikari.pool.HikariProxyConnection
14:46:51.772 [OceanWorld connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool -- OceanWorld - Connection not added, stats (total=2, active=2, idle=0, waiting=0)
14:46:51.821 [OceanWorld housekeeper] DEBUG com.zaxxer.hikari.pool.HikariPool -- OceanWorld - Pool stats (total=2, active=2, idle=0, waiting=0)

#커넥션 풀 전용 쓰레드가 커넥션 풀에 커넥션을 나머지 8개 - 총 10개 채움
14:46:51.829 [OceanWorld connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool -- OceanWorld - Added connection conn2: url=jdbc:h2:tcp://localhost/~/test user=SA
14:46:51.868 [OceanWorld connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool -- OceanWorld - After adding stats (total=3, active=2, idle=1, waiting=0)
14:46:51.877 [OceanWorld connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool -- OceanWorld - Added connection conn3: url=jdbc:h2:tcp://localhost/~/test user=SA
14:46:51.916 [OceanWorld connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool -- OceanWorld - After adding stats (total=4, active=2, idle=2, waiting=0)
14:46:51.926 [OceanWorld connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool -- OceanWorld - Added connection conn4: url=jdbc:h2:tcp://localhost/~/test user=SA
14:46:51.962 [OceanWorld connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool -- OceanWorld - After adding stats (total=5, active=2, idle=3, waiting=0)
14:46:51.973 [OceanWorld connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool -- OceanWorld - Added connection conn5: url=jdbc:h2:tcp://localhost/~/test user=SA
14:46:52.010 [OceanWorld connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool -- OceanWorld - After adding stats (total=6, active=2, idle=4, waiting=0)
14:46:52.021 [OceanWorld connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool -- OceanWorld - Added connection conn6: url=jdbc:h2:tcp://localhost/~/test user=SA
14:46:52.057 [OceanWorld connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool -- OceanWorld - After adding stats (total=7, active=2, idle=5, waiting=0)
14:46:52.068 [OceanWorld connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool -- OceanWorld - Added connection conn7: url=jdbc:h2:tcp://localhost/~/test user=SA
14:46:52.104 [OceanWorld connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool -- OceanWorld - After adding stats (total=8, active=2, idle=6, waiting=0)
14:46:52.116 [OceanWorld connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool -- OceanWorld - Added connection conn8: url=jdbc:h2:tcp://localhost/~/test user=SA
14:46:52.150 [OceanWorld connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool -- OceanWorld - After adding stats (total=9, active=2, idle=7, waiting=0)
14:46:52.159 [OceanWorld connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool -- OceanWorld - Added connection conn9: url=jdbc:h2:tcp://localhost/~/test user=SA
14:46:52.198 [OceanWorld connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool -- OceanWorld - After adding stats (total=10, active=2, idle=8, waiting=0)
14:46:52.198 [OceanWorld connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool -- OceanWorld - Connection not added, stats (total=10, active=2, idle=8, waiting=0)
14:46:52.198 [OceanWorld connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool -- OceanWorld - Connection not added, stats (total=10, active=2, idle=8, waiting=0)
```

HikariConfig
- HikariCP 관련 설정을 확인할 수 있다. 풀의 이름(OceanWorld)과 최대 풀 수(10)을 확인할 수 있다.

OceanWorld connection adder
- 별도의 쓰레드 사용해서 커넥션 풀에 커넥션을 채우고 있는 것을 확인할 수 있다.
  - 로그를 확인하니까 처음 1개는 커넥션 풀에 기존 쓰레드가 채우고 있다.
- 이 쓰레드는 커넥션 풀에 커넥션을 최대 풀 수(10)까지 채운다.
  - 나머지 9개를 채운다
- 그렇다면 왜 별도의 쓰레드를 사용해서 커넥션 풀에 커넥션을 채우는 것일까?
- 커넥션 풀에 커넥션을 채우는 것은 상대적으로 오래 걸리는 일이다.
- 애플리케이션을 실행할 때 커넥션 풀을 채울 때 까지 마냥 대기하고 있다면 애플리케이션 실행 시간이 늦어진다.
- 따라서 이렇게 별도의 쓰레드를 사용해서 커넥션 풀을 채워야 애플리케이션 실행 시간에 영향을 주지 않는다.
  - 멀티 쓰레딩(커넥션 풀을 채우는 작업이 너무 오래 걸리니까 CPU가 쓰레드를 1개 만들어서 컨텍스트 스위칭을 통한 작업을 하도록함)
  - 이 멀티 쓰레딩이 시작하게된 이유는 HikariCP 라이브러리를 만든 사람이 쓰레드를 만들어서 실행하도록 한 것이고 
    멀티쓰레딩 관리 자체는 OS(CPU)가 알아서 CPU스케줄링을 통해서 관리 

커넥션 풀에서 커넥션 획득
- 커넥션 풀에서 커넥션을 2개 획득하고 반환하지는 않았다.
- 따라서 풀에 있는 10개의 커넥션 중에 2개를 가지고 있는 상태이다.
- 그래서 마지막 로그를 보면 사용중인 커넥션 active=2 , 풀에서 대기 상태인 커넥션 idle=8 을 확인할 수 있다.
- ``OceanWorld - Connection not added, stats (total=10, active=2, idle=8, waiting=0)``

참고
- HikariCP 커넥션 풀에 대한 더 자세한 내용은 다음 공식 사이트를 참고하자.
- https://github.com/brettwooldridge/HikariCP

### 스프링 부트 3.1 이상 - 로그 출력 안되는 문제 해결

히카리 커넥션 풀을 테스트하는 dataSourceConnectionPool() 을 실행할 때, 스프링 부트 3.1 이상을 사용한다
면 전체 로그가 아니라 간략한 로그만 출력된다.

- src/main/resources/logback.xml
```xml
<configuration>
 <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
 <encoder>
 <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} -%kvp- %msg%n</pattern>
 </encoder>
 </appender>
 <root level="DEBUG">
 <appender-ref ref="STDOUT" />
 </root>
</configuration>
```