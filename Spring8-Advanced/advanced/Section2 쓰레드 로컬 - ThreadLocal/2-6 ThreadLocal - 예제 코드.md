# ThreadLocal - 예제 코드

### ThreadLocalService

```java
@Slf4j
public class ThreadLocalService {
    private ThreadLocal<String> nameStore = new ThreadLocal<>();
    public String logic(String name){
        log.info("저장 name={} -> nameStore={}", name, nameStore.get());
        nameStore.set(name);
        sleep(1000);
        log.info("조회 nameStore={}", nameStore.get());
        return nameStore.get();
    }
    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        }
        catch (InterruptedException e){
            log.info("sleep fail={}", e);
        }
    }
}
```
ThreadLocal 사용법
- 값 저장: ``ThreadLocal.set(xxx)``
- 값 조회: ``ThreadLocal.get()``
- 값 제거: ``ThreadLocal.remove()``

주의
- 해당 쓰레드가 쓰레드 로컬을 모두 사용하고 나면 ``ThreadLocal.remove()``를 호출해서 쓰레드 로컬에 저장된 값을 제거해주어야 한다.

### ThreadLocalServiceTest

```java
@Slf4j
public class ThreadLocalServiceTest {
    private final ThreadLocalService threadLocalService = new ThreadLocalService();
    @Test
    void field(){
        log.info("main start");

        Runnable userA = () ->{
            threadLocalService.logic("userA");
        };

        Runnable userB = () ->{
            threadLocalService.logic("userB");
        };

        Thread threadA = new Thread(userA);
        threadA.setName("thread-A");
        Thread threadB = new Thread(userB);
        threadB.setName("thread-B");

        threadA.start();
        sleep(2000); // 동시성 문제 발생 X
        threadB.start();

        sleep(3000); // 메인 쓰레드 종료 대기
        log.info("main exit");
    }
    private void sleep(int mills) {
        try {
            Thread.sleep(mills);
        }
        catch (InterruptedException e){
            log.info("sleep fail{}", e);
        }
    }
}
```

실행 결과
```text
[Test worker] main start
[Thread-A] 저장 name=userA -> nameStore=null
[Thread-B] 저장 name=userB -> nameStore=null
[Thread-A] 조회 nameStore=userA
[Thread-B] 조회 nameStore=userB
[Test worker] main exit
```
- 쓰레드 로컬 덕분에 쓰레드 마다 각각 별도의 데이터 저장소를 가지게 되었다. 결과적으로 동시성 문제도 해결되었다



