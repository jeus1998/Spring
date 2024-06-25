# JdbcTemplate - SimpleJdbcInsert

- JdbcTemplate은 INSERT SQL를 직접 작성하지 않아도 되도록 ``SimpleJdbcInsert``라는 편리한 기능을 제공한다.

### JdbcTemplateItemRepositoryV3

```java
/**
 * SimpleJdbcInsert
 */
@Slf4j
public class JdbcTemplateItemRepositoryV3 implements ItemRepository {
    private final NamedParameterJdbcTemplate template;
    private final SimpleJdbcInsert jdbcInsert;
    public JdbcTemplateItemRepositoryV3(DataSource dataSource) {
        this.template = new NamedParameterJdbcTemplate(dataSource);
        this.jdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("item")
                .usingGeneratedKeyColumns("id");
            //  .usingColumns("item_name", "price", "quantity");  생략 가능
    }

    @Override
    public Item save(Item item) {
        SqlParameterSource param = new BeanPropertySqlParameterSource(item);
        long id = jdbcInsert.executeAndReturnKey(param).longValue();
        item.setId(id);
        return item;
    }

    @Override
    public void update(Long itemId, ItemUpdateDto updateParam) {
        String sql = "update item " +
                     "set item_name=:itemName, price=:price, quantity=:quantity " +
                     "where id=:id";

        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("itemName", updateParam.getItemName())
                .addValue("price", updateParam.getPrice())
                .addValue("quantity", updateParam.getQuantity())
                .addValue("id", itemId);

        template.update(sql, param);
    }

    @Override
    public Optional<Item> findById(Long id) {
        String sql = "select id, item_name, price, quantity from item where id= :id";
        try {
            Map<String, Object> param = Map.of("id", id);
            Item item = template.queryForObject(sql, param, itemRowMapper());
            return Optional.of(item);
        }
        catch (EmptyResultDataAccessException e){
            return Optional.empty();
        }
    }

    @Override
    public List<Item> findAll(ItemSearchCond cond) {
        String itemName = cond.getItemName();
        Integer maxPrice = cond.getMaxPrice();

        String sql = "select id, item_name, price, quantity from item";

        SqlParameterSource param = new BeanPropertySqlParameterSource(cond);
        // 동적쿼리
        if(StringUtils.hasText(itemName) || maxPrice != null){
            sql += " where";
        }

        boolean andFlag = false;
        if(StringUtils.hasText(itemName)){
            sql += " item_name like concat ('%',:itemName,'%')";
            andFlag = true;
        }

        if(maxPrice != null){
            if(andFlag){
                sql += " and";
            }
            sql += " price <= :maxPrice";
        }

        log.info("sql={}", sql);
        return template.query(sql, param, itemRowMapper());
    }
    private RowMapper<Item> itemRowMapper() {
       return BeanPropertyRowMapper.newInstance(Item.class);
    }
}
```

SimpleJdbcInsert
```java
 this.jdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("item")
                .usingGeneratedKeyColumns("id");
            //  .usingColumns("item_name", "price", "quantity");  생략 가능
```
- ``withTableName``: 데이터를 저장할 테이블 명을 지정한다.
- ``usingGeneratedKeyColumns``: key 를 생성하는 PK 컬럼 명을 지정한다.
- ``usingColumns``: INSERT SQL에 사용할 컬럼을 지정한다. 특정 값만 저장하고 싶을 때 사용한다. 생략할 수 있다.
- ``SimpleJdbcInsert``는 생성 시점에 데이터베이스 테이블의 메타 데이터를 조회한다.
  - 따라서 어떤 컬럼이 있는지 확인 할 수 있으므로 ``usingColumns``을 생략할 수 있다. 
  - 만약 특정 컬럼만 지정해서 저장하고 싶다면 ``usingColumns``를 사용하면 된다.


save()
- ``jdbcInsert.executeAndReturnKey(param)``을 사용해서 INSERT SQL을 실행하고, 생성된 키 값도 매우 편리하게 조회할 수 있다.
```java
public Item save(Item item) {
     SqlParameterSource param = new BeanPropertySqlParameterSource(item);
     Number key = jdbcInsert.executeAndReturnKey(param);
     item.setId(key.longValue());
     return item;
}
```

### 적용 
```java
@Configuration
@RequiredArgsConstructor
public class JdbcTemplateV3Config {
    private final DataSource dataSource;
    @Bean
    public ItemService itemService() {
        return new ItemServiceV1(itemRepository());
    }
    @Bean
    public ItemRepository itemRepository() {
        return new JdbcTemplateItemRepositoryV3(dataSource);
    }
}
```

ItemServiceApplication - 변경
```java
@Import(JdbcTemplateV3Config.class)
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