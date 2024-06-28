package hello.itemservice.repository.v2;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemSearchCond;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import java.util.*;

import static hello.itemservice.domain.QItem.*;

/**
 * 복잡한 동적 쿼리
 */
@Repository
public class ItemQueryRepositoryV2 {
    private final JPAQueryFactory query;
    private final EntityManager em;
    public ItemQueryRepositoryV2(EntityManager em) {
        this.em = em;
        this.query = new JPAQueryFactory(em);
    }

    public List<Item> findAll(ItemSearchCond cond) {
        return query.select(item)
                    .from(item)
                    .where(likeItemName(cond.getItemName()), maxPrice(cond.getMaxPrice()))
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
