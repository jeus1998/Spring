
#  HTTP 캐시와 조건부 요청 - 검증 헤더와 조건부 요청2

- 검증 헤더
  - 캐시 데이터와 서버 데이터가 같은지 검증하는 데이터
  - Last-Modified , ETag
- 조건부 요청 헤더
  - 검증 헤더로 조건에 따른 분기
  - If-Modified-Since: Last-Modified 사용
  - If-None-Match: ETag 사용
  - 조건이 만족하면 200 OK (데이터 변경 ⭕️)
  - 조건이 만족하지 않으면 304 Not Modified (데이터 변경 ❌)

### 검증 헤더와 조건부 요청 - 예시 

- If-Modified-Since: 이후에 데이터가 수정되었으면?
  - 데이터 미변경 예시
     - 캐시: 2020년 11월 10일 10:00:00 vs 서버: 2020년 11월 10일 10:00:00
     - 304 Not Modified, 헤더 데이터만 전송(BODY 미포함
     - 전송 용량 0.1M (헤더 0.1M)
  - 데이터 변경 예시
     - 캐시: 2020년 11월 10일 10:00:00 vs 서버: 2020년 11월 10일 11:00:0
     - 200 OK, 모든 데이터 전송(BODY 포함)
     - 전송 용량 1.1M (헤더 0.1M, 바디 1.0M)

### 검증 헤더와 조건부 요청 - Last-Modified, If-Modified-Since 단점

- 1초 미만(0.x초) 단위로 캐시 조정이 불가능 ➡️ 2020년 11월 10일 10:00:00 (초 단위)
- 날짜 기반의 로직 사용
- 데이터를 수정해서 날짜가 다르지만, 같은 데이터를 수정해서 데이터 결과가 똑같은 경우
  - A ➡️ B ➡️ A 날짜는 update 데이터는 그대로 
- 서버에서 별도의 캐시 로직을 관리하고 싶은 경우
  - 예) 스페이스나 주석처럼 크게 영향이 없는 변경에서 캐시를 유지하고 싶은 경우

### 검증 헤더와 조건부 요청 - ETag, If-None-Match

- ETag(Entity Tag)
- 캐시용 데이터에 임의의 고유한 버전 이름을 달아둠
  - 예) ETag: "v1.0", ETag: "a2jiodwjekjl3"
- 데이터가 변경되면 이 이름을 바꾸어서 변경함(Hash를 다시 생성)
  - 예) ETag: "aaaaa" -> ETag: "bbbbb"
  - 해시 파일 라이브러리 사용 
- 진짜 단순하게 ETag만 보내서 같으면 유지, 다르면 다시 받기
  - 같으면 ➡️ 304 Not modified
  - 다르면 ➡️ 200 OK 

### ETag, If-None-Match 적용

![67.JPG](%EC%9D%B4%EB%AF%B8%EC%A7%80%2F67.JPG)

![68.JPG](%EC%9D%B4%EB%AF%B8%EC%A7%80%2F68.JPG)

![69.JPG](%EC%9D%B4%EB%AF%B8%EC%A7%80%2F69.JPG)

![70.JPG](%EC%9D%B4%EB%AF%B8%EC%A7%80%2F70.JPG)

![71.JPG](%EC%9D%B4%EB%AF%B8%EC%A7%80%2F71.JPG)

![72.JPG](%EC%9D%B4%EB%AF%B8%EC%A7%80%2F72.JPG)

### 💯 ETag, If-None-Match 정리 

- 진짜 단순하게 ETag만 서버에 보내서 같으면 유지, 다르면 다시 받기!
- 캐시 제어 로직을 서버에서 완전히 관리
- 클라이언트는 단순히 이 값을 서버에 제공(클라이언트는 캐시 메커니즘을 모름)
- 예)
  - 서버는 배타 오픈 기간인 3일 동안 파일이 변경되어도 ETag를 동일하게 유지
  - 애플리케이션 배포 주기에 맞추어 ETag 모두 갱신



