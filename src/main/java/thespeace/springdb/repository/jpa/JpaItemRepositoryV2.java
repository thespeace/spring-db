package thespeace.springdb.repository.jpa;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import thespeace.springdb.domain.Item;
import thespeace.springdb.repository.ItemRepository;
import thespeace.springdb.repository.ItemSearchCond;
import thespeace.springdb.repository.ItemUpdateDto;

import java.util.List;
import java.util.Optional;

/**
 * <h1>스프링 데이터 JPA 적용</h1>
 * @see spring-db/docs/12.Spring_Data_JPA_apply.md
 */
@Repository
@Transactional
@RequiredArgsConstructor
public class JpaItemRepositoryV2 implements ItemRepository {

    private final SpringDataJpaItemRepository repository;

    @Override
    public Item save(Item item) {
        return repository.save(item);
    }

    @Override
    public void update(Long itemId, ItemUpdateDto updateParam) {
        Item findItem = repository.findById(itemId).orElseThrow();
        findItem.setItemName(updateParam.getItemName());
        findItem.setPrice(updateParam.getPrice());
        findItem.setQuantity(updateParam.getQuantity());
    }

    @Override
    public Optional<Item> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<Item> findAll(ItemSearchCond cond) {
        String itemName = cond.getItemName();
        Integer maxPrice = cond.getMaxPrice();

        if(StringUtils.hasText(itemName) && maxPrice != null) {
//            return repository.findByItemNameLikeAndPriceLessThanEqual("%" + itemName + "%", maxPrice);

            return repository.findItems("%" + itemName + "%", maxPrice);
        } else if(StringUtils.hasText(itemName)) {
            return repository.findByItemNameLike("%" + itemName + "%");
        } else if(maxPrice != null) {
            return repository.findByPriceLessThanEqual(maxPrice);
        } else {
            return repository.findAll();
        }
    }
}
