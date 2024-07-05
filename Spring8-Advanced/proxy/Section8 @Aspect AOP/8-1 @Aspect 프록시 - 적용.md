# @Aspect 프록시 - 적용

- 스프링 애플리케이션에 프록시를 적용하려면 포인트컷과 어드바이스로 구성되어 있는 어드바이저(Advisor)를 만들어서 
  스프링 빈으로 등록하면 된다.
- 자동 프록시 생성기는 스프링 빈으로 등록된 어드바이저들을 찾고, 스프링 빈들에 자동으로 프록시를 적용해준다.
- 물론 포인트컷이 매칭되는 경우에 프록시를 생성한다.
- 스프링은 ```@Aspect```애노테이션으로 매우 편리하게 포인트컷과 어드바이스로 구성되어 있는 어드바이저 생성 기능을 지원한다.

### LogTraceAspect

```java
@Slf4j
@Aspect
public class LogTraceAspect {
    private final LogTrace logTrace;
    public LogTraceAspect(LogTrace logTrace) {
        this.logTrace = logTrace;
    }

    @Around("execution(* hello.proxy.app..*(..)) && !execution(* hello.proxy.app..noLog(..))") // 포인트컷
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable{  // Advice 로직
        TraceStatus status = null;
        try {
            String message = joinPoint.getSignature().toShortString();
            status = logTrace.begin(message);

            // 로직 호출
            Object result = joinPoint.proceed();

            logTrace.end(status);
            return result;
        }
        catch (Exception e){
            logTrace.exception(status, e);
            throw e;
        }
    }
}
```
- ```@Aspect```: 애노테이션 기반 프록시를 적용할 때 필요하다.
- ```@Around("execution(* hello.proxy.app..*(..))")```
  - ```@Around```의 값에 포인트컷 표현식을 넣는다. 표현식은 AspectJ 표현식을 사용한다.
  - ```@Around```의 메서드는 어드바이스(Advice)가 된다.
- ``ProceedingJoinPoint joinPoint``: 어드바이스에서 살펴본 ``MethodInvocation invocation``과 유사한 기능이다.
  내부에 실제 호출 대상, 전달 인자, 그리고 어떤 객체와 어떤 메서드가 호출되었는지 정보가 포함되어 있다.
- ``joinPoint.proceed()``: 실제 호출 대상(target)을 호출한다.

### AopConfig, ProxyApplication

AopConfig
```java
@Configuration
@Import({AppV1Config.class, AppV2Config.class})
public class AopConfig {
    @Bean
    public LogTraceAspect logTraceAspect(LogTrace trace){
        return new LogTraceAspect(trace);
    }
}
```
ProxyApplication
```java
@Import(AopConfig.class)
@SpringBootApplication(scanBasePackages = "hello.proxy.app") //주의
public class ProxyApplication {
	public static void main(String[] args) {
		SpringApplication.run(ProxyApplication.class, args);
	}
	@Bean
	LogTrace logTrace(){
		return new ThreadLocalLogTrace();
	}
}
```

실행해보면 모두 프록시가 잘 적용된 것을 확인할 수 있다.