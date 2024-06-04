
# StringUtils

- org.springframework.util.StringUtils
- 검증 하거나 문자열 데이터 파싱에 사용 

### 주요 메서드 

✅ hasText
- 주어진 문자열이 null이 아니고, 공백만으로 구성되지 않은 경우 true를 반환
```java
boolean result1 = StringUtils.hasText("Hello"); // true
boolean result2 = StringUtils.hasText("   ");   // false
boolean result3 = StringUtils.hasText(null);    // false
```
- 내부 구조
```java
public static boolean hasText(@Nullable String str) {
    return (str != null && !str.isEmpty() && containsText(str));
}

private static boolean containsText(CharSequence str) {
    int strLen = str.length();
    for (int i = 0; i < strLen; i++) {
        if (!Character.isWhitespace(str.charAt(i))) {
            return true;
        }
    }
    return false;
}
```
- null ❌
- 빈 문자열 ❌
- 공백만으로 구성 ❌

✅ isEmpty
- 주어진 문자열이 null이거나 빈 문자열("")인 경우 true를 반환
```java
boolean result1 = StringUtils.isEmpty("");      // true
boolean result2 = StringUtils.isEmpty("Hello"); // false
boolean result3 = StringUtils.isEmpty(null);    // true
```

✅ hasLength
- 주어진 문자열이 null이 아니고, 길이가 0보다 큰 경우 true를 반환
```java
boolean result1 = StringUtils.hasLength("Hello"); // true
boolean result2 = StringUtils.hasLength("");      // false
boolean result3 = StringUtils.hasLength(null);    // false
```
✅ trimWhitespace
- 주어진 문자열의 앞뒤 공백을 제거
```java
String result = StringUtils.trimWhitespace("  Hello  "); // "Hello"
```

✅ collectionToCommaDelimitedString
- 컬렉션을 콤마로 구분된 문자열로 변환
```java
List<String> list = Arrays.asList("a", "b", "c");
String result = StringUtils.collectionToCommaDelimitedString(list); // "a,b,c"
```






