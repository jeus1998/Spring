package hello.itemservice.repository.v2;

import hello.itemservice.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 기본 CRUD
 */
public interface ItemRepositoryV2 extends JpaRepository<Item, Long> {
}
