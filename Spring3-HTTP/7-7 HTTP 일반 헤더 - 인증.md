
# HTTP 일반 헤더 - 인증

- Authorization: 클라이언트 인증 정보를 서버에 전달
- WWW-Authenticate: 리소스 접근시 필요한 인증 방법 정의

### Authorization
- Authorization: Basic xxxxxxxxxxxxxxxx

### WWW-Authenticate - 리소스 접근시 필요한 인증 방법 정의
- 리소스 접근시 필요한 인증 방법 정의
- 401 Unauthorized 응답과 함께 사용'
- WWW-Authenticate: Newauth realm="apps", type=1,
  title="Login to \"apps\"", Basic realm="simple"

