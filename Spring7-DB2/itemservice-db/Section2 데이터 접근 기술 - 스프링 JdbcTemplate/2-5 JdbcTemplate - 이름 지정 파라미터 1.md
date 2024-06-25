# JdbcTemplate - 이름 지정 파라미터 1

### 순서대로 바인딩

JdbcTemplate을 기본으로 사용하면 파라미터를 순서대로 바인딩 한다.
```java
String sql = "update item set item_name=?, price=?, quantity=? where id=?";
template.update(sql,
               itemName,
               price,
               quantity,
               itemId);
```
- 여기서는 ``itemName , price , quantity, itemId``가 SQL에 있는 ``?``에 순서대로 바인딩 된다.
- 따라서 순서만 잘 지키면 문제가 될 것은 없다. 그런데 문제는 변경시점에 발생한다.


누군가 다음과 같이 SQL 코드의 순서를 변경했다고 가정해보자. ( price 와 quantity 의 순서를 변경했다.)
```java
String sql = "update item set item_name=?, quantity=?, price=? where id=?";
template.update(sql,
               itemName,
               price,
               quantity,
               itemId);
```
- 이렇게 되면 다음과 같은 순서로 데이터가 바인딩 된다.
- item_name=itemName, quantity=price, price=quantity

```text
결과적으로 price 와 quantity 가 바뀌는 매우 심각한 문제가 발생한다. 이럴일이 없을 것 같지만, 실무에서는 파라
미터가 10~20개가 넘어가는 일도 아주 많다. 그래서 미래에 필드를 추가하거나, 수정하면서 이런 문제가 충분히 발생
할 수 있다. 

버그 중에서 가장 고치기 힘든 버그는 데이터베이스에 데이터가 잘못 들어가는 버그다. 이것은 코드만 고치는 수준이 아
니라 데이터베이스의 데이터를 복구해야 하기 때문에 버그를 해결하는데 들어가는 리소스가 어마어마하다.

개발을 할 때는 코드를 몇줄 줄이는 편리함도 중요하지만, 모호함을 제거해서 코드를 명확하게 만드는 것이 유지보수 관
점에서 매우 중요하다.

이처럼 파라미터를 순서대로 바인딩 하는 것은 편리하기는 하지만, 순서가 맞지 않아서 버그가 발생할 수도 있으므로 주
의해서 사용해야 한다.
```

### 이름 지정 바인딩

```text
JdbcTemplate은 이런 문제를 보완하기 위해 NamedParameterJdbcTemplate 라는 이름을 지정해서 파라미터를
바인딩 하는 기능을 제공한다.
```

JdbcTemplateItemRepositoryV2
```java
/**
 * NamedParameterJdbcTemplate
 * SqlParameterSource
 * - BeanPropertyParameterSource
 * - MapSqlParameterSource
 * Map
 * BeanPropertyRowMapper -> newInstance(Item.class)
 */
@Slf4j
public class JdbcTemplateItemRepositoryV2 implements ItemRepository {
    private final NamedParameterJdbcTemplate template;
    public JdbcTemplateItemRepositoryV2(DataSource dataSource) {
        this.template = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public Item save(Item item) {
        String sql = "insert into item(item_name, price, quantity) " +
                "values (:itemName, :price , :quantity)";

        // 데이터베이스가 identity 전략으로 자동 생성한 primary key를 받기 위해 필요
        KeyHolder keyHolder = new GeneratedKeyHolder();

        SqlParameterSource param = new BeanPropertySqlParameterSource(item);
        template.update(sql, param, keyHolder);

        long key = keyHolder.getKey().longValue();
        item.setId(key);
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

기본
- ``this.template = new NamedParameterJdbcTemplate(dataSource)``
  - ``NamedParameterJdbcTemplate``도 내부에 ``dataSource``가 필요하다.

save()
- SQL에서 다음과 같이 ? 대신에 :파라미터이름 을 받는 것을 확인할 수 있다.
```text
insert into item (item_name, price, quantity) " + "values (:itemName, :price, :quantity)"
```
- 추가로 ``NamedParameterJdbcTemplate``은 데이터베이스가 생성해주는 키를 매우 쉽게 조회하는 기능도 제공해준다.