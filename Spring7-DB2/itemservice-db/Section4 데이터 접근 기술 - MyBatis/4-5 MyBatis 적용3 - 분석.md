# MyBatis 적용3 - 분석

ItemMapper 매퍼 인터페이스의 구현체가 없는데 어떻게 동작한 것일까?

### 분석 

```java
@Mapper
public interface ItemMapper {
    void save(Item item);
    void update(@Param("id") Long id, @Param("updateParam") ItemUpdateDto updateParam);
    Optional<Item> findById(Long id);
    List<Item> findAll(ItemSearchCond itemSearch);
}
```
- 이 부분은 MyBatis 스프링 연동 모듈에서 자동으로 처리해주는데 다음과 같다.

![1.png](Image%2F1.png)
1. 애플리케이션 로딩 시점에 MyBatis 스프링 연동 모듈은 ```@Mapper```가 붙어있는 인터페이스를 조사한다
2. 해당 인터페이스가 발견되면 동적 프록시 기술을 사용해서 ```ItemMapper```인터페이스의 구현체를 만든다.
3. 생성된 구현체를 스프링 빈으로 등록한다.

실제 동적 프록시 기술이 사용되었는지 확인

MyBatisItemRepository - 로그 추가
```java
@Slf4j
@Repository
@RequiredArgsConstructor
public class MyBatisItemRepository implements ItemRepository {

    private final ItemMapper itemMapper;
    @Override
    public Item save(Item item) {
        log.info("itemMapper class={}", itemMapper.getClass());
        itemMapper.save(item);
        return item;
    }
    // 생략 ...
}
```

실행 결과
```text
itemMapper class=class jdk.proxy3.$Proxy69
```
- 출력해보면 JDK 동적 프록시가 적용된 것을 확인할 수 있다.

매퍼 구현체
- 마이바티스 스프링 연동 모듈이 만들어주는 ``ItemMapper``의 구현체 덕분에 인터페이스 만으로 편리하게 XML의 
  데이터를 찾아서 호출할 수 있다.
- 매퍼 구현체는 예외 변환까지 처리해준다. MyBatis에서 발생한 예외를 스프링 예외 추상화인
  ``DataAccessException``에 맞게 변환해서 반환해준다.
  - JdbcTemplate이 제공하는 예외 변환 기능을 여기서도 제공한다고 이해하면 된다.

정리
- 매퍼 구현체 덕분에 마이바티스를 스프링에 편리하게 통합해서 사용할 수 있다.
- 매퍼 구현체를 사용하면 스프링 예외 추상화도 함께 적용된다.
- 마이바티스 스프링 연동 모듈이 많은 부분을 자동으로 설정해주는데, 데이터베이스 커넥션, 트랜잭션과 관련된 기능도 
  마이바티스와 함께 연동하고, 동기화해준다.

참고
```text
마이바티스 스프링 연동 모듈이 자동으로 등록해주는 부분은 MybatisAutoConfiguration 클래스를 참고하자.
```

