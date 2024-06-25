# MyBatis 설정

build.gradle 의존 관계를 추가
- 스프링 부트 3.0 미만 
  - ``implementation 'org.mybatis.spring.boot:mybatis-spring-boot-starter:2.2.0'``
- 스프링 부트 3.0 이상
  - ``implementation 'org.mybatis.spring.boot:mybatis-spring-boot-starter:3.0.3'``
- 뒤에 버전 정보가 붙는 이유는 스프링 부트가 버전을 관리해주는 공식 라이브러리가 아니기 때문이다.
- 스프링 부트가 버전을 관리해주는 경우 버전 정보를 붙이지 않아도 최적의 버전을 자동으로 찾아준다.


다음과 같은 라이브러리가 추가된다.
- ``mybatis-spring-boot-starter``
  - MyBatis를 스프링 부트에서 편리하게 사용할 수 있게 시작하는 라이브러리
- ``mybatis-spring-boot-autoconfigure``
  - MyBatis와 스프링 부트 설정 라이브러리
- ``mybatis-spring``
  - MyBatis와 스프링을 연동하는 라이브러리
- ``mybatis``
  - MyBatis 라이브러리

application.properties에 설정 추가
```text
#MyBatis
mybatis.type-aliases-package=hello.itemservice.domain
mybatis.configuration.map-underscore-to-camel-case=true
logging.level.hello.itemservice.repository.mybatis=trace
```
- main, test 모두 추가 
- main - application.properties
- test - application.properties
- ``mybatis.type-aliases-package``
  - 마이바티스에서 타입 정보를 사용할 때는 패키지 이름을 적어주어야 하는데, 여기에 명시하면 패키지 이름을 생략할 수 있다.
  - 지정한 패키지와 그 하위 패키지가 자동으로 인식된다.
  - 여러 위치를 지정하려면 ``,``,``;``로 구분하면 된다.
- ``mybatis.configuration.map-underscore-to-camel-case``
  - ``JdbcTemplate``의 ``BeanPropertyRowMapper``에서 처럼 언더바를 카멜로 자동 변경해주는 기능을 활성화 한다.
- ``logging.level.hello.itemservice.repository.mybatis=trace``
  - MyBatis에서 실행되는 쿼리 로그를 확인할 수 있다.

