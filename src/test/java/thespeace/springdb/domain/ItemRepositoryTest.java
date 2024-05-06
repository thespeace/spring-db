package thespeace.springdb.domain;

import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import thespeace.springdb.repository.ItemRepository;
import thespeace.springdb.repository.ItemSearchCond;
import thespeace.springdb.repository.ItemUpdateDto;
import thespeace.springdb.repository.memory.MemoryItemRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <h2>테스트 - 데이터베이스 분리</h2>
 * 로컬에서 사용하는 애플리케이션 서버와 테스트에서 같은 데이터베이스를 사용하고 있으니 테스트에서 문제가 발생한다.<br>
 * 이런 문제를 해결하려면 테스트를 다른 환경과 철저하게 분리해야 한다.<p><p>
 *
 * <h2>테스트에서 매우 중요한 원칙</h2>
 * <ul>
 *     <li>테스트는 다른 테스트와 격리해야 한다.</li>
 *     <li>테스트는 반복해서 실행할 수 있어야 한다.</li>
 * </ul>
 * 데이터베이스를 분리하여 격리를 하였고, 반복적인 실행 원칙을 지켜야하는데, 이때 `트랜잭션과 롤백 전략`이 도움이 된다.
 * <p><p>
 *
 * <h2>테스트 - 데이터 롤백</h2>
 * 테스트가 끝나고 나서 트랜잭션을 강제로 롤백해버리면 데이터가 깔끔하게 제거된다.<br>
 * 테스트를 하면서 데이터를 이미 저장했는데, 중간에 테스트가 실패해서 롤백을 호출하지 못해도 괜찮다.
 * 트랜잭션을 커밋하지 않았기 때문에 데이터베이스에 해당 데이터가 반영되지 않는다.<br>
 * 이렇게 트랜잭션을 활용하면 테스트가 끝나고 나서 데이터를 깔끔하게 원래 상태로 되돌릴 수 있다.<br><br>
 *
 * 테스트는 각각의 테스트 실행 전 후로 동작하는 {@code @BeforeEach}, {@code @AfterEach}라는 편리한 기능을 제공한다.
 *
 */
@Transactional
@SpringBootTest // @SpringBootApplication를 찾아서 설정으로 사용한다.
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    //트랜잭션 관리자는 PlatformTransactionManager를 주입 받아서 사용.
/*
    @Autowired
    PlatformTransactionManager transactionManager;
    TransactionStatus status; //Rollback시 필요.
*/


    /**
     * <h2>@BeforeEach</h2>
     * 각각의 테스트 케이스를 실행하기 직전에 호출된다.
     * 따라서 여기서 트랜잭션을 시작하면 된다. 그러면 각각의 테스트를 트랜잭션 범위 안에서 실행할 수 있다
     */
/*
    @BeforeEach
    void beforeEach() {
        //트랜잭션 시작
        status = transactionManager.getTransaction(new DefaultTransactionDefinition());
    }
*/

    /**
     * <h2>@AfterEach</h2>
     * 각각의 테스트 케이스가 완료된 직후에 호출된다.
     * 따라서 여기서 트랜잭션을 롤백하면 된다. 그러면 데이터를 트랜잭션 실행 전 상태로 복구할 수 있다.
     */
    @AfterEach
    void afterEach() {
        //MemoryItemRepository 의 경우 제한적으로 사용
        if (itemRepository instanceof MemoryItemRepository) {
            ((MemoryItemRepository) itemRepository).clearStore();
        }

        //트랜잭션 롤백
//        transactionManager.rollback(status);
    }

    @Test
    void save() {
        //given
        Item item = new Item("itemA", 10000, 10);

        //when
        Item savedItem = itemRepository.save(item);

        //then
        Item findItem = itemRepository.findById(item.getId()).get();
        assertThat(findItem).isEqualTo(savedItem);
    }

    @Test
    void updateItem() {
        //given
        Item item = new Item("item1", 10000, 10);
        Item savedItem = itemRepository.save(item);
        Long itemId = savedItem.getId();

        //when
        ItemUpdateDto updateParam = new ItemUpdateDto("item2", 20000, 30);
        itemRepository.update(itemId, updateParam);

        //then
        Item findItem = itemRepository.findById(itemId).get();
        assertThat(findItem.getItemName()).isEqualTo(updateParam.getItemName());
        assertThat(findItem.getPrice()).isEqualTo(updateParam.getPrice());
        assertThat(findItem.getQuantity()).isEqualTo(updateParam.getQuantity());
    }

    @Test
    void findItems() {
        //given
        Item item1 = new Item("itemA-1", 10000, 10);
        Item item2 = new Item("itemA-2", 20000, 20);
        Item item3 = new Item("itemB-1", 30000, 30);

        itemRepository.save(item1);
        itemRepository.save(item2);
        itemRepository.save(item3);

        //둘 다 없음 검증
        test(null, null, item1, item2, item3);
        test("", null, item1, item2, item3);

        //itemName 검증
        test("itemA", null, item1, item2);
        test("temA", null, item1, item2);
        test("itemB", null, item3);

        //maxPrice 검증
        test(null, 10000, item1);

        //둘 다 있음 검증
        test("itemA", 10000, item1);
    }

    void test(String itemName, Integer maxPrice, Item... items) {
        List<Item> result = itemRepository.findAll(new ItemSearchCond(itemName, maxPrice));
        assertThat(result).containsExactly(items);
    }
}
