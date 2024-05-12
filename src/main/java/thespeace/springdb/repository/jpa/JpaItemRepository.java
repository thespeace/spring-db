package thespeace.springdb.repository.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.extern.slf4j.Slf4j;
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
 * <h1>JPA 사용</h1>
 * <ul>
 *     <li>{@code private final EntityManager em} : 생성자를 보면 스프링을 통해 엔티티 매니저( EntityManager )
 *         라는 것을 주입받은 것을 확인할 수 있다. JPA의 모든 동작은 엔티티 매니저를 통해서 이루어진다. 엔티티 매니저는
 *         내부에 데이터소스를 가지고 있고, 데이터베이스에 접근할 수 있다.</li>
 *     <li>@Transactional : JPA의 모든 데이터 변경(등록, 수정, 삭제)은 트랜잭션 안에서 이루어져야 한다.
 *         조회는 트랜잭션이 없어도 가능하다. 변경의 경우 일반적으로 서비스 계층에서 트랜잭션을 시작하기 때문에 문제가 없다.
 *         하지만 이번 예제에서는 복잡한 비즈니스 로직이 없어서 서비스 계층에서 트랜잭션을 걸지 않았다. JPA에서는 데이터
 *         변경시 트랜잭션이 필수다. 따라서 리포지토리에 트랜잭션을 걸어주었다. 다시한번 강조하지만 일반적으로는 비즈니스
 *         로직을 시작하는 서비스 계층에 트랜잭션을 걸어주는 것이 맞다.</li>
 * </ul>
 */
@Slf4j
@Repository
@Transactional
public class JpaItemRepository implements ItemRepository {

    private final EntityManager em;

    public JpaItemRepository(EntityManager em) {
        this.em = em;
    }

    /**
     * <h2>저장</h2>
     * JPA에서 객체를 테이블에 저장할 때는 엔티티 매니저가 제공하는 persist() 메서드를 사용하면 된다.<br>
     * 객체의 맵핑 정보를 가지고 Insert Query만들어서 DB에 저장. == 자바 컬렉션.
     */
    @Override
    public Item save(Item item) {
        em.persist(item);
        return item;
    }

    /**
     * <h2>수정</h2>
     * <ul>
     *     <li>JPA는 트랜잭션이 커밋되는 시점에, 변경된 엔티티 객체가 있는지 확인한다. 특정 엔티티 객체가 변경된 경우에는
     *         UPDATE SQL을 실행한다.(JPA가 어떻게 변경된 엔티티 객체를 찾는지 명확하게 이해하려면 영속성 컨텍스트라는
     *         JPA 내부 원리를 이해해야 한다)</li>
     *     <li>테스트의 경우 마지막에 트랜잭션이 롤백되기 때문에 JPA는 UPDATE SQL을 실행하지 않는다. 테스트에서
     *         UPDATE SQL을 확인하려면 @Commit 을 붙이면 확인할 수 있다.</li>
     * </ul>
     */
    @Override
    public void update(Long itemId, ItemUpdateDto updateParam) {
        Item findItem = em.find(Item.class, itemId);
        findItem.setItemName(updateParam.getItemName());
        findItem.setPrice(updateParam.getPrice());
        findItem.setQuantity(updateParam.getQuantity());
    }

    /**
     * <h2>단건 조회</h2>
     * JPA에서 엔티티 객체를 PK를 기준으로 조회할 때는 find() 를 사용하고 조회 타입과, PK 값을 주면 된다.<br>
     * 그러면 JPA가 다음과 같은 조회 SQL을 만들어서 실행하고, 결과를 객체로 바로 변환해준다.
     */
    @Override
    public Optional<Item> findById(Long id) {
        Item item = em.find(Item.class, id);
        return Optional.ofNullable(item);
    }

    /**
     * <h2>목록 조회</h2>
     *
     * <h2>JPQL</h2>
     * JPA는 JPQL(Java Persistence Query Language)이라는 `객체지향 쿼리 언어`를 제공한다.<br>
     * 주로 여러 데이터를 복잡한 조건으로 조회할 때 사용한다.<br>
     * SQL이 테이블을 대상으로 한다면, JPQL은 엔티티 객체를 대상으로 SQL을 실행한다 생각하면 된다.<br>
     * SQL은 테이블을 대상으로 하지만 JPQL은 엔티티 객체를 대상으로 하기 때문에 from 다음에 Item 엔티티 객체 이름이 들어간다.<br>
     * 엔티티 객체와 속성의 대소문자는 구분해야 한다.<br>
     * JPQL은 SQL과 문법이 거의 비슷하기 때문에 개발자들이 쉽게 적응할 수 있다.<br>
     * 결과적으로 JPQL을 실행하면 그 안에 포함된 엔티티 객체의 매핑 정보를 활용해서 SQL을 만들게 된다.<p>
     *
     * <h2>JPQL에서의 파라미터</h2>
     * <ul>
     *     <li>{@code where price <= :maxPrice}</li>
     *     <li>파라미터 바인딩은 다음과 같이 사용한다.</li>
     *     <li>{@code query.setParameter("maxPrice", maxPrice) }</li>
     * </ul>
     * <br>
     * <h2>동적 쿼리 문제</h2>
     * JPA를 사용해도 동적 쿼리 문제가 남아있다.<br>
     * 동적 쿼리는 Querydsl이라는 기술을 활용하면 매우 깔끔하게 사용할 수 있다. 거의 선택이아니라 필수이다.
     */
    @Override
    public List<Item> findAll(ItemSearchCond cond) {
        String jpql = "select i from Item i";

        Integer maxPrice = cond.getMaxPrice();
        String itemName = cond.getItemName();

        if (StringUtils.hasText(itemName) || maxPrice != null) {
            jpql += " where";
        }

        boolean andFlag = false;
        if (StringUtils.hasText(itemName)) {
            jpql += " i.itemName like concat('%',:itemName,'%')";
            andFlag = true;
        }

        if (maxPrice != null) {
            if (andFlag) {
                jpql += " and";
            }
            jpql += " i.price <= :maxPrice";
        }

        log.info("jpql={}", jpql);

        TypedQuery<Item> query = em.createQuery(jpql, Item.class);
        if (StringUtils.hasText(itemName)) {
            query.setParameter("itemName", itemName);
        }
        if (maxPrice != null) {
            query.setParameter("maxPrice", maxPrice);
        }
        return query.getResultList();
    }
}
