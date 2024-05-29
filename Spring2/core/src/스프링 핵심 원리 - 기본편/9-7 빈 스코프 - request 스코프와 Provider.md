
# 빈 스코프 - request 스코프와 Provider


### ObjectProvider 적용하기 

/main/java/hello/core/web/LogDemoController.java
```java
@Controller
@RequiredArgsConstructor
public class LogDemoController {

    private final LogDemoService logDemoService;
    private final ObjectProvider<MyLogger> objectProvider;
    @RequestMapping("log-demo")
    @ResponseBody
    public String logDemo(HttpServletRequest request){
        String requestUrl = request.getRequestURI().toString();

        MyLogger myLogger = objectProvider.getObject();

        myLogger.setRequestUrl(requestUrl);

        myLogger.log("controller test");
        logDemoService.logic("testId");

        return "OK";
    }
}
````

/main/java/hello/core/web/LogDemoService.java
```java
@Service
@RequiredArgsConstructor
public class LogDemoService {
    private final ObjectProvider<MyLogger> objectProvider;
    public void logic(String id){
        MyLogger myLogger = objectProvider.getObject();
        myLogger.log("service id = " + id);
    }
}
```

main() 메서드로 스프링을 실행하고, 웹 브라우저에 http://localhost:8080/log-demo 를 입력하자
드디어 잘 작동하는 것을 확인할 수 있다.

```text
[d06b992f...] request scope bean create
[d06b992f...][http://localhost:8080/log-demo] controller test
[d06b992f...][http://localhost:8080/log-demo] service id = testId
[d06b992f...] request scope bean close
````

- ObjectProvider 덕분에 ObjectProvider.getObject() 를 호출하는 시점까지 request scope 빈의
  생성을 지연할 수 있다.
- ObjectProvider.getObject() 를 호출하시는 시점에는 HTTP 요청이 진행중이므로 request scope 빈
  의 생성이 정상 처리된다.
- ObjectProvider.getObject() 를 LogDemoController , LogDemoService 에서 각각 한번씩 따로
  호출해도 같은 HTTP 요청이면 같은 스프링 빈이 반환된다!

