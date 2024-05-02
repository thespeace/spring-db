package thespeace.springdb.repository.memory;

import thespeace.springdb.domain.Item;
import thespeace.springdb.repository.ItemRepository;
import thespeace.springdb.repository.ItemSearchCond;
import thespeace.springdb.repository.ItemUpdateDto;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <h2>인터페이스를 구현한 메모리 저장소</h2>
 */
@Repository
public class MemoryItemRepository implements ItemRepository {

    private static final Map<Long, Item> store = new HashMap<>(); //static
    private static long sequence = 0L; //static

    @Override
    public Item save(Item item) {
        item.setId(++sequence);
        store.put(item.getId(), item);
        return item;
    }

    @Override
    public void update(Long itemId, ItemUpdateDto updateParam) {
        Item findItem = findById(itemId).orElseThrow();
        findItem.setItemName(updateParam.getItemName());
        findItem.setPrice(updateParam.getPrice());
        findItem.setQuantity(updateParam.getQuantity());
    }

    @Override
    public Optional<Item> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    /**
     * <h2>검색 기능</h2>
     * ItemSearchCond 이라는 검색 조건을 받아서 내부에서 데이터를 검색하는 기능을 한다. 데이터
     * 베이스로 보면 where 구문을 사용해서 필요한 데이터를 필터링 하는 과정을 거치는 것이다.
     * <ul>
     *     <li>여기서 자바 스트림을 사용한다.</li>
     *     <li>itemName 이나, maxPrice 가 null 이거나 비었으면 해당 조건을 무시한다.</li>
     *     <li>itemName 이나, maxPrice 에 값이 있을 때만 해당 조건으로 필터링 기능을 수행한다.</li>
     * </ul>
     */
    @Override
    public List<Item> findAll(ItemSearchCond cond) {
        String itemName = cond.getItemName();
        Integer maxPrice = cond.getMaxPrice();
        return store.values().stream()
                .filter(item -> {
                    if (ObjectUtils.isEmpty(itemName)) {
                        return true;
                    }
                    return item.getItemName().contains(itemName);
                }).filter(item -> {
                    if (maxPrice == null) {
                        return true;
                    }
                    return item.getPrice() <= maxPrice;
                })
                .collect(Collectors.toList());
    }

    /**
     * 메모리에 저장된 Item 을 모두 삭제해서 초기화, 테스트 용도로만 사용한다.
     */
    public void clearStore() {
        store.clear();
    }

}
