
# HTTP 메시지

### HTTP 메시지 구조 

![26.JPG](%EC%9D%B4%EB%AF%B8%EC%A7%80%2F26.JPG)

![27.JPG](%EC%9D%B4%EB%AF%B8%EC%A7%80%2F27.JPG)

### 시작라인 요청 메시지 

![28.JPG](%EC%9D%B4%EB%AF%B8%EC%A7%80%2F28.JPG)

- start-line
  - request-line
    - method SP(공백) request-target SP HTTP-version CRLF(엔터)
    - HTTP method: GET
    - request-target(요청 대상): /search?q=hello&hl=ko
    - HTTP version: HTTP/1.1

### 시작라인  요청 메시지 - HTTP 메서드
- 종류: GET, POST, PUT, DELETE...
- 서버가 수행해야 할 동작 지정
  - GET: 리소스 조회
  - POST: 요청 내역 처리

### 시작라인  요청 메시지 - 요청 대상 
- absolute-path[?query] (절대경로[?쿼리])
- 절대경로= "/" 로 시작하는 경로
- 참고: *, http://...?x=y 와 같이 다른 유형의 경로지정 방법도 있다.

### 시작라인  요청 메시지 - HTTP 버전
- HTTP Version

### 시작라인 응답 메시지 

![29.JPG](%EC%9D%B4%EB%AF%B8%EC%A7%80%2F29.JPG)

- start-line
  - status-line
  - HTTP-version SP status-code SP reason-phrase CRLF
  - HTTP version: HTTP/1.1
  - status-code: HTTP 상태 코드: 200 
    - 200: 성공
    - 400: 클라이언트 요청 오류
    - 500: 서버 내부 오류 
  - reason-pharse(이유 문구): 사람이 이해할 수 있는 짧은 생태 코드 설명 글

### HTTP 헤더 & 용도

![30.JPG](%EC%9D%B4%EB%AF%B8%EC%A7%80%2F30.JPG)

- HTTP 전송에 필요한 모든 부가정보 
- 예) 메시지 바디의 내용, 메시지 바디의 크기, 압축, 인증, 요청 클라이언트(브라우저) 정보,
  서버 애플리케이션 정보, 캐시 관리 정보...
- 표준 헤더가 너무 많음
- 필요시 임의의 헤더 추가 가능
  - helloworld: hihi

### HTTP 메시지 바디 & 용도

![31.JPG](%EC%9D%B4%EB%AF%B8%EC%A7%80%2F31.JPG)