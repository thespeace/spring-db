package thespeace.springdb.repository;

import thespeace.springdb.domain.Item;

import java.util.List;
import java.util.Optional;

/**
 * <p>메모리 구현체에서 향후 다양한 데이터 접근 기술 구현체로 손쉽게 변경하기 위해
 * 리포지토리에 인터페이스를 도입.</p>
 */
public interface ItemRepository {

    Item save(Item item);

    void update(Long itemId, ItemUpdateDto updateParam);

    Optional<Item> findById(Long id);

    List<Item> findAll(ItemSearchCond cond);

}
