
# HTTP 일반 헤더 - 쿠키 🍪

- Set-Cookie: 서버에서 클라이언트로 쿠키 전달(응답)
- Cookie: 클라이언트가 서버에서 받은 쿠키를 저장하고, HTTP 요청시 서버로 전달

### Stateless

- HTTP는 무상태(Stateless) 프로토콜이다
- 클라이언트와 서버가 요청과 응답을 주고 받으면 연결이 끊어진다.
- 클라이언트와 서버는 서로 상태를 유지하지 않는다

### 쿠키 🍪 사용 로그인 

![50.JPG](%EC%9D%B4%EB%AF%B8%EC%A7%80%2F50.JPG)

![51.JPG](%EC%9D%B4%EB%AF%B8%EC%A7%80%2F51.JPG)

- 쿠키는 모든 요청에 쿠키 정보를 자동 포함

### 쿠키 🍪

- set-cookie: sessionId=abcde1234; expires=Sat, 26-Dec-2020 00:00:00 GMT; path=/; domain=.google.com; Secure
- 사용처
  - 사용자 로그인 세션 관리
  - 광고 정보 트래킹
- 쿠키 정보는 항상 서버에 전송됨
  - 네트워크 트래픽 추가 유발
  - 최소한의 정보만 사용(세션 id, 인증 토큰)
  - 서버에 전송하지 않고, 웹 브라우저 내부에 데이터를 저장하고 싶으면 웹 스토리지 (localStorage, sessionStorage) 
- 보안에 민감한 데이터는 저장하면 안됨(주민번호, 신용카드 번호 등등)

### 쿠키 🍪 - 생명 주기 (Expires, max-age)

- Set-Cookie: expires=Sat, 26-Dec-2020 04:39:21 GMT
  - 만료일이 되면 쿠키 삭제
- Set-Cookie: max-age=3600 (3600초)
  - 0이나 음수를 지정하면 쿠키 삭제
- 세션 쿠키: 만료 날짜를 생략하면 브라우저 종료시 까지만 유지
- 영속 쿠키: 만료 날짜를 입력하면 해당 날짜까지 유지

### 쿠키 🍪 - 도메인 (Domain)

- 예) domain=example.org
- 명시: 명시한 문서 기준 도메인 + 서브 도메인 포함
   - domain=example.org를 지정해서 쿠키 생성
     - example.org
     - dev.example.org도 쿠키 접근
- 생략: 현재 문서 기준 도메인만 적용
   - example.org 에서 쿠키를 생성하고 domain 지정을 생략
     - example.org 에서만 쿠키 접근
     - dev.example.org는 쿠키 미접근

### 쿠키 🍪 - 경로 (Path)

- 예) path=/home
- 이 경로를 포함한 하위 경로 페이지만 쿠키 접근
- 일반적으로 path=/ 루트로 지정
- 예)
   - path=/home 지정
   - /home ->  ⭕️
   - /home/level1 ->  ⭕️
   - /hello -> ❌ 

### 쿠키 🍪 - 보안 (Secure, HttpOnly, SameSite)

- Secure
  - 쿠키는 http, https를 구분하지 않고 전송
  - Secure를 적용하면 https인 경우에만 전송
- HttpOnly
  - XSS 공격 방지
  - 자바스크립트에서 접근 불가(document.cookie)
  - HTTP 전송에만 사용
- SameSite
  - XSRF 공격 방지
  - 요청 도메인과 쿠키에 설정된 도메인이 같은 경우만 쿠키 전송



