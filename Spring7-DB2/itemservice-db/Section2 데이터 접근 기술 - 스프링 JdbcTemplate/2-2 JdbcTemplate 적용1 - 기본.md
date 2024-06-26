# JdbcTemplate 적용1 - 기본

### JdbcTemplateItemRepositoryV1

```java
/**
 * JdbcTemplate
 */
@Slf4j
public class JdbcTemplateItemRepositoryV1 implements ItemRepository {
    private final JdbcTemplate template;
    public JdbcTemplateItemRepositoryV1(DataSource dataSource) {
        this.template = new JdbcTemplate(dataSource);
    }

    @Override
    public Item save(Item item) {
        String sql = "insert into item(item_name, price, quantity) values (?,?,?)";

        // 데이터베이스가 identity 전략으로 자동 생성한 primary key를 받기 위해 필요
        KeyHolder keyHolder = new GeneratedKeyHolder();

        template.update(connection -> {
            // 자동 증가 키
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, item.getItemName());
            ps.setInt(2, item.getPrice());
            ps.setInt(3, item.getQuantity());
            return ps;
        }, keyHolder);

        long key = keyHolder.getKey().longValue();
        item.setId(key);

        return item;
    }

    @Override
    public void update(Long itemId, ItemUpdateDto updateParam) {
        String sql = "update item set item_name=?, price=?, quantity=?, where id=?";
        template.update(sql,
                updateParam.getItemName(),
                updateParam.getPrice(),
                updateParam.getQuantity(),
                itemId
                );
    }

    @Override
    public Optional<Item> findById(Long id) {
        String sql = "select id, item_name, price, quantity from item where id=?";
        try {
            Item item = template.queryForObject(sql, itemRowMapper(), id);
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

        // 동적쿼리
        if(StringUtils.hasText(itemName) || maxPrice != null){
            sql += " where";
        }

        boolean andFlag = false;
        List<Object> param = new ArrayList<>();
        if(StringUtils.hasText(itemName)){
            sql += " item_name like concat ('%',?,'%')";
            param.add(itemName);
            andFlag = true;
        }

        if(maxPrice != null){
            if(andFlag){
                sql += " and";
            }
            sql += " price <= ?";
            param.add(maxPrice);
        }

        log.info("sql={}", sql);
        return template.query(sql, itemRowMapper(), param.toArray());
    }
    private RowMapper<Item> itemRowMapper() {
        return ((rs, rowNum) -> {
           Item item = new Item();
           item.setId(rs.getLong("id"));
           item.setItemName(rs.getString("item_name"));
           item.setPrice(rs.getInt("price"));
           item.setQuantity(rs.getInt("quantity"));
           return item;
        });
    }
}
```

기본
- ``JdbcTemplateItemRepositoryV1``은 ``ItemRepository``인터페이스를 구현했다.
- ``this.template = new JdbcTemplate(dataSource)``
  - ``JdbcTemplate``은 ``데이터소스(dataSource)``가 필요하다.
  - 스프링에서는 ``JdbcTemplate``을 사용할 때 관례상 이 방법을 많이 사용
  - ``JdbcTemplate``을 스프링 빈으로 직접 등록하고 주입받아도 된다.

save(): 데이터 저장 
- ``template.update()``: 데이터를 변경할 때는 ``update()``를 사용하면 된다.
  - ``INSERT , UPDATE , DELETE`` SQL에 사용한다.
  - ``template.update()``의 반환 값은 int 인데, 영향 받은 로우 수를 반환한다.
- 데이터를 저장할 때 PK 생성에 identity(auto increment) 방식을 사용하기 때문에, PK인 ID 값을 개발자가
  직접 지정하는 것이 아니라 비워두고 저장해야 한다. 그러면 데이터베이스가 PK인 ID를 대신 생성해준다.
- 이렇게 데이터베이스가 대신 생성해주는 PK ID 값은 데이터베이스가 생성하기 때문에, 데이터베이스에
  INSERT가 완료 되어야 생성된 PK ID 값을 확인할 수 있다.
- ``KeyHolder``와 ``connection.prepareStatement(sql, new String[]{"id"})``를 사용해서 id 를
  지정해주면 INSERT 쿼리 실행 이후에 데이터베이스에서 생성된 ID 값을 조회할 수 있다.
- ``JdbcTemplate이`` 제공하는 ``SimpleJdbcInsert``라는 훨씬 편리한 기능이 있다.

update(): 데이터 업데이트 
- ``template.update()``: 데이터를 변경할 때는 ``update()``를 사용하면 된다.
  - ``?``에 바인딩할 파라미터를 순서대로 전달하면 된다.
  - 반환 값은 해당 쿼리의 영향을 받은 로우 수 이다. 여기서는 ``where id=?``를 지정했기 때문에 영향 받은 로우수는 최대 1개이다.

findById(): 데이터를 하나 조회한다.
- ``template.queryForObject()``
  - 결과 로우가 하나일 때 사용한다
  - ``RowMapper``는 데이터베이스의 반환 결과인 ``ResultSet``을 객체로 변환한다.
  - 결과가 없으면 ``EmptyResultDataAccessException``예외가 발생한다.
  - 결과가 둘 이상이면 ``IncorrectResultSizeDataAccessException``예외가 발생한다.
- ``ItemRepository.findById()``인터페이스는 결과가 없을 때 ``Optional``을 반환해야 한다. 따라서 결과가
  없으면 예외를 잡아서 ``Optional.empty``를 대신 반환하면 된다.

findAll(): 데이터를 리스트로 조회한다. 그리고 검색 조건으로 적절한 데이터를 찾는다
- ``template.query()``
  - 결과가 하나 이상일 때 사용한다.
  - 결과가 없으면 빈 컬렉션을 반환한다.
  - 동적 쿼리 

itemRowMapper()
```text
데이터베이스의 조회 결과를 객체로 변환할 때 사용한다.
JDBC를 직접 사용할 때 ResultSet 를 사용했던 부분을 떠올리면 된다.
차이가 있다면 다음과 같이 JdbcTemplate이 다음과 같은 루프를 돌려주고, 개발자는 RowMapper 를 구현해서 그 내부 코드만 
채운다고 이해하면 된다.

while(resultSet 이 끝날 때 까지) {
 rowMapper(rs, rowNum)
}
```