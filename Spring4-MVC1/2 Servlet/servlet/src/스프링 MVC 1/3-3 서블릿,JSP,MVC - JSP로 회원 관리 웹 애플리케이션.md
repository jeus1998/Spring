
# JSP 회원 관리 웹 애플리케이션

### JSP 라이브러리 추가

- JSP를 사용하려면 먼저 다음 라이브러리를 추가해야 한다. - build.gradle
- 스프링 부트 3.0 미만
```java
//JSP 추가 시작
implementation 'org.apache.tomcat.embed:tomcat-embed-jasper'
implementation 'javax.servlet:jstl'
//JSP 추가 끝
```

- 스프링 부트 3.0 이상 
```java
//JSP 추가 시작
implementation 'org.apache.tomcat.embed:tomcat-embed-jasper'
implementation 'jakarta.servlet:jakarta.servlet-api' //스프링부트 3.0 이상
implementation 'jakarta.servlet.jsp.jstl:jakarta.servlet.jsp.jstl-api' //스프링부트3.0 이상
implementation 'org.glassfish.web:jakarta.servlet.jsp.jstl' //스프링부트 3.0 이상
//JSP 추가 끝
```

### 회원 등록 폼 JSP

- webapp/jsp/members/new-form.jsp

```html
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
 <title>Title</title>
</head>
<body>
<form action="/jsp/members/save.jsp" method="post">
     username: <input type="text" name="username" />
     age: <input type="text" name="age" />
     <button type="submit">전송</button>
</form>
</body>
</html>
```

### 회원 저장 폼 JSP

- webapp/jsp/members/save.jsp

```html
<%@ page import="spring.servlet.domain.member.MemberRepository" %>
<%@ page import="spring.servlet.domain.member.Member" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // request, response 사용 가능
    MemberRepository memberRepository = MemberRepository.getInstance();

    String username = request.getParameter("username");
    int age = Integer.parseInt(request.getParameter("age"));

    Member member = new Member(username, age);
    memberRepository.save(member);

%>
<html>
<head>
    <title>Title</title>
</head>
<body>
성공
<ul>
    <li>id=<%=member.getId()%></li>
    <li>username=<%=member.getUsername()%></li>
    <li>age=<%=member.getAge()%></li>
</ul>
<a href="/index.html"></a>
</body>
</html>
```

### 회원 리스트 폼 JSP

```html
<%@ page import="java.util.List" %>
<%@ page import="spring.servlet.domain.member.MemberRepository" %>
<%@ page import="spring.servlet.domain.member.Member" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
 MemberRepository memberRepository = MemberRepository.getInstance();
 List<Member> members = memberRepository.findAll();
%>
<html>
<head>
 <meta charset="UTF-8">
 <title>Title</title>
</head>
<body>
<a href="/index.html">메인</a>
<table>
  <thead>
    <th>id</th>
    <th>username</th>
    <th>age</th>
 </thead>
 <tbody>
<%
 for (Member member : members) {
 out.write(" <tr>");
 out.write(" <td>" + member.getId() + "</td>");
 out.write(" <td>" + member.getUsername() + "</td>");
 out.write(" <td>" + member.getAge() + "</td>");
 out.write(" </tr>");
 }
%>
 </tbody>
</table>
</body>
</html>
```

### 💯 JSP 정리

- <%@ page contentType="text/html;charset=UTF-8" language="java" %>
  -  줄은 JSP문서라는 뜻이다. JSP 문서는 이렇게 시작해야 한다.
- 실행
  - http://localhost:8080/jsp/members/new-form.jsp
  - 실행시 .jsp 까지 함께 적어주어야 한다.
- JSP는 자바 코드를 그대로 다 사용할 수 있다.
  - <%@ page import="hello.servlet.domain.member.MemberRepository" %>
     - 자바의 import 문과 같다.
  - <% ~~ %>
     - 이 부분에는 자바 코드를 입력할 수 있다.
  - <%= ~~ %>
     - 이 부분에는 자바 코드를 출력할 수 있다.

### 👊 서블릿과 JSP의 한계

서블릿으로 개발할 때는 뷰(View)화면을 위한 HTML을 만드는 작업이 자바 코드에 섞여서 지저분하고 복잡했다.
JSP를 사용한 덕분에 뷰를 생성하는 HTML 작업을 깔끔하게 가져가고, 중간중간 동적으로 변경이 필요한 부분에만 자
바 코드를 적용했다.
회원 저장 JSP를 보자. 코드의 상위 절반은 회원을 저장하기 위한 비즈니스 로직이고, 나머지 하위 절반만 결과를
HTML로 보여주기 위한 뷰 영역이다. 회원 목록의 경우에도 마찬가지다.
코드를 잘 보면, JAVA 코드, 데이터를 조회하는 리포지토리 등등 다양한 코드가 모두 JSP에 노출되어 있다. JSP가 너
무 많은 역할을 한다. 

### 👌 MVC 패턴의 등장 
비즈니스 로직은 서블릿 처럼 다른곳에서 처리하고, JSP는 목적에 맞게 HTML로 화면(View)을 그리는 일에 집중하도
록 하자. ➡️ MVC 패턴 