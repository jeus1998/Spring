
# 포맷터 - Formatter

- Converter 는 입력과 출력 타입에 제한이 없는, 범용 타입 변환 기능을 제공한다.
- 불린 타입을 숫자로 바꾸는 것 같은 범용 기능 보다는 개발자 입장에서는 문자를 다른 타입으로 변환하거나, 
  다른 타입을 문자로 변환하는 상황이 대부분이다.
- 문자를 다른 객체로 변환하거나 객체를 문자로 변환하는 일이 대부분이다.

웹 애플리케이션에서 객체를 문자로, 문자를 객체로 변환하는 예
- 화면에 숫자를 출력해야 하는데, Integer ➡️ String 출력 시점에 숫자 1000 문자 "1,000" 이렇게 1000 단위에 쉼표를 넣어서 출력
- "1,000" 라는 문자를 1000 이라는 숫자로 변경해야 한다
- 날짜 객체를 문자인 "2021-01-01 10:50:11" 와 같이 출력하거나 또는 그 반대의 상황

Locale
- 여기에 추가로 날짜 숫자의 표현 방법은 Locale 현지화 정보가 사용될 수 있다.
- 이렇게 객체를 특정한 포멧에 맞추어 문자로 출력하거나 또는 그 반대의 역할을 하는 것에 특화된 기능이 바로 포맷터
  (Formatter)이다.
- 포맷터는 컨버터의 특별한 버전으로 이해하면 된다.

Converter vs Formatter
- Converter 는 범용(객체 ➡️ 객체)
- Formatter 는 문자에 특화(객체 ➡️ 문자, 문자 ➡️ 객체) + 현지화(Locale)
  - Converter 의 특별한 버전

## 포맷터 - Formatter 만들기

- 포맷터(Formatter)는 객체를 문자로 변경하고, 문자를 객체로 변경하는 두 가지 기능을 모두 수행한다.
- String print(T object, Locale locale): 객체를 문자로 변경한다
- T parse(String text, Locale locale): 문자를 객체로 변경한다.

Formatter 인터페이스
```java
public interface Printer<T> {
    String print(T object, Locale locale);
}
public interface Parser<T> {
    T parse(String text, Locale locale) throws ParseException;
}
public interface Formatter<T> extends Printer<T>, Parser<T> {
}
```
- 숫자 1000 을 문자 "1,000" 으로 그러니까, 1000 단위로 쉼표가 들어가는 포맷을 적용해보자.
- 그 반대도 처리해주는 포맷터를 만들어보자.

### MyNumberFormatter
```java
@Slf4j
public class MyNumberFormatter implements Formatter<Number> {
    @Override
    public Number parse(String text, Locale locale) throws ParseException {
        log.info("text={}, locale={}", text, locale);
        // "1,000" -> 1000
        NumberFormat format =  NumberFormat.getInstance(locale);
        return format.parse(text); // Long
    }

    @Override
    public String print(Number object, Locale locale) {
        log.info("object={}, locale={}", object, locale);
        NumberFormat instance = NumberFormat.getInstance(locale);
        return instance.format(object);
    }
}
```
"1,000" 처럼 숫자 중간의 쉼표를 적용하려면 자바가 기본으로 제공하는 NumberFormat 객체를 사용하면 된다.
이 객체는 Locale 정보를 활용해서 나라별로 다른 숫자 포맷을 만들어준다.

parse() 를 사용해서 문자를 숫자로 변환한다. 참고로 Number 타입은 Integer, Long 과 같은 숫자 타입의 부모
클래스이다.
print() 를 사용해서 객체를 문자로 변환한다.

### MyNumberFormatterTest
```java
class MyNumberFormatterTest {
    MyNumberFormatter formatter = new MyNumberFormatter();
    @Test
    @DisplayName("String -> Integer (parse)")
    void parse() throws ParseException {
        Number result = formatter.parse("1,000", Locale.KOREA);
        assertThat(result).isEqualTo(1000L); // Long 타입 주의
    }

    @Test
    @DisplayName("Integer -> String (print)")
    void print() {
        String result = formatter.print(1000, Locale.KOREA);
        assertThat(result).isEqualTo("1,000");
    }
}
```
- parse() 의 결과가 Long 이기 때문에 isEqualTo(1000L) 을 통해 비교할 때 마지막에 L 을 넣어주어야 한다.

실행 결과 로그
```text
MyNumberFormatter - text=1,000, locale=ko_KR
MyNumberFormatter - object=1000, locale=ko_KR
```

✅참고
- 스프링은 용도에 따라 다양한 방식의 포맷터를 제공한다. 
  - Formatter 포맷터
  - AnnotationFormatterFactory 필드의 타입이나 애노테이션 정보를 활용할 수 있는 포맷터
- [포멧터 공식 문서](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#format)

