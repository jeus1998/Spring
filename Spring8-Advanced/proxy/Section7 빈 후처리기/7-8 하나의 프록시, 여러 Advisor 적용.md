# 하나의 프록시, 여러 Advisor 적용

- Q: 어떤 스프링 빈이 ``advisor1, advisor2``가 제공하는 포인트컷의 조건을 모두 만족하면 프록시 자동 생성기는   
  프록시를 몇 개 생성할까?
- A: 프록시 자동 생성기는 프록시를 하나만 생성한다. 왜냐하면 프록시 팩토리가 생성하는 프록시는 내부에 여러
  ``advisor``들을 포함할 수 있기 때문이다. 따라서 프록시를 여러 개 생성해서 비용을 낭비할 이유가 없다.

프록시 자동 생성기 상황별 정리
- ``advisor1``의 포인트컷만 만족 ➡️ 프록시 1개 생성, 프록시에 ``advisor1``만 포함
- ``advisor1, advisor2``의 포인트컷을 모두 만족 ➡️ 프록시 1개 생성, 프록시에  ``advisor1, advisor2``모두 포함
- ``advisor1, advisor2``의 포인트컷을 모두 만족하지 않음 ➡️ 프록시가 생성되지 않음
- 스프링 AOP도 동일한 방식으로 동작한다.

자동 프록시 생성기 - ``AnnotationAwareAspectJAutoProxyCreator``
![7.png](Image%2F7.png)


하나의 프록시. 여러 어드바이저 
![8.png](Image%2F8.png)
