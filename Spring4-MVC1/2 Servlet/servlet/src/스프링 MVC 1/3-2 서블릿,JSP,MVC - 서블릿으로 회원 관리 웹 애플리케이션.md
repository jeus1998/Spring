
# 서블릿으로 회원 관리 웹 애플리케이션

### MemberFormServlet - 회원 등록 폼

- /web/servlet/MemberFormServlet.java

```java
@WebServlet(name = "memberFormServlet", urlPatterns = "/servlet/members/new-form")
public class MemberFormServlet extends HttpServlet {
    private MemberRepository memberRepository = MemberRepository.getInstance();
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("utf-8");

        PrintWriter w = response.getWriter();
        w.write("<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "<meta charset=\"UTF-8\">\n" +
                "<title>Title</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "<form action=\"/servlet/members/save\" method=\"post\">\n" +
                " username: <input type=\"text\" name=\"username\" />\n" +
                " age: <input type=\"text\" name=\"age\" />\n" +
                " <button type=\"submit\">전송</button>\n" +
                "</form>\n" +
                "</body>\n" +
                "</html>\n"
        );
    }
}
```

### MemberSaveServlet - 회원 저장

- /web/servlet/MemberSaveServlet.java

```java
@WebServlet(name = "memberSaveServlet", urlPatterns = "/servlet/members/save")
public class MemberSaveServlet extends HttpServlet {
    private MemberRepository memberRepository = MemberRepository.getInstance();
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        int age = Integer.parseInt(request.getParameter("age"));

        Member member = new Member(username, age);
        memberRepository.save(member);

        response.setContentType("text/html");
        response.setCharacterEncoding("utf-8");
        PrintWriter w = response.getWriter();
        w.write(  "<html>\n" +
                     "<head>\n" +
                     "<meta charset=\"UTF-8\">\n" +
                     "</head>\n" +
                     "<body>\n" +
                     "성공\n" +
                     "<ul>\n" +
                     " <li>id="+member.getId()+"</li>\n" +
                     " <li>username="+member.getUsername()+"</li>\n" +
                     " <li>age="+member.getAge()+"</li>\n" +
                     "</ul>\n" +
                     "<a href=\"/index.html\">메인</a>\n" +
                     "</body>\n" +
                     "</html>"
        );
    }
}
```

### MemberListServlet - 회원 목록

- - /web/servlet/MemberListServlet

```java
@WebServlet(name = "memberListServlet", urlPatterns = "/servlet/members")
public class MemberListServlet extends HttpServlet {
    private MemberRepository memberRepository = MemberRepository.getInstance();
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("text/html");
        response.setCharacterEncoding("utf-8");
        List<Member> members = memberRepository.findAll();
        PrintWriter w = response.getWriter();
             w.write("<html>");
             w.write("<head>");
             w.write(" <meta charset=\"UTF-8\">");
             w.write(" <title>Title</title>");
             w.write("</head>");
             w.write("<body>");
             w.write("<a href=\"/index.html\">메인</a>");
             w.write("<table>");
             w.write(" <thead>");
             w.write(" <th>id</th>");
             w.write(" <th>username</th>");
             w.write(" <th>age</th>");
             w.write(" </thead>");
             w.write(" <tbody>");
             for (Member member : members) {
             w.write(" <tr>");
             w.write(" <td>" + member.getId() + "</td>");
             w.write(" <td>" + member.getUsername() + "</td>");
             w.write(" <td>" + member.getAge() + "</td>");
             w.write(" </tr>");
             }
             w.write(" </tbody>");
             w.write("</table>");
             w.write("</body>");
             w.write("</html>");
    }
}
```

### 템플릿 엔진으로

지금까지 서블릿과 자바 코드만으로 HTML을 만들어보았다. 서블릿 덕분에 동적으로 원하는 HTML을 마음껏 만들 수
있다. 정적인 HTML 문서라면 화면이 계속 달라지는 회원의 저장 결과라던가, 회원 목록 같은 동적인 HTML을 만드는
일은 불가능 할 것이다.
그런데, 코드에서 보듯이 이것은 매우 복잡하고 비효율 적이다. 자바 코드로 HTML을 만들어 내는 것 보다 차라리
HTML 문서에 동적으로 변경해야 하는 부분만 자바 코드를 넣을 수 있다면 더 편리할 것이다.
이것이 바로 템플릿 엔진이 나온 이유이다. 템플릿 엔진을 사용하면 HTML 문서에서 필요한 곳만 코드를 적용해서 동
적으로 변경할 수 있다.
템플릿 엔진에는 JSP, Thymeleaf, Freemarker, Velocity등이 있다.
다음 시간에는 JSP로 동일한 작업을 진행해보자

