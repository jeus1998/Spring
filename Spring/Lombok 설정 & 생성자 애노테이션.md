
# Lombok 설정 & 생성자 애노테이션.md

Lombok은 다양한 생성자 관련 어노테이션을 제공하여 개발자가 반복적인 생성자 코드를 작성할 필요를 줄여준다.

## Lombok 설정 방법(IntelliJ IDEA, Gradle Project)

build.gradle Lombok 의존성 추가
```text
dependencies {
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'
}
```
IDE Lombok 플러그인 설치
- Settings ➡️  Plugins ➡️ Marketplace: "Lombok" 검색 후 설치
- Settings ➡️ Build, Execution, Deployment ➡️ Compiler ➡️ Annotation Processors: "Enable annotation processing" ☑️

클래스 작성 확인
```java
import lombok.Data;
@Data
public class LombokTest{
    private final String name;
}
```

클래스 작성 단계에서 import 에러 발생
- 터미널에서 ./gradlew clean build 실행

## Lombok 다양한 생성자 애노테이션 

### @RequiredArgsConstructor

- 설명: final, @Notnull(@Valid, @Validated) 필드를 매개변수로 하는 생성자를 자동으로 생성 
- 용도: 의존성 주입(DI)이나 불변 객체를 만들 때 사용, 생성자 주입을 통해 클래스 필드 초기화할 때 유용

```java
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MyClass {
    private final String name;
    private final int age;
    private String address;
}
/*
생성되는 생성자
public MyClass(String name, int age) {
     this.name = name;
     this.age = age;
}
*/
```

### @AllArgsConstructor

- 설명: 클래스의 모든 필드를 매개변수로 하는 생성자를 자동 생성 
- 모든 필드를 초기화할 수 있는 생성자가 필요할 때 사용
- DTO, 엔티티 클래스에서 유용

```java
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MyClass {
    private String name;
    private int age;
    private String address;
}

/*
생성되는 생성자
public MyClass(String name, int age, String address) {
     this.name = name;
     this.age = age;
     this.address = address;
} 
*/
```

### @NoArgsConstructor

- 설명: 매개변수가 없는 기본 생성자를 자동으로 생성
- 기본 생성자가 필요할 때 사용 
- JPA와 같은 ORM 프레임워크는 매개변수가 없는 생성자를 요구 

```java
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class MyClass {
    private String name;
    private int age;
    private String address;
}
/*
생성되는 생성자
public MyClass() {}
*/
```
### @Builder

- 설명: 빌더 패턴을 사용하여 객체를 생성할 수 있도록 한다.
- 복잡한 객체를 생성할 때 유용
- 많은 필드를 가진 클래스에서 가독성을 높이고 객체 생성 시 유연성을 제공

```java
import lombok.Builder;

@Builder
public class MyClass {
    private String name;
    private int age;
    private String address;
}
/*
MyClass 객체 생성 
MyClass myClass = MyClass.builder()
                         .name("John")
                         .age(30)
                         .address("123 Street")
                         .build();
 */
```

### @Data

- 설명: @NoArgsConstructor, @AllArgsConstructor 포함
  -  추가로 getter, setter, toString, equals, hashCode 메서드 제공 
- 간단한 데이터 객체(POJO)를 만들 때 유용

```java
import lombok.Data;

@Data
public class MyClass {
    private String name;
    private int age;
    private String address;
}
/*
 - 기본 생성자
 - 모든 필드를 포함하는 생성자
 - getter, setter, toString, equals, hashCode 메서드
*/
```

### @Value

- 설명: 불변 클래스를 생성, 모든 필드를 private final 선언, getter 메서드와 모든 필드를 초기화하는 생성자 생성
- 불변 객체를 만들 때 사용

```java
import lombok.Value;

@Value
public class MyClass {
    String name;
    int age;
    String address;
}
/*
- 모든 필드를 final 설정 
- 모든 필드를 포함하는 생성자
- getter 메서드
*/
```



