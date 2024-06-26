
# BeanFactory와 ApplicationContext

## 스프링 컨테이너 계층도
![스프링 컨테이너 계층도.JPG](0%20%EC%9D%B4%EB%AF%B8%EC%A7%80%2F%EC%8A%A4%ED%94%84%EB%A7%81%20%EC%BB%A8%ED%85%8C%EC%9D%B4%EB%84%88%20%EA%B3%84%EC%B8%B5%EB%8F%84.JPG)

⭐️ BeanFactory
- 스프링 컨테이너의 최상위 인터페이스
- 스프링 빈을 관리하고 조회하는 역할을 담당
- getBean() 제공
- 지금까지 우리가 사용했던 대부분의 기능 -> BeanFactory 제공 기능 

⭐️ ApplicationContext
- BeanFactory 기능을 모두 상속받아서 제공한다.
- 애플리케이션을 개발할 때는 빈을 관리하고 조회하는 기능은 물론이고, 수 많은 부가기능이 필요하다.

## ApplicatonContext가 제공하는 부가기능
![ApplicationContext 부가 기능.JPG](0%20%EC%9D%B4%EB%AF%B8%EC%A7%80%2FApplicationContext%20%EB%B6%80%EA%B0%80%20%EA%B8%B0%EB%8A%A5.JPG)

⭐️ 메시지소스를 활용한 국제화 기능
- 예를 들어서 한국에서 들어오면 한국어로, 영어권에서 들어오면 영어로 출력

⭐️ 환경변수
- 로컬, 개발, 운영등을 구분해서 처리

⭐️ 애플리케이션 이벤트
- 이벤트를 발행하고 구독하는 모델을 편리하게 지원

⭐️ 편리한 리소스 조회
- 파일, 클래스패스, 외부 등에서 리소스를 편리하게 조회 

💯 정리
- ApplicationContext는 BeanFactory의 기능을 상속받는다.
- ApplicationContext는 빈 관리기능 + 편리한 부가 기능을 제공한다.
- BeanFactory를 직접 사용할 일은 거의 없다. 부가기능이 포함된 ApplicationContext를 사용한다.
- BeanFactory나 ApplicationContext를 스프링 컨테이너라 한다.
