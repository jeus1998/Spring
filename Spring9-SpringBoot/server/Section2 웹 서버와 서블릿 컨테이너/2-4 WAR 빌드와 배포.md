# WAR 빌드와 배포

- WAS에 우리가 만든 코드를 빌드하고 배포해보자

### 프로젝트 빌드

- 프로젝트 폴더로 이동
- 프로젝트 빌드
  - MAC OS: ```./gradlew build```
  - 윈도우 OS: ```gradlew build```
- WAR 파일 생성 확인
  - ``build/libs/server-0.0.1-SNAPSHOT.war``

참고
```text
plugins {
 id 'java'
 id 'war'
}
```
- ``build.gradle``에 보면 ``war``플러그인이 사용된 것을 확인할 수 있다. 이 플러그인이 ``war``파일을 만들어준다.

WAR 압축 풀기
- 우리가 빌드한 war 파일의 압축을 풀어서 내용물을 확인해보자.
- ``build/libs``폴더로 이동하자.
- 다음 명령어를 사용해서 압축을 풀자
  - ``jar -xvf server-0.0.1-SNAPSHOT.war``

WAR를 푼 결과
- WEB-INF
  - classes
    - hello/servlet/TestServlet.class
  - lib
    - akarta.servlet-api-6.0.0.jar
- index.html

### JAR, WAR 간단 소개

JAR 소개
- 자바는 여러 클래스와 리소스를 묶어서 JAR(Java Archive)라고 하는 압축 파일을 만들 수 있다.
- 해당 파일은 JVM 위에서 직접 실행되거나 또는 다른 곳에서 사용하는 라이브러리로 제공된다.
- 직접 실행하는 경우 main()메서드가 필요하고, ``MANIFEST.MF``파일에 실행할 메인 메서드가 있는 클래스를 지정해 두어야 한다.
- 실행 EX) `` java -jar abc.jar``
- Jar는 쉽게 이야기해서 클래스와 관련 리소스를 압축한 단순한 파일이다.
- 필요한 경우 이 파일을 직접 실행할 수도 있고, 다른 곳에서 라이브러리로 사용할 수도 있다.

WAR 소개
- WAR(Web Application Archive)라는 이름에서 알 수 있듯 WAR 파일은 웹 애플리케이션 서버(WAS)에 배포할 때 사용하는 파일이다.
- JAR 파일이 JVM 위에서 실행된다면, WAR는 웹 애플리케이션 서버 위에서 실행된다.
- 웹 애플리케이션 서버 위에서 실행되고, HTML 같은 정적 리소스와 클래스 파일을 모두 함께 포함하기 때문에 JAR와
  비교해서 구조가 더 복잡하다.
- 그리고 WAR 구조를 지켜야 한다.

WAR 구조
- WEB-INF
  - classes : 실행 클래스 모음
  - lib : 라이브러리 모음
  - web.xml : 웹 서버 배치 설정 파일(생략 가능)
  - WEB-INF 폴더 하위는 자바 클래스와 라이브러리, 그리고 설정 정보가 들어가는 곳이다.
- index.html : 정적 리소스
- WEB-INF 를 제외한 나머지 영역은 HTML, CSS 같은 정적 리소스가 사용되는 영역이다.

### WAR 배포

이렇게 생성된 WAR 파일을 톰캣 서버에 실제 배포해보자.

1. 톰캣 서버를 종료한다. - shutdown.bat
2. 톰캣폴더/webapps 하위를 모두 삭제한다. (예제 코드 or 전에 실행한 파일들 clean 빋드를 위해)
3. 빌드된 ``server-0.0.1-SNAPSHOT.war``를 복사한다.
4. 톰캣폴더/webapps 하위에 붙여넣는다. - 톰캣폴더/webapps/server-0.0.1-SNAPSHOT.war
5. 이름을 변경한다. - 톰캣폴더/webapps/ROOT.war  
6. 톰캣 서버를 실행한다. - startup.bat

주의!
- ``ROOT.war``에서 ``ROOT``는 대문자를 사용해야 한다.

실행 결과 확인
- http://localhost:8080/index.html
- http://localhost:8080/test

인털리제이, 이클립스 IDE 자동화
- 실제 서버에서는 이렇게 사용하면 되지만, 개발 단계에서는 war 파일을 만들고, 이것을 서버에 복사해서 배포하는 과정이 너무 번잡하다.
- 인텔리J나 이클립스 같은 IDE는 이 부분을 편리하게 자동화해준다.
