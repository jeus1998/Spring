# Querydsl 설정

스프링 부트 2.x와 스프링 부트 3.x의 설정이 다르다.

### 스프링 부트 2.x 설정 (build.gradle)

```text
dependencies{

    //Querydsl 추가
    implementation 'com.querydsl:querydsl-jpa'
    annotationProcessor "com.querydsl:querydsl-apt:${dependencyManagement.importedProperties['querydsl.version']}:jpa"
    annotationProcessor "jakarta.annotation:jakarta.annotation-api"
    annotationProcessor "jakarta.persistence:jakarta.persistence-api"
    
    // .. 생략 
}

//Querydsl 추가, 자동 생성된 Q클래스 gradle clean으로 제거
clean {
	delete file('src/main/generated')
}
```


### 스프링 부트 3.x 설정 (build.gradle)

```text
dependencies{

    //Querydsl 추가
    implementation 'com.querydsl:querydsl-jpa:5.0.0:jakarta'
    annotationProcessor "com.querydsl:querydsl-apt:${dependencyManagement.importedProperties['querydsl.version']}:jakarta"
    annotationProcessor "jakarta.annotation:jakarta.annotation-api"
    annotationProcessor "jakarta.persistence:jakarta.persistence-api"
    
    // .. 생략 
}

//Querydsl 추가, 자동 생성된 Q클래스 gradle clean으로 제거
clean {
	delete file('src/main/generated')
}
```
- 2.x 와 비교하면 다음 부분이 jpa ➡️ jakarta 로 변경되었다.

### 검증 - Q 타입 생성 확인 방법

2가지 옵션 선택 가능 
- Preferences➡️ Build, Execution, Deployment ➡️ Build Tools ➡️ Gradle
  - Gradle: Gradle을 통해서 빌드한다.
- Preferences➡️ Build, Execution, Deployment ➡️ Build Tools ➡️ IntelliJ IDEA
  - IntelliJ IDEA: IntelliJ가 직접 자바를 실행해서 빌드한다.

옵션 선택1 - Gradle - Q타입 생성 확인 방법

Gradle IntelliJ 사용법
- Gradle -> Tasks -> build -> clean
- Gradle -> Tasks -> other -> compileJava

Gradle 콘솔 사용법
- ./gradlew clean compileJava

Q 타입 생성 확인
- build -> generated -> sources -> annotationProcessor -> java/main
  - hello.itemservice.domain.QItem 이 생성되어 있어야 한다.

참고
```text
Q타입은 컴파일 시점에 자동 생성되므로 버전관리(GIT)에 포함하지 않는 것이 좋다.
gradle 옵션을 선택하면 Q타입은 gradle build 폴더 아래에 생성되기 때문에 여기를 포함하지 않아야 한다. 
대부분 gradle build 폴더를 git에 포함하지 않기 때문에 이 부분은 자연스럽게 해결된다. 
```

Q타입 삭제
- gradle clean 을 수행하면 build 폴더 자체가 삭제된다. 따라서 별도의 설정은 없어도 된다.


옵션 선택2 - IntelliJ IDEA - Q타입 생성 확인 방법

3가지중 하나 실행 
- Build -> Build Project
- Build -> Rebuild
- main()


Q 타입 생성 확인
- src/main/generated
  - hello.itemservice.domain.QItem 이 생성되어 있어야 한다.

참고
```text
Q타입은 컴파일 시점에 자동 생성되므로 버전관리(GIT)에 포함하지 않는 것이 좋다.
IntelliJ IDEA 옵션을 선택하면 Q타입은 src/main/generated 폴더 아래에 생성되기 때문에 여기를 포함하
지 않는 것이 좋다.
```

Q타입 삭제
```text
//Querydsl 추가, 자동 생성된 Q클래스 gradle clean으로 제거
clean {
    delete file('src/main/generated')
}
```
- IntelliJ IDEA 옵션을 선택하면 src/main/generated 에 파일이 생성되고, 필요한 경우 Q파일을 직접 삭제
  해야 한다.
- gradle 에 해당 스크립트를 추가하면 gradle clean 명령어를 실행할 때 src/main/generated 의 파일도 함께 삭제해준다.


