# MyBatis 적용2 - 설정과 실행

### MyBatisItemRepository

```java
@Repository
@RequiredArgsConstructor
public class MyBatisItemRepository implements ItemRepository {

    private final ItemMapper itemMapper;
    @Override
    public Item save(Item item) {
        itemMapper.save(item);
        return item;
    }
    @Override
    public void update(Long itemId, ItemUpdateDto updateParam) {
        itemMapper.update(itemId, updateParam);
    }
    @Override
    public Optional<Item> findById(Long id) {
        return itemMapper.findById(id);
    }
    @Override
    public List<Item> findAll(ItemSearchCond cond) {
        return itemMapper.findAll(cond);
    }
}
```
- ``MyBatisItemRepository``는 단순히 ``ItemMapper``에 기능을 위임한다.

### MyBatisConfig
```java
@Configuration
@RequiredArgsConstructor
public class MyBatisConfig {
    private final ItemMapper itemMapper;
    @Bean
    public ItemService itemService() {
        return new ItemServiceV1(itemRepository());
    }
    @Bean
    public ItemRepository itemRepository() {
        return new MyBatisItemRepository(itemMapper);
    }
}
```