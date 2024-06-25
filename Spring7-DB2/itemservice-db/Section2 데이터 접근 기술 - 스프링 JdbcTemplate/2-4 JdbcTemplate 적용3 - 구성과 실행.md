# JdbcTemplate 적용3 - 구성과 실행

### JdbcTemplateV1Config

```java
@Configuration
@RequiredArgsConstructor
public class JdbcTemplateV1Config {
    private final DataSource dataSource;
    @Bean
    public ItemService itemService() {
        return new ItemServiceV1(itemRepository());
    }
    @Bean
    public ItemRepository itemRepository() {
        return new JdbcTemplateItemRepositoryV1(dataSource);
    }
}
```
- ``ItemRepository``구현체로 ``JdbcTemplateItemRepositoryV1``이 사용되도록 했다

### ItemServiceApplication - 변경
```java
// @Import(MemoryConfig.class)
@Import(JdbcTemplateV1Config.class)
@SpringBootApplication(scanBasePackages = "hello.itemservice.web")
public class ItemServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ItemServiceApplication.class, args);
	}

	@Bean
	@Profile("local")
	public TestDataInit testDataInit(ItemRepository itemRepository) {
		return new TestDataInit(itemRepository);
	}
}
```

### 데이터베이스 접근 설정

```applcation.properties
spring.profiles.active=local

spring.datasource.url=jdbc:h2:tcp://localhost/~/test
spring.datasource.username=sa
spring.datasource.password=
```
- 이렇게 설정만 하면 스프링 부트가 해당 설정을 사용해서 커넥션 풀과 DataSource , 트랜잭션 매니저를 스프링빈으로 자동 등록한다.
  - 커넥션 풀: HikariCP 커넥션 풀(스프링 부트 Default 커넥션 풀)
  - 트랜잭션 매니저: DataSourceTransactionManager (JDBC 트랜잭션 관리)

로그 추가
```text
JdbcTemplate이 실행하는 SQL 로그를 확인하려면 application.properties 에 다음을 추가하면 된다. 
main , test 설정이 분리되어 있기 때문에 둘다 확인하려면 두 곳에 모두 추가해야 한다.

#jdbcTemplate sql log
logging.level.org.springframework.jdbc=debug
```