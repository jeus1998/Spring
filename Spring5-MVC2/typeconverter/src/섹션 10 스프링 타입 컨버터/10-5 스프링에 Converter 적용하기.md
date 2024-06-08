
# 스프링에 Converter 적용하기

### WebConfig - 컨버터 등록

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
         registry.addConverter(new StringToIntegerConverter());
         registry.addConverter(new IntegerToStringConverter());
         registry.addConverter(new StringToIpPortConverter());
         registry.addConverter(new IpPortToStringConverter());
    }
}
```
- 스프링은 내부에서 ConversionService 를 제공한다. 
- 우리는 WebMvcConfigurer 가 제공하는 addFormatters() 를 사용해서 추가하고 싶은 컨버터를 등록하면 된다.
- 이렇게 하면 스프링은 내부에서 사용하는 ConversionService 에 컨버터를 추가해준다.

### 등록한 컨버터가 잘 동작하는지 확인해보자.

HelloController - 기존 코드
```java
@GetMapping("/hello-v2")
public String helloV2(@RequestParam Integer data) {
     System.out.println("data = " + data);
     return "ok";
}
```

실행
- http://localhost:8080/hello-v2?data=10

실행 로그
```text
StringToIntegerConverter : convert source=10
data = 10
```
- ?data=10 의 쿼리 파라미터는 문자이고 이것을 Integer data 로 변환하는 과정이 필요하다.
- 실행해보면 직접 등록한 StringToIntegerConverter 가 작동하는 로그를 확인할 수 있다.
- 그런데 생각해보면 StringToIntegerConverter 를 등록하기 전에도 이 코드는 잘 수행되었다.
- 그것은 스프링이 내부에서 수 많은 기본 컨버터들을 제공하기 때문이다.
- 컨버터를 추가하면 추가한 컨버터가 기본 컨버터 보다 높은 우선 순위를 가진다.


이번에는 직접 정의한 타입인 IpPort 를 사용해보자.

HelloController - 추가
```java
@GetMapping("/ip-port")
public String ipPort(@RequestParam IpPort ipPort){
    System.out.println("ipPort Ip = " + ipPort.getIp());
    System.out.println("ipPort Port = " + ipPort.getPort());

    return "ok";
}
```

실행
- http://localhost:8080/ip-port?ipPort=127.0.0.1:8080

실행 로그
```text
StringToIpPortConverter : convert source=127.0.0.1:8080
ipPort IP = 127.0.0.1
ipPort PORT = 8080
```

?ipPort=127.0.0.1:8080 쿼리 스트링이 @RequestParam IpPort ipPort 에서 객체 타입으로 잘 변환 된
것을 확인할 수 있다.

처리 과정
```text
@RequestParam 은 @RequestParam 을 처리하는 ArgumentResolver 인
RequestParamMethodArgumentResolver 에서 ConversionService 를 사용해서 타입을 변환한다.
```

