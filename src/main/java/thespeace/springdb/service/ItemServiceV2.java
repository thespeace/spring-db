package thespeace.springdb.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import thespeace.springdb.domain.Item;
import thespeace.springdb.repository.ItemSearchCond;
import thespeace.springdb.repository.ItemUpdateDto;
import thespeace.springdb.repository.v2.ItemQueryRepositoryV2;
import thespeace.springdb.repository.v2.ItemRepositoryV2;

import java.util.List;
import java.util.Optional;

/**
 * ItemServiceV2 는 ItemRepositoryV2 와 ItemQueryRepositoryV2 를 의존한다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ItemServiceV2 implements ItemService{

    //의존
    private final ItemRepositoryV2 itemRepositoryV2;
    private final ItemQueryRepositoryV2 itemQueryRepositoryV2;

    @Override
    public Item save(Item item) {
        return itemRepositoryV2.save(item);
    }

    @Override
    public void update(Long itemId, ItemUpdateDto updateParam) {
        Item findItem = itemRepositoryV2.findById(itemId).orElseThrow();
        findItem.setItemName(updateParam.getItemName());
        findItem.setPrice(updateParam.getPrice());
        findItem.setQuantity(updateParam.getQuantity());
    }

    @Override
    public Optional<Item> findById(Long id) {
        return itemRepositoryV2.findById(id);
    }

    @Override
    public List<Item> findItems(ItemSearchCond cond) {
        return itemQueryRepositoryV2.findAll(cond);
    }
}