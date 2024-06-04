
# 검증1 Validation - 오류 코드와 메시지 처리4

### MessageCodesResolverTest

```java
/**
 * MessageCodesResolver 동작 방식 이해
 */
class MessageCodesResolverTest {
    MessageCodesResolver codesResolver = new DefaultMessageCodesResolver();

    @Test
    void messageCodesResolverObject(){
        String[] messageCodes = codesResolver.resolveMessageCodes("required", "item");
        for (String messageCode : messageCodes) {
            System.out.println("messageCode = " + messageCode);
        }
        /*
        messageCode = required.item
        messageCode = required
         */
        assertThat(messageCodes).containsExactly("required.item", "required");

       // BindingResult.reject() -> new ObjectError("item", new String[]{"required.item", "required"});
    }

    @Test
    void messageCodesResolverField(){
        String[] messageCodes = codesResolver.resolveMessageCodes("required", "item", "itemName", String.class);
        for (String messageCode : messageCodes) {
            System.out.println("messageCode = " + messageCode);
        }
        /*
        messageCode = required.item.itemName
        messageCode = required.itemName
        messageCode = required.java.lang.String
        messageCode = required
         */

        /*
        BindingResult.rejectValue -> newFiledError("itemName", new String []{"required.item.itemName, required.itemName",
            "required.java.lang.String", "required" });
         */
        assertThat(messageCodes).containsExactly(
        "required.item.itemName",
        "required.itemName",
        "required.java.lang.String",
        "required");
    }
}
```
MessageCodesResolver
- 검증 오류 코드로 메시지 코드들을 생성한다.
- MessageCodesResolver 인터페이스이고 DefaultMessageCodesResolver 는 기본 구현체이다.
- 주로 다음과 함께 사용 ObjectError , FieldError

⭐️⭐️ DefaultMessageCodesResolver의 기본 메시지 생성 규칙

객체 오류 
```text
객체 오류의 경우 다음 순서로 2가지 생성
1): code + "." + object name
2): code

예) 오류 코드: required, object name: item
1): required.item
2): required
```

필드 오류
```text
필드 오류의 경우 다음 순서로 4가지 메시지 코드 생성
1): code + "." + object name + "." + field
2): code + "." + field
3): code + "." + field type
4): code

예) 오류 코드: typeMismatch, object name "user", field "age", field type: int
1) "typeMismatch.user.age"
2) "typeMismatch.age"
3) "typeMismatch.int"
4) "typeMismatch"
```

동작 방식 
- rejectValue() , reject() 는 내부에서 MessageCodesResolver 를 사용한다.
  - 메시지 코드 생성
- FieldError , ObjectError 의 생성자를 보면, 오류 코드를 하나가 아니라 여러 오류 코드를 가질 수 있다. 
  - MessageCodesResolver 를 통해서 생성된 순서대로 오류 코드를 보관한다.

동작 순서
```text
1. rejectValue() or reject() 

2. 넘겨준 파라미터 기반으로 defaultMessageCodesResolver: 메시지 코드 생성 

3. defaultMessageCodesResolver new FiledError() or new ObjectError)_ 파라미터 중 messageCodes
String[] 형태로 넘겨준다.

4. BindingResult 객체 생성 완료
```

오류 메시지 출력
- 타임리프 화면을 렌더링 할 때 th:errors 가 실행된다. 
- 만약 이때 오류가 있다면 생성된 오류 메시지 코드를 순서대로 돌아가면서 메시지를 찾는다. 
- 그리고 없으면 디폴트 메시지를 출력한다.


