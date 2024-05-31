
# 서블릿 - HttpServletRequest 기본 사용법

### 시작 라인 

hello.servlet.basic.request.RequestHeaderServlet

```java
@WebServlet(name = "requestHeaderServlet", urlPatterns = "/request-header")
public class RequestHeaderServlet extends HttpServlet {
    
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        printStartLine(request);
        printHeaders(request);
    }

    private void printStartLine(HttpServletRequest request) {
          System.out.println("--- REQUEST-LINE - start ---");
          //GET
          System.out.println("request.getMethod() = " + request.getMethod());
          // HTTP/1.1
          System.out.println("request.getProtocol() = " + request.getProtocol());
          //http
          System.out.println("request.getScheme() = " + request.getScheme());
          // http://localhost:8080/request-header
          System.out.println("request.getRequestURL() = " + request.getRequestURL());
          // /request-header
          System.out.println("request.getRequestURI() = " + request.getRequestURI());
          //username=hi
          System.out.println("request.getQueryString() = " + request.getQueryString());
          //https 사용 유무
          System.out.println("request.isSecure() = " + request.isSecure());
          System.out.println("--- REQUEST-LINE - end ---" + "\n");
    }
}
```

```text
--- REQUEST-LINE - start ---
request.getMethod() = GET
request.getProtocol() = HTTP/1.1
request.getScheme() = http
request.getRequestURL() = http://localhost:8080/request-header
request.getRequestURI() = /request-header
request.getQueryString() = username=hi
request.isSecure() = false
--- REQUEST-LINE - end ---
```

### 모든 Header

```java
    // 모든 Header 조회
    private void printHeaders(HttpServletRequest request){
        System.out.println("--- Headers - start ---");
        
        request.getHeaderNames().asIterator().forEachRemaining(
                headerName -> System.out.println( headerName + "= " + request.getHeader(headerName)));

        System.out.println("--- Headers - end ---" + "\n");
    }
```

```text
--- Headers - start ---
host= localhost:8080
connection= keep-alive
sec-ch-ua= "Google Chrome";v="125", "Chromium";v="125", "Not.A/Brand";v="24"
sec-ch-ua-mobile= ?0
sec-ch-ua-platform= "Windows"
upgrade-insecure-requests= 1
user-agent= Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.0.0 Safari/537.36
accept= text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7
sec-fetch-site= same-origin
sec-fetch-mode= navigate
sec-fetch-user= ?1
sec-fetch-dest= document
referer= http://localhost:8080/basic.html
accept-encoding= gzip, deflate, br, zstd
accept-language= ko,en-US;q=0.9,en;q=0.8,ko-KR;q=0.7
cookie= Idea-43d89782=838654bb-a5f0-488b-ae0c-75543ba7a1cf
--- Headers - end ---
```

### Header 편리한 조회

```java
// Header 편리한 조회
    private void printHeaderUtils(HttpServletRequest request) {
        System.out.println("--- Header 편의 조회 start ---");
        System.out.println("[Host 편의 조회]");
        System.out.println("request.getServerName() = " + request.getServerName()); // Host 헤더
        System.out.println("request.getServerPort() = " + request.getServerPort()); // Host 헤더
        System.out.println();

        System.out.println("[Accept-Language 편의 조회]");
        request.getLocales().asIterator().forEachRemaining(
                locale -> System.out.println("locale = " + locale));
        System.out.println("request.getLocale() = " + request.getLocale());
        System.out.println();

        System.out.println("[cookie 편의 조회]");
        if(request.getCookies() != null){
            for (Cookie cookie : request.getCookies() ) {
                System.out.println(cookie.getName() + ": " + cookie.getValue());
            }
        }
        System.out.println();

        System.out.println("[Content 편의 조회]");
        System.out.println("request.getContentType() = " + request.getContentType());
        System.out.println("request.getContentLength() = " + request.getContentLength());
        System.out.println("request.getCharacterEncoding() = " + request.getCharacterEncoding());
        
    }
```


```text
--- Header 편의 조회 start ---
[Host 편의 조회]
request.getServerName() = localhost
request.getServerPort() = 8080
[Accept-Language 편의 조회]
locale = ko
locale = en_US
locale = en
locale = ko_KR
request.getLocale() = ko
[cookie 편의 조회]

[Content 편의 조회]
request.getContentType() = null
request.getContentLength() = -1
request.getCharacterEncoding() = UTF-8
--- Header 편의 조회 end ---
```

### 기타 정보

```java
// 기타 정보 - HTTP 메시지의 정보는 아니다.
    private void printEtc(HttpServletRequest request){
        System.out.println("--- 기타 조회 start ---");
        
        System.out.println("[Remote 정보]"); 
        System.out.println("request.getRemoteHost() = " + request.getRemoteHost()); //
        System.out.println("request.getRemoteAddr() = " + request.getRemoteAddr()); //
        System.out.println("request.getRemotePort() = " + request.getRemotePort()); 
        System.out.println();
        
        System.out.println("[Local 정보]");
        System.out.println("request.getLocalName() = " + request.getLocalName()); //
        System.out.println("request.getLocalAddr() = " + request.getLocalAddr()); //
        System.out.println("request.getLocalPort() = " + request.getLocalPort()); //
        
        System.out.println("--- 기타 조회 end ---");
        System.out.println();
    }
```

```text
--- 기타 조회 start ---
[Remote 정보]
request.getRemoteHost() = 0:0:0:0:0:0:0:1
request.getRemoteAddr() = 0:0:0:0:0:0:0:1
request.getRemotePort() = 54305

[Local 정보]
request.getLocalName() = localhost
request.getLocalAddr() = 0:0:0:0:0:0:0:1
request.getLocalPort() = 8080
--- 기타 조회 end ---
```

