# 스프링 트랜잭션 전파7 - REQUIRES_NEW

- 이번에는 외부 트랜잭션과 내부 트랜잭션을 완전히 분리해서 사용하는 방법에 대해서 알아보자.
- 외부 트랜잭션과 내부 트랜잭션을 완전히 분리해서 각각 별도의 물리 트랜잭션을 사용하는 방법이다
- 커밋과 롤백도 각각 별도로 이루어지게 된다.
- 이 방법은 내부 트랜잭션에 문제가 발생해서 롤백해도, 외부 트랜잭션에는 영향을 주지 않는다.
- 반대로 외부 트랜잭션에 문제가 발생해도 내부 트랜잭션에 영향을 주지 않는다.

REQUIRES_NEW
![15.png](Image%2F15.png)
- 이렇게 물리 트랜잭션을 분리하려면 내부 트랜잭션을 시작할 때 ``REQUIRES_NEW``옵션을 사용하면 된다.
- 외부 트랜잭션과 내부 트랜잭션이 각각 별도의 물리 트랜잭션을 가진다.
- 별도의 물리 트랜잭션을 가진다는 뜻은 DB 커넥션을 따로 사용한다는 뜻이다.
- 이 경우 내부 트랜잭션이 롤백되면서 로직 2가 롤백되어도 로직 1에서 저장한 데이터에는 영향을 주지 않는다
- 최종적으로 로직2는 롤백되고, 로직1은 커밋된다.

### inner_rollback_requires_new() - BasicTxTest 추가
```java
@Test
void inner_rollback_requires_new(){
    log.info("외부 트랜잭션 시작");
    TransactionStatus outer = txManager.getTransaction(new DefaultTransactionDefinition());
    log.info("outer.isNewTransaction()={}", outer.isNewTransaction());

    log.info("내부 트랜잭션 시작");
    DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
    definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    TransactionStatus inner = txManager.getTransaction(definition);
    log.info("inner.isNewTransaction()={}", inner.isNewTransaction()); // true

    log.info("내부 트랜잭션 롤백");
    txManager.rollback(inner); // 롤백

    log.info("외부 트랜잭션 커밋");
    txManager.commit(outer);  // 커밋
}
```
- 내부 트랜잭션을 시작할 때 전파 옵션인 ``propagationBehavior``에 ``PROPAGATION_REQUIRES_NEW``옵션을 주었다.
- 전파 옵션을 사용하면 내부 트랜잭션을 시작할 때 기존 트랜잭션에 참여하는 것이 아니라 새로운 물리 트랜잭션을 
  만들어서 시작하게 된다.

실행 결과 - inner_rollback_requires_new()
```text
외부 트랜잭션 시작
Creating new transaction with name [null]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT
Acquired Connection [HikariProxyConnection@1064414847 wrapping conn0] for JDBC transaction
Switching JDBC Connection [HikariProxyConnection@1064414847 wrapping conn0] to manual commit
outer.isNewTransaction()=true

내부 트랜잭션 시작
Suspending current transaction, creating new transaction with name [null]
Acquired Connection [HikariProxyConnection@778350106 wrapping conn1] for JDBC transaction
Switching JDBC Connection [HikariProxyConnection@778350106 wrapping conn1] to manual commit
inner.isNewTransaction()=true

내부 트랜잭션 롤백
Initiating transaction rollback
Rolling back JDBC transaction on Connection [HikariProxyConnection@778350106 wrapping conn1]
Releasing JDBC Connection [HikariProxyConnection@778350106 wrapping conn1] after transaction
Resuming suspended transaction after completion of inner transaction

외부 트랜잭션 커밋
Initiating transaction commit
Committing JDBC transaction on Connection [HikariProxyConnection@1064414847 wrapping conn0]
Releasing JDBC Connection [HikariProxyConnection@1064414847 wrapping conn0] after transaction
```

외부 트랜잭션 시작
- 외부 트랜잭션을 시작하면서 ``conn0``를 획득하고 ``manual commit``으로 변경해서 물리 트랜잭션을 시작한다.
- 외부 트랜잭션은 신규 트랜잭션이다.(outer.isNewTransaction()=true)

내부 트랜잭션 시작
- 내부 트랜잭션을 시작하면서 ``conn1``를 획득하고 ``manual commit``으로 변경해서 물리 트랜잭션을 시작한다.
- 내부 트랜잭션은 외부 트랜잭션에 참여하는 것이 아니라, ``PROPAGATION_REQUIRES_NEW ``옵션을 사용했기 때문에 
  완전히 새로운 신규 트랜잭션으로 생성된다.(inner.isNewTransaction()=true)

내부 트랜잭션 롤백
- 내부 트랜잭션을 롤백한다.
- 내부 트랜잭션은 신규 트랜잭션이기 때문에 실제 물리 트랜잭션을 롤백한다
- 내부 트랜잭션은 ``conn1``을 사용하므로 ``conn1``에 물리 롤백을 수행한다

외부 트랜잭션 커밋
- 외부 트랜잭션을 커밋한다.
- 외부 트랜잭션은 신규 트랜잭션이기 때문에 실제 물리 트랜잭션을 커밋한다.
- 외부 트랜잭션은 ``conn0``를 사용하므로 ``conn0``에 물리 커밋을 수행한다.

![15.png](Image%2F15.png)

요청 흐름 - REQUIRES_NEW
![16.png](Image%2F16.png)

요청 흐름 - 외부 트랜잭션
- ``txManager.getTransaction()``를 호출해서 외부 트랜잭션을 시작한다.
- 트랜잭션 매니저는 데이터소스를 통해 커넥션을 생성한다.
- 생성한 커넥션을 수동 커밋 모드(setAutoCommit(false))로 설정한다. - 물리 트랜잭션 시작
- 트랜잭션 매니저는 트랜잭션 동기화 매니저에 커넥션을 보관한다.
- 트랜잭션 매니저는 트랜잭션을 생성한 결과를 ``TransactionStatus``에 담아서 반환하는데, 여기에 신규
  트랜잭션의 여부가 담겨 있다. ``isNewTransaction``를 통해 신규 트랜잭션 여부를 확인할 수 있다. 트랜
  잭션을 처음 시작했으므로 신규 트랜잭션이다.(true)
- 로직1이 사용되고, 커넥션이 필요한 경우 트랜잭션 동기화 매니저를 통해 트랜잭션이 적용된 커넥션을 획득해서 사용한다.

요청 흐름 - 내부 트랜잭션
- ``REQUIRES_NEW``옵션과 함께 ``txManager.getTransaction()``를 호출해서 내부 트랜잭션을 시작한다.
- 트랜잭션 매니저는 ``REQUIRES_NEW``옵션을 확인하고, 기존 트랜잭션에 참여하는 것이 아니라 새로운 트랜잭션을 시작한다.
- 트랜잭션 매니저는 데이터소스를 통해 커넥션을 생성한다.
- 생성한 커넥션을 수동 커밋 모드(setAutoCommit(false))로 설정한다. - 물리 트랜잭션 시작
- 트랜잭션 매니저는 트랜잭션 동기화 매니저에 커넥션을 보관한다.
- 이때 ``con1``은 잠시 보류되고, 지금부터는 ``con2``가 사용된다. (내부 트랜잭션을 완료할 때 까지 ``con2``가 사용된다.)
- 트랜잭션 매니저는 신규 트랜잭션의 생성한 결과를 반환한다. ``isNewTransaction == true``
- 로직2가 사용되고, 커넥션이 필요한 경우 트랜잭션 동기화 매니저에 있는 ``con2``커넥션을 획득해서 사용한다.

응답 흐름 - REQUIRES_NEW
![17.png](Image%2F17.png)

응답 흐름 - 내부 트랜잭션
- 로직2가 끝나고 트랜잭션 매니저를 통해 내부 트랜잭션을 롤백한다.
- 내부 트랜잭션이 ``con2``물리 트랜잭션을 롤백한다.
- 트랜잭션이 종료되고, ``con2``는 종료되거나, 커넥션 풀에 반납된다.
- 이후에 ``con1``의 보류가 끝나고, 다시 ``con1``을 사용한다.

응답 흐름 - 외부 트랜잭션
- 외부 트랜잭션에 커밋을 요청한다.
- 외부 트랜잭션은 신규 트랜잭션이기 때문에 물리 트랜잭션을 커밋한다.
- 이때 ``rollbackOnly``설정을 체크한다. ``rollbackOnly``설정이 없으므로 커밋한다.
- 본인이 만든 ``con1``커넥션을 통해 물리 트랜잭션을 커밋한다.
- 트랜잭션이 종료되고, ``con1``은 종료되거나, 커넥션 풀에 반납된다.

정리
- ``REQUIRES_NEW``옵션을 사용하면 물리 트랜잭션이 명확하게 분리된다.
- ``REQUIRES_NEW``를 사용하면 데이터베이스 커넥션이 트랜잭션 동기화 매니저에 동시에 2개 사용된다는 점을 주의해야 한다.


