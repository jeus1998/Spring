# Querydsl 적용

### JpaItemRepositoryV3

```java
@Repository
@Transactional
public class JpaItemRepositoryV3 implements ItemRepository {
    private final EntityManager em;
    private final JPAQueryFactory query;
    public JpaItemRepositoryV3(EntityManager em) {
        this.em = em;
        this.query = new JPAQueryFactory(em);
    }

    @Override
    public Item save(Item item) {
        em.persist(item);
        return item;
    }

    @Override
    public void update(Long itemId, ItemUpdateDto updateParam) {
        Item findItem = em.find(Item.class, itemId);
        findItem.setItemName(updateParam.getItemName());
        findItem.setPrice(updateParam.getPrice());
        findItem.setQuantity(updateParam.getQuantity());
    }

    @Override
    public Optional<Item> findById(Long id) {
        Item item = em.find(Item.class, id);
        return Optional.ofNullable(item);
    }

    // @Override
    public List<Item> findAllOld(ItemSearchCond cond) {

        String itemName = cond.getItemName();
        Integer maxPrice = cond.getMaxPrice();

        QItem item = QItem.item;
        BooleanBuilder builder = new BooleanBuilder();

        if(StringUtils.hasText(itemName)){
            builder.and(item.itemName.like("%" + itemName + "%"));
        }

        if(maxPrice != null){
            builder.and(item.price.loe(maxPrice));
        }

        return  query
                .select(item)
                .from(item)
                .where(builder)
                .fetch();
    }
    @Override
    public List<Item> findAll(ItemSearchCond cond) {

           String itemName = cond.getItemName();
           Integer maxPrice = cond.getMaxPrice();

           return  query.select(item)
                   .from(item)
                   .where(likeItemName(itemName), maxPrice(maxPrice))
                   .fetch();
    }

    private BooleanExpression likeItemName(String itemName){
        if(StringUtils.hasText(itemName)){
           return item.itemName.like("%" + itemName + "%");
        }
        return null;
    }
    private BooleanExpression maxPrice(Integer maxPrice){
        if(maxPrice != null){
           return item.price.loe(maxPrice);
        }
        return null;
    }
}
```
공통
- ``Querydsl``을 사용하려면 ``JPAQueryFactory``가 필요하다. ``JPAQueryFactory``는 JPA 쿼리인 JPQL을 만들기 
  때문에 ``EntityManager``가 필요하다.
- 설정 방식은 ``JdbcTemplate``을 설정하는 것과 유사하다. 
- ``JPAQueryFactory``를 스프링 빈으로 등록해서 사용해도 된다.

save(), update(), findById()
- 기본 기능들은 JPA가 제공하는 기본 기능을 사용한다.

findAllOld()
- Querydsl을 사용해서 동적 쿼리 문제를 해결한다.
- BooleanBuilder 를 사용해서 원하는 where 조건들을 넣어주면 된다.
- 이 모든 것을 자바 코드로 작성하기 때문에 동적 쿼리를 매우 편리하게 작성할 수 있다.

findAll()
- 앞서 findAllOld 에서 작성한 코드를 깔끔하게 리팩토링 했다.
```java
List<Item> result = query
                     .select(item)
                     .from(item)
                     .where(likeItemName(itemName), maxPrice(maxPrice))
                     .fetch();
```
- Querydsl에서 where(A,B) 에 다양한 조건들을 직접 넣을 수 있는데, 이렇게 넣으면 AND 조건으로 처리된다.
  - ``where()``에 null 을 입력하면 해당 조건은 무시한다.
- 코드의 또 다른 장점은 ``likeItemName() , maxPrice()``를 다른 쿼리를 작성할 때 재사용 할 수 있다는 점이다

### QuerydslConfig, ItemServiceApplication - 변경

```java
@Configuration
@RequiredArgsConstructor
public class QuerydslConfig {

    private final EntityManager em;
    @Bean
    public ItemService itemService() {
        return new ItemServiceV1(itemRepository());
    }
    @Bean
    public ItemRepository itemRepository() {
        return new JpaItemRepositoryV3(em);
    }
}
```
```java
@Slf4j
@Import(QuerydslConfig.class)
@SpringBootApplication(scanBasePackages = "hello.itemservice.web")
public class ItemServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ItemServiceApplication.class, args);
	}
	@Bean
	@Profile("local")
	public TestDataInit testDataInit(ItemRepository itemRepository) {
		return new TestDataInit(itemRepository);
	}
}
```

예외 변환 
- ``Querydsl``은 별도의 스프링 예외 추상화를 지원하지 않는다. 
- 대신에 JPA에서 학습한 것 처럼 ```@Repository```에서 스프링 예외 추상화를 처리해준다


