# JdbcTemplate - 이름 지정 파라미터 2

### 이름 지정 파라미터

- 파라미터를 전달하려면 ``Map`` 처럼 ``key , value`` 데이터 구조를 만들어서 전달해야 한다.
- 이름 지정 바인딩에서 자주 사용하는 파라미터의 종류는 크게 3가지가 있다.
  - Map
  - SqlParameterSource<인터페이스>
    - MapSqlParameterSource<구현체>
    - BeanPropertySqlParameterSource<구현체>

Map
- 단순히 Map 사용 
- findById 코드
```java
Map<String, Object> param = Map.of("id", id);
Item item = template.queryForObject(sql, param, itemRowMapper());
```

MapSqlParameterSource
- Map 과 유사한데, SQL 타입을 지정할 수 있는 등 SQL에 좀 더 특화된 기능을 제공한다.
- ``SqlParameterSource``인터페이스의 구현체이다.
- ``MapSqlParameterSource``는 메서드 체인을 통해 편리한 사용법도 제공한다.
- update() 코드
```java
SqlParameterSource param = new MapSqlParameterSource()
         .addValue("itemName", updateParam.getItemName())
         .addValue("price", updateParam.getPrice())
         .addValue("quantity", updateParam.getQuantity())
         .addValue("id", itemId); // 이 부분이 별도로 필요하다.

template.update(sql, param);
```

BeanPropertySqlParameterSource
- 자바빈 프로퍼티 규약을 통해서 자동으로 파라미터 객체를 생성한다.
- 예) (getXxx() -> xxx, getItemName() -> itemName)
- getItemName() , getPrice() 가 있으면 다음과 같은 데이터를 자동으로 만들어낸다.
  - key=itemName, value=상품명 값
  - key=price, value=가격 값
- ``SqlParameterSource``인터페이스의 구현체이다.
- save() , findAll() 코드에서 확인할 수 있다.
```java
SqlParameterSource param = new BeanPropertySqlParameterSource(item);
KeyHolder keyHolder = new GeneratedKeyHolder();
template.update(sql, param, keyHolder);
```
- ``BeanPropertySqlParameterSource``가 많은 것을 자동화 해주기 때문에 가장 좋아보이지만, 
  ``BeanPropertySqlParameterSource``를 항상 사용할 수 있는 것은 아니다.
- update() 에서는 SQL에 :id 를 바인딩 해야 하는데, update() 에서 사용하는
  ItemUpdateDto 에는 itemId 가 없다. 따라서 ``BeanPropertySqlParameterSource``를 사용할 수 없
  고, 대신에 ``MapSqlParameterSource``를 사용했다

### BeanPropertyRowMapper

JdbcTemplateItemRepositoryV1 - itemRowMapper()
```java
private RowMapper<Item> itemRowMapper() {
     return (rs, rowNum) -> {
         Item item = new Item();
         item.setId(rs.getLong("id"));
         item.setItemName(rs.getString("item_name"));
         item.setPrice(rs.getInt("price"));
         item.setQuantity(rs.getInt("quantity"));
         return item;
     };
}
```

JdbcTemplateItemRepositoryV2 - itemRowMapper()
```java
private RowMapper<Item> itemRowMapper() {
    return BeanPropertyRowMapper.newInstance(Item.class); // camel 변환 지원
}
```
- ``BeanPropertyRowMapper``는 ``ResultSet``의 결과를 받아서 자바빈 규약에 맞추어 데이터를 변환한다.

별칭(AS)
- select item_name 의 경우 setItem_name() 이라는 메서드가 없기 때문에 골치가 아프다.
- 이런 경우 개발자가 조회 SQL을 다음과 같이 고치면 된다.
  - select item_name as itemName 
- 별칭 as 를 사용해서 SQL 조회 결과의 이름을 변경하는 것이다. 실제로 이 방법은 자주 사용된다. 
- 데이터베이스 컬럼 이름과 객체 이름이 완전히 다를 때 문제를 해결할 수 있다.
  - member_name 이라고 되어 있는데 객체에 username 이라고 되어 있다면 다음과 같이 해결할 수 있다.
  - select member_name as username
- ``JdbcTemplate``은 물론이고, ``MyBatis``같은 기술에서도 자주 사용된다.

관례의 불일치
```text
자바 객체는 카멜(camelCase) 표기법을 사용한다. itemName 처럼 중간에 낙타 봉이 올라와 있는 표기법이다.
반면에 관계형 데이터베이스에서는 주로 언더스코어를 사용하는 snake_case 표기법을 사용한다. item_name 처
럼 중간에 언더스코어를 사용하는 표기법이다.

이 부분을 관례로 많이 사용하다 보니 BeanPropertyRowMapper 는 언더스코어 표기법을 카멜로 자동 변환해준다.
따라서 select item_name 으로 조회해도 setItemName() 에 문제 없이 값이 들어간다.

정리하면 snake_case 는 자동으로 해결되니 그냥 두면 되고, 컬럼 이름과 객체 이름이 완전히 다른 경우에는 조회
SQL에서 별칭을 사용하면 된다.
```