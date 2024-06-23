# 트랜잭션 AOP 적용


### MemberServiceV3_3 

```java
/**
 * 트랜잭션 - @Transactional AOP
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV3_3 {

    private final MemberRepositoryV3 memberRepository;

    @Transactional
    public void accountTransfer(String fromId, String toId, int money) throws SQLException{
        bizLogic(fromId, toId, money);
    }

    private void bizLogic(String fromId, String toId, int money) throws SQLException {
        // 비즈니스 로직 시작
        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);

        memberRepository.update(fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(toId, toMember.getMoney() + money);
    }

    private static void validation(Member toMember) {
        if(toMember.getMemberId().equals("ex")){
            throw new IllegalStateException("이체중 예외 발생");
        }
    }
}
```
- 트랜잭션 AOP를 사용하는 새로운 서비스 클래스
- 순수한 비즈니스 로직만 남기고, 트랜잭션 관련 코드는 모두 제거
- 스프링이 제공하는 트랜잭션 AOP를 적용하기 위해 @Transactional 애노테이션을 추가
- @Transactional 애노테이션은 메서드에 붙여도 되고, 클래스에 붙여도 된다.
- 클래스에 붙이면 외부에서 호출 가능한 public 메서드가 AOP 적용 대상이 된다.

### MemberServiceV3_3Test

```java
@Slf4j
@SpringBootTest
class MemberServiceV3_3Test {
    private static final String MEMBER_A = "memberA";
    private static final String MEMBER_B = "memberB";
    private static final String MEMBER_EX = "ex";

    @Autowired
    private MemberServiceV3_3 memberService;
    @Autowired
    private MemberRepositoryV3 memberRepository;

    @TestConfiguration
    static class TestConfig{
        @Bean
        DataSource dataSource(){
            return new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        }
        @Bean
        PlatformTransactionManager transactionManager(){
            return new DataSourceTransactionManager(dataSource());
        }
        @Bean
        MemberRepositoryV3 memberRepositoryV3(){
            return new MemberRepositoryV3(dataSource());
        }
        @Bean
        MemberServiceV3_3 memberServiceV3_3(){
            return new MemberServiceV3_3(memberRepositoryV3());
        }
    }
    @Test
    void AopCheck(){
        log.info("출력 memberService class={}", memberService.getClass());
        log.info("출력 memberRepository class={}", memberRepository.getClass());
        assertThat(AopUtils.isAopProxy(memberService)).isTrue();
        // repository 에 @Repository 애노테이션이 붙어있고 checked 예외를 던진다면 예외 변환을 위해 프록시가 생성
        assertThat(AopUtils.isAopProxy(memberRepository)).isFalse();
    }
    @AfterEach
    void afterEach() throws SQLException {
        memberRepository.delete(MEMBER_A);
        memberRepository.delete(MEMBER_B);
        memberRepository.delete(MEMBER_EX);
    }

    @Test
    @DisplayName("정상 이체")
    void accountTransfer() throws SQLException {
        // given
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberB = new Member(MEMBER_B, 10000);
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        // when
        memberService.accountTransfer(memberA.getMemberId(), memberB.getMemberId(), 2000);

        // then
        Member findMemberA = memberRepository.findById(memberA.getMemberId());
        Member findMemberB = memberRepository.findById(memberB.getMemberId());

        assertThat(findMemberA.getMoney()).isEqualTo(8000);
        assertThat(findMemberB.getMoney()).isEqualTo(12000);
    }

    @Test
    @DisplayName("이체중 예외빌생")
    void accountTransferEx() throws SQLException {
        // given
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberEX = new Member(MEMBER_EX, 10000);
        memberRepository.save(memberA);
        memberRepository.save(memberEX);

        // when
        assertThatThrownBy(
                ()-> memberService.accountTransfer(memberA.getMemberId(), memberEX.getMemberId(), 2000)
        ).isInstanceOf(IllegalStateException.class);

        // then
        Member findMemberA = memberRepository.findById(memberA.getMemberId());
        Member findMemberEX = memberRepository.findById(memberEX.getMemberId());

        assertThat(findMemberA.getMoney()).isEqualTo(10000);
        assertThat(findMemberEX.getMoney()).isEqualTo(10000);
    }
}
```
- ``@SpringBootTest``
  - 스프링 AOP를 적용하려면 스프링 컨테이너가 필요하다.
  - 이 애노테이션이 있으면 테스트 스프링 부트를 통해 스프링 컨테이너를 생성한다. 
  - 테스트에서 @Autowired 등을 통해 스프링 컨테이너가 관리하는 빈들을 사용할 수 있다.
- ``@TestConfiguration``
  - 테스트 안에서 내부 설정 클래스를 만들어서 사용하면서 이 에노테이션을 붙이면, 스프링 부트가 
    자동으로 만들어주는 빈들에 추가로 필요한 스프링 빈들을 등록하고 테스트를 수행할 수 있다.
- ``TestConfig``
  - ``DataSource`` 스프링에서 기본으로 사용할 데이터소스를 스프링 빈으로 등록한다.
  - ``DataSourceTransactionManager`` 트랜잭션 매니저를 스프링 빈으로 등록한다.
     - 스프링이 제공하는 트랜잭션 AOP는 스프링 빈에 등록된 트랜잭션 매니저를 찾아서 사용하기 때문에
       트랜잭션 매니저를 스프링 빈으로 등록해두어야 한다.

AOP 프록시 적용 확인
```java
@Test
void AopCheck(){
    log.info("출력 memberService class={}", memberService.getClass());
    log.info("출력 memberRepository class={}", memberRepository.getClass());
    assertThat(AopUtils.isAopProxy(memberService)).isTrue();
    // repository 에 @Repository 애노테이션이 붙어있고 checked 예외를 던진다면 예외 변환을 위해 프록시가 생성
    assertThat(AopUtils.isAopProxy(memberRepository)).isFalse();
}
```

실행 결과 - AopCheck()
```text
memberService class=class hello.jdbc.service.MemberServiceV3_3$$EnhancerBySpringCGLIB$$...
memberRepository class=class hello.jdbc.repository.MemberRepositoryV3

먼저 AOP 프록시가 적용되었는지 확인해보자. AopCheck() 의 실행 결과를 보면 memberService 에
EnhancerBySpringCGLIB.. 라는 부분을 통해 프록시(CGLIB)가 적용된 것을 확인할 수 있다. 
memberRepository 에는 AOP를 적용하지 않았기 때문에 프록시가 적용되지 않는다.

나머지 테스트 코드들을 실행해보면 트랜잭션이 정상 수행되고, 실패시 정상 롤백된 것을 확인할 수 있다.

MemberRepositoryV3 (@Repository , checked 예외 던지기)
만약 이런 조합이면 스프링은 예외 변환을 위해서 MemberRepositoryV3 또한 프록시를 적용한다. 
```