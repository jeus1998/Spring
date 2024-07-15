# YAML

### YAML 소개 

- 스프링은 설정 데이터를 사용할 때 ``application.properties``뿐만 아니라 ``application.yml``이라는 형식도
  지원한다.
- YAML(YAML Ain't Markup Language)은 사람이 읽기 좋은 데이터 구조를 목표로 한다.
- 확장자는 ``yaml, yml``이다. 주로 ``yml``을 사용한다.

application.properties 예시
```properties
environments.dev.url=https://dev.example.com
environments.dev.name=Developer Setup
environments.prod.url=https://another.example.com
environments.prod.name=My Cool App
```

application.yml 예시
```yml
environments:
  dev:
    url: "https://dev.example.com"
    name: "Developer Setup"
  prod:
    url: "https://another.example.com"
    name: "My Cool App"
```
- YAML의 가장 큰 특징은 사람이 읽기 좋게 계층 구조를 이룬다는 점이다
- YAML은 space(공백)로 계층 구조를 만든다. ``space``는 1칸을 사용해도 되는데, 보통 2칸을 사용한다. 
  - 일관성있게 사용하지 않으면 읽기 어렵거나 구조가 깨질 수 있다.
- 구분 기호로 ``:``를 사용한다. 만약 값이 있다면 이렇게 ``key: value`` ``:``이후에 공백을 하나 넣고 값을 넣어주면 된다.

### YAML 적용 

- ``application.properties``를 사용하지 않도록 파일 이름을 변경하자.
- ``application.properties`` ➡️ ``application_backup.properties``

src/main/resources/application.yml 생성
```yml
my:
  datasource:
    url: local.db.com
    username: local_user
    password: local_pw
    etc:
      max-connection: 1
      timeout: 3500ms
      options: CACHE, ADMIN
```

주의
- ``application.properties, application.yml``을 같이 사용하면 ``application.properties``가 우선권을 가진다.
- 이것을 둘이 함께 사용하는 것은 일관성이 없으므로 권장하지 않는다.
- 참고로 실무에서는 설정 정보가 많아서 보기 편한 ``yml``을 선호한다.


### YML과 프로필

- YML에도 프로필을 적용할 수 있다.

```yml
my:
  datasource:
    url: local.db.com
    username: local_user
    password: local_pw
    etc:
      max-connection: 1
      timeout: 3500ms
      options: CACHE, ADMIN
---
spring:
  config:
    activate:
      on-profile: dev

my:
  datasource:
    url: dev.db.com
    username: dev_user
    password: dev_pw
    etc:
      max-connection: 1
      timeout: 3500ms
      options: CACHE, DEV
---
spring:
  config:
    activate:
      on-profile: prod

my:
  datasource:
    url: prod.db.com
    username: prod_user
    password: prod_pw
    etc:
      max-connection: 1
      timeout: 3500ms
      options: CACHE, PROD
```
- ``yml``은 ``---``dash(-) 3개를 사용해서 논리 파일을 구분한다
- ``spring.config.active.on-profile``을 사용해서 프로필을 적용할 수 있다.
- 나머지는 ``application.properties``와 동일하다.