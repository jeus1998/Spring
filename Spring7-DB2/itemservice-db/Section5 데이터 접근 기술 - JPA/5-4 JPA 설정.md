# JPA 설정

### build.gradle

build.gradle
```text
dependencies {
    //JPA, 스프링 데이터 JPA 추가
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    
    //JdbcTemplate 제거 
    //implementation 'org.springframework.boot:spring-boot-starter-jdbc'
}    
```
- ``spring-boot-starter-data-jpa``라이브러리를 사용하면 JPA와 스프링 데이터 JPA를 스프링 부트와 통합하고, 
  설정도 아주 간단히 할 수 있다.
- ``spring-boot-starter-data-jpa``는 ``spring-boot-starter-jdbc``도 함께 포함(의존)한다.
  - 따라서 해당 라이브러리 의존관계를 제거해도 된다.
  - ``mybatis-spring-boot-starter``도 ``spring-boot-starter-jdbc``를 포함
- 라이브러리 추가
  - ``hibernate-core``:JPA 구현체인 하이버네이트 라이브러리
  - ``jakarta.persistence-api``:JPA 인터페이스
  - ``spring-data-jpa``:스프링 데이터 JPA 라이브러리

### application.properties

main - application.properties, test - application.properties
```text
#JPA log
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```
- ``org.hibernate.SQL=DEBUG``:하이버네이트가 생성하고 실행하는 SQL을 확인할 수 있다.
- ``org.hibernate.type.descriptor.sql.BasicBinder=TRACE``:SQL에 바인딩 되는 파라미터를 확인 할 수 있다.
- ``spring.jpa.show-sql=true``: ``org.hibernate.SQL=DEBUG`` 동일 설정 
  - 설정은 System.out 콘솔을 통해서 SQL이 출력
  - 권장 x

스프링 부트 3.0
- 스프링 부트 3.0 이상을 사용하면 하이버네이트 6 버전이 사용되는데, 로그 설정 방식이 달려졌다.
```text
#JPA log
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.orm.jdbc.bind=TRACE
```