
# 서블릿 - HTTP 요청 데이터(POST HTML Form)

- 이번에는 HTML의 Form을 사용해서 클라이언트에서 서버로 데이터를 전송해보자.
- 주로 회원 가입, 상품 주문 등에서 사용하는 방식이다

### HTTP 요청 데이터(POST HTML Form)특징 
- content-type: application/x-www-form-urlencoded
- 메시지 바디에 쿼리 파리미터 형식으로 데이터를 전달한다. username=hello&age=20

### src/main/webapp/basic/hello-form.html 생성

```html
<!DOCTYPE html>
<html>
<head>
 <meta charset="UTF-8">
 <title>Title</title>
</head>
<body>
<form action="/request-param" method="post">
 username: <input type="text" name="username" />
 age: <input type="text" name="age" />
 <button type="submit">전송</button>
</form>
</body>
</html>
```

- POST의 HTML Form을 전송하면 웹 브라우저는 다음 형식으로 HTTP 메시지를 만든다.
  - 요청 URL: http://localhost:8080/request-param
  - content-type: application/x-www-form-urlencoded
  - message body: username=hello&age=20
- application/x-www-form-urlencoded 형식은 앞서 GET에서 살펴본 쿼리 파라미터 형식과 같다. 따라서 쿼
  리 파라미터 조회 메서드를 그대로 사용하면 된다.
- 클라이언트(웹 브라우저) 입장에서는 두 방식에 차이가 있지만, 서버 입장에서는 둘의 형식이 동일하므로, 
  request.getParameter() 로 편리하게 구분없이 조회할 수 있다

### ✅ 참고 
- content-type은 HTTP 메시지 바디의 데이터 형식을 지정한다.
- GET URL 쿼리 파라미터 형식으로 클라이언트에서 서버로 데이터를 전달할 때는 HTTP 메시지 바디를 사용하
  지 않기 때문에 content-type이 없다.
- POST HTML Form 형식으로 데이터를 전달하면 HTTP 메시지 바디에 해당 데이터를 포함해서 보내기 때문에
  바디에 포함된 데이터가 어떤 형식인지 content-type을 꼭 지정해야 한다.
- 이렇게 폼으로 데이터를 전송하는 형식을 application/x-www-form-urlencoded 라 한다.

