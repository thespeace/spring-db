package thespeace.springdb.repository.jpa;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import thespeace.springdb.domain.Item;
import thespeace.springdb.domain.QItem;
import thespeace.springdb.repository.ItemRepository;
import thespeace.springdb.repository.ItemSearchCond;
import thespeace.springdb.repository.ItemUpdateDto;

import java.util.List;
import java.util.Optional;

import static thespeace.springdb.domain.QItem.*;

/**
 * <h1>Querydsl 적용</h1>
 *
 * <h2>공통</h2>
 * <ul>
 *     <li>Querydsl을 사용하려면 `JPAQueryFactory`가 필요하다. `JPAQueryFactory`는 JPA 쿼리인 JPQL을
 *         만들기 때문에 `EntityManager`가 필요하다.</li>
 *     <li>설정 방식은 `JdbcTemplate`을 설정하는 것과 유사하다.</li>
 *     <li>참고로 `JPAQueryFactory`를 스프링 빈으로 등록해서 사용해도 된다.</li>
 * </ul><p>
 *
 * <h2>예외 변환</h2>
 * Querydsl 은 별도의 스프링 예외 추상화를 지원하지 않는다. 대신에 JPA에서 학습한 것 처럼 @Repository 에서
 * 스프링 예외 추상화를 처리해준다.
 */
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

    /**
     * Querydsl을 사용해서 동적 쿼리 문제를 해결한다.<br>
     * BooleanBuilder 를 사용해서 원하는 where 조건들을 넣어주면 된다.<br>
     * 이 모든 것을 자바 코드로 작성하기 때문에 동적 쿼리를 매우 편리하게 작성할 수 있다.
     */
    public List<Item> findAllOld(ItemSearchCond cond) {

        String itemName = cond.getItemName();
        Integer maxPrice = cond.getMaxPrice();

//        QItem item = new QItem("i");
        QItem item = QItem.item;

        BooleanBuilder builder = new BooleanBuilder();

        if(StringUtils.hasText(itemName)) {
            builder.and(item.itemName.like("%" + itemName + "%"));
        }
        if(maxPrice != null) {
            builder.and(item.price.loe(maxPrice));
        }

        List<Item> result = query
                .select(item)
                .from(item)
                .where(builder)
                .fetch();

        return result;
    }

    /**
     * <h3>위의 findAllOld 코드를 깔끔하게 리팩토링</h3>
     *
     * <ul>
     *     <li>Querydsl에서 where(A,B) 에 다양한 조건들을 직접 넣을 수 있는데, 이렇게 넣으면 AND 조건으로 처리된다.
     *         참고로 where() 에 null 을 입력하면 해당 조건은 무시한다.</li>
     *     <li>이 코드의 또 다른 장점은 likeItemName() , maxPrice() 를 다른 쿼리를 작성할 때 재사용 할 수 있다는
     *         점이다. 쉽게 이야기해서 쿼리 조건을 부분적으로 모듈화 할 수 있다. 자바 코드로 개발하기 때문에 얻을 수
     *         있는 큰 장점이다.</li>
     * </ul>
     */
    @Override
    public List<Item> findAll(ItemSearchCond cond) {

        String itemName = cond.getItemName();
        Integer maxPrice = cond.getMaxPrice();

        return query
                .select(item)
                .from(item)
                .where(likeItemName(itemName), maxPrice(maxPrice))
                .fetch();
    }

    private BooleanExpression likeItemName(String itemName) {
        if(StringUtils.hasText(itemName)) {
            return item.itemName.like("%" + itemName + "%");
        }
        return null;
    }

    private BooleanExpression maxPrice(Integer maxPrice) {
        if(maxPrice != null) {
            return item.price.loe(maxPrice);
        }
        return null;
    }
}
