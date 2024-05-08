package thespeace.springdb.repository.mybatis;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import thespeace.springdb.domain.Item;
import thespeace.springdb.repository.ItemRepository;
import thespeace.springdb.repository.ItemSearchCond;
import thespeace.springdb.repository.ItemUpdateDto;

import java.util.List;
import java.util.Optional;

/**
 * <h2>ItemRepository 를 구현</h2>
 * MyBatisItemRepository 는 단순히 ItemMapper 에 기능을 위임한다.
 */
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