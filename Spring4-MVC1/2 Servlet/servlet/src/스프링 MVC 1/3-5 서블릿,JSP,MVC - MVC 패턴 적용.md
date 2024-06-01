
#  MVC 패턴 적용

- 컨트롤러: 서블릿
- 뷰: JSP
- 모델: HttpServletRequest
  - setAttribute(), getAttribute()

### 회원 등록  - 컨트롤러

- /web/servletmvc/MvcMemberFormServlet.java

```java
@WebServlet(name = "mvcMemberFormServlet", urlPatterns = "/servlet-mvc/members/new-form")
public class MvcMemberFormServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String viewPath = "/WEB-INF/views/new-form.jsp";

        RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);
        dispatcher.forward(request, response); // 서블릿 -> JSP

    }
}
```

- dispatcher.forward(request, response)
  - 다른 서블릿이나 JSP로 이동할 수 있는 기능이다. 
  - 서버 내부에서 다시 호출이 발생한다.
- /WEB-INF
  - 경로안에 JSP가 있으면 외부에서 직접 JSP를 호출할 수 없다.
  - 우리가 기대하는 것은 항상 컨트롤러를 통해서 JSP를 호출하는 것이다.
  - 만약 직접 웹 브라우저에 /WEB-INF/* 경로로 호출을 하면 404 에러 페이지
- redirect vs forward
  - 리다이렉트는 실제 클라이언트(웹 브라우저)에 응답이 나갔다가, 클라이언트가 redirect 경로로 다시 요청한다.
  - 따라서 클라이언트가 인지할 수 있고, URL 경로도 실제로 변경된다.
  - 클라이언트 ➡️ 서버 ➡️ 클라이언트 ➡️ 서버 
  - 반면에 포워드는 서버 내부에서 일어나는 호출이기 때문에 클라이언트가 전혀 인지하지 못한다.
  - 클라이언트 ➡️ 서버 ➡️ 서버 ➡️ 클라이언트 

### 회원 등록 폼 - 뷰

```html
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
 <meta charset="UTF-8">
 <title>Title</title>
</head>
<body>
<!-- 상대경로 사용, [현재 URL이 속한 계층 경로 + /save] -->
<form action="save" method="post">
     username: <input type="text" name="username" />
     age: <input type="text" name="age" />
     <button type="submit">전송</button>
</form>
</body>
</html>
```

- 상대 경로
  - 여기서 form의 action을 보면 절대 경로가 아니라 상대 경로인 것을 확인할 수 있다.
  - /로 시작 ❌
  - 이렇게 상대 경로로 전송을 하면 폼 전송시 현재 URL이 속한 계층 경로 + save가 호출
  - 현재 계층 경로: /servlet-mvc/members/
  - 결과: /servlet-mvc/members/save
- 절대 경로
  - 전체 경로를 다 포함하는 URL
  - /로 시작 ⭕️

### 회원 저장 - 컨트롤러

- /web/servletmvc/MvcMemberSaveServlet.java

```java
@WebServlet(name = "mvcMemberSaveServlet", urlPatterns = "/servlet-mvc/members/save")
public class MvcMemberSaveServlet extends HttpServlet {
    MemberRepository memberRepository = MemberRepository.getInstance();
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        int age = Integer.parseInt(request.getParameter("age"));

        Member member = new Member(username, age);
        memberRepository.save(member);

        // Model 데이터를 보관
        request.setAttribute("member", member);

        String viewPath = "/WEB-INF/views/save-result.jsp";
        RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);
        dispatcher.forward(request, response);

    }
}
```

- HttpServletRequest를 Model로 사용한다.
  - request가 제공하는 setAttribute() 를 사용하면 request 객체에 데이터를 보관
  - 뷰는 request.getAttribute() 를 사용해서 데이터를 꺼내면 된다.

### 회원 저장 폼 - 뷰

- webapp/WEB-INF/views/save-result.jsp

```html
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
성공
<ul>
    <li>id=${member.id}</li>
    <li>username=${member.username}</li>
    <li>age=${member.age}</li>
</ul>
<a href="/index.html"></a>
</body>
</html>
```

- JSP는 ${} 문법을 제공
- 이 문법을 사용하면 request의 attribute에 담긴 데이터를 편리하게 조회할 수 있다.

### 회원 목록 - 컨트롤러

- /web/servletmvc/MvcMemberListServlet.java
```java
@WebServlet(name = "mvcMemberListServlet", urlPatterns = "/servlet-mvc/members")
public class MvcMemberListServlet extends HttpServlet {
    MemberRepository memberRepository = MemberRepository.getInstance();
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Member> members = memberRepository.findAll();

        request.setAttribute("members", members);

        String viewPath = "/WEB-INF/views/members.jsp";

        RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);
        dispatcher.forward(request, response);
    }
}
```

### 회원 목록 - 뷰

- webapp/WEB-INF/views/members.jsp
```html
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
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
<c:forEach var="item" items="${members}">
     <tr>
         <td>${item.id}</td>
         <td>${item.username}</td>
         <td>${item.age}</td>
     </tr>
 </c:forEach>
 </tbody>
</table>
</body>
</html>
```

- <c:forEach> 이 기능을 사용하려면 다음과 같이 선언해야 한다.
  - <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
