# JdbcTemplate 기능 정리

### 주요 기능

- JdbcTemplate
  - 순서 기반 파라미터 바인딩을 지원한다.
- NamedParameterJdbcTemplate
  - 이름 기반 파라미터 바인딩을 지원한다. (권장)
- SimpleJdbcInsert
  - INSERT SQL을 편리하게 사용할 수 있다.
- SimpleJdbcCall
  - 스토어드 프로시저를 편리하게 호출할 수 있다.

참고
- 스토어드 프로시저를 사용하기 위한 ``SimpleJdbcCall``에 대한 자세한 내용은 다음 스프링 공식 메뉴얼을 참고하자.
  - [스토어드 프로시저 예제](https://docs.spring.io/spring-framework/docs/current/reference/html/data-access.html#jdbcsimple-jdbc-call-1)
- JdbcTemplate에 대한 사용법은 스프링 공식 메뉴얼에 자세히 소개되어 있다.
  - [스프링 JdbcTemplate 사용 방법 공식 메뉴얼](https://docs.spring.io/spring-framework/docs/current/reference/html/data-access.html#jdbcJdbcTemplate)

###  조회

단건 조회 - 숫자 조회
```java
int rowCount = jdbcTemplate.queryForObject("select count(*) from t_actor", Integer.class);
```
- 하나의 로우를 조회할 때는 ``queryForObject()``를 사용하면 된다. 
- 지금처럼 조회 대상이 객체가 아니라 단순 데이터 하나라면 타입을 ``Integer.class , String.class``와 같이 지정해주면 된다.

단건 조회 - 숫자 조회, 파라미터 바인딩
```java
int countOfActorsNamedJoe = jdbcTemplate.queryForObject(
 "select count(*) from t_actor where first_name = ?", Integer.class, "Joe");
```
- 숫자 하나와 파라미터 바인딩 예시이다.

단건 조회 - 객체 조회 
```java
Actor actor = jdbcTemplate.queryForObject(
 "select first_name, last_name from t_actor where id = ?",
        (resultSet, rowNum) -> {
           Actor newActor = new Actor();
           newActor.setFirstName(resultSet.getString("first_name"));
           newActor.setLastName(resultSet.getString("last_name"));
           return newActor;
           },1212L); 
```
- 객체 하나를 조회한다. 결과를 객체로 매핑해야 하므로 RowMapper 를 사용해야 한다. 여기서는 람다를 사용했다.


목록 조회 - 객체 
```java
List<Actor> actors = jdbcTemplate.query(
     "select first_name, last_name from t_actor",
     (resultSet, rowNum) -> {
         Actor actor = new Actor();
         actor.setFirstName(resultSet.getString("first_name"));
         actor.setLastName(resultSet.getString("last_name"));
         return actor;
     });
```
- 여러 로우를 조회할 때는 query() 를 사용하면 된다. 결과를 리스트로 반환한다.
- 결과를 객체로 매핑해야 하므로 RowMapper 를 사용해야 한다. 여기서는 람다를 사용했다.

목록 조회 - 객체 
```java
private final RowMapper<Actor> actorRowMapper = (resultSet, rowNum) -> {
     Actor actor = new Actor();
     actor.setFirstName(resultSet.getString("first_name"));
     actor.setLastName(resultSet.getString("last_name"));
     return actor;
};
public List<Actor> findAllActors() {
    return this.jdbcTemplate.query("select first_name, last_name from t_actor", actorRowMapper);
}
```
- 여러 로우를 조회할 때는 query() 를 사용하면 된다. 결과를 리스트로 반환한다.
- 여기서는 RowMapper 를 분리했다. 이렇게 하면 여러 곳에서 재사용 할 수 있다.

### 변경 (INSERT, UPDATE, DELETE)

- 데이터를 변경할 때는 ``jdbcTemplate.update()``를 사용하면 된다. 
- 참고로 int 반환값을 반환하는데, SQL 실행 결과에 영향받은 로우 수를 반환한다.

등록
```java
jdbcTemplate.update(
 "insert into t_actor (first_name, last_name) values (?, ?)","Leonor", "Watling"); 
```
수정
```java
jdbcTemplate.update("update t_actor set last_name = ? where id = ?", "Banjo", 5276L); 
 ```
삭제
```java
jdbcTemplate.update("delete from t_actor where id = ?",Long.valueOf(actorId)); 
```

### 기타 기능

- 임의의 SQL을 실행할 때는 execute() 를 사용하면 된다. 
- 테이블을 생성하는 DDL에 사용할 수 있다.

DDL
```java
jdbcTemplate.execute("create table mytable (id integer, name varchar(100))"); 
```

스토어드 프로시저 호출 
```java
jdbcTemplate.update("call SUPPORT.REFRESH_ACTORS_SUMMARY(?)", Long.valueOf(unionId)); 
```

### 정리

```text
실무에서 가장 간단하고 실용적인 방법으로 SQL을 사용하려면 JdbcTemplate을 사용하면 된다.
JPA와 같은 ORM 기술을 사용하면서 동시에 SQL을 직접 작성해야 할 때가 있는데, 그때도 JdbcTemplate을 함께
사용하면 된다.
그런데 JdbcTemplate의 최대 단점이 있는데, 바로 동적 쿼리 문제를 해결하지 못한다는 점이다. 그리고 SQL을 자바
코드로 작성하기 때문에 SQL 라인이 코드를 넘어갈 때 마다 문자 더하기를 해주어야 하는 단점도 있다.
동적 쿼리 문제를 해결하면서 동시에 SQL도 편리하게 작성할 수 있게 도와주는 기술이 바로 MyBatis 이다.
```