package thespeace.springdb.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import thespeace.springdb.domain.Item;

import java.util.List;

/**
 * <h1>스프링 데이터 JPA 적용</h1>
 * <ul>
 *     <li>스프링 데이터 JPA가 제공하는 JpaRepository 인터페이스를 인터페이스 상속 받으면 기본적인 CRUD 기능을
 *         사용할 수 있다.</li>
 *     <li>그런데 이름으로 검색하거나, 가격으로 검색하는 기능은 공통으로 제공할 수 있는 기능이 아니다.
 *         따라서 쿼리 메서드 기능을 사용하거나 @Query 를 사용해서 직접 쿼리를 실행하면 된다.</li>
 * </ul>
 *
 * 여기서는 데이터를 조건에 따라 4가지로 분류해서 검색한다.
 * <ul>
 *     <li>모든 데이터 조회</li>
 *     <li>이름 조회</li>
 *     <li>가격 조회</li>
 *     <li>이름 + 가격 조회</li>
 * </ul>
 * 동적 쿼리를 사용하면 좋겠지만, 스프링 데이터 JPA는 동적 쿼리에 약하다. 이번에는 직접 4가지 상황을
 * 스프링 데이터 JPA로 구현해보자(이 문제는 이후에 Querydsl에서 동적 쿼리로 깔끔하게 해결.)
 *
 * @reference : 스프링 데이터 JPA도 Example 이라는 기능으로 약간의 동적 쿼리를 지원하지만, 실무에서 사용하기는 기능이
 * 빈약하다. 실무에서 JPQL 동적 쿼리는 Querydsl을 사용하는 것이 좋다.
 */
public interface SpringDataJpaItemRepository extends JpaRepository<Item, Long> {

    /**
     * <h2>이름 조건만 검색했을 때 사용하는 쿼리 메서드</h2>
     * 실행되는 JPQL : {@code select i from Item i where i.name like ? }
     */
    List<Item> findByItemNameLike(String itemName);

    /**
     * <h2>가격 조건만 검색했을 때 사용하는 쿼리 메서드</h2>
     * 실행되는 JPQL : {@code select i from Item i where i.price <= ? }
     */
    List<Item> findByPriceLessThanEqual(Integer price);

    /**
     * <h2>이름과 가격 조건을 검색했을 때 사용하는 쿼리 메서드</h2>
     * 실행되는 JPQL : {@code select i from Item i where i.itemName like ? and i.price <= ? }
     */
    List<Item> findByItemNameLikeAndPriceLessThanEqual(String itemName, Integer price);

    /**
     * <h2>쿼리 직접 실행(위 메서드와 같은 기능 수행)</h2>
     * 위 메서드처럼 이름으로 쿼리를 실행하는 기능은 다음과 같은 단점이 있다.
     * <ol>
     *     <li>조건이 많으면 메서드 이름이 너무 길어진다.</li>
     *     <li>조인 같은 복잡한 조건을 사용할 수 없다.</li>
     * </ol>
     *
     * 메서드 이름으로 쿼리를 실행하는 기능은 간단한 경우에는 매우 유용하지만, 복잡해지면 직접 JPQL 쿼리를 작성하는
     * 것이 좋다.
     *
     * <ul>
     *     <li>쿼리를 직접 실행하려면 @Query 애노테이션을 사용하면 된다.</li>
     *     <li>메서드 이름으로 쿼리를 실행할 때는 파라미터를 순서대로 입력하면 되지만, 쿼리를 직접 실행할 때는 파라미터를
     *         명시적으로 바인딩 해야 한다.</li>
     *     <li>파라미터 바인딩은 @Param("itemName") 애노테이션을 사용하고, 애노테이션의 값에 파라미터 이름을 주면
     *         된다.</li>
     * </ul>
     */
    @Query("select i from Item i where i.itemName like :itemName and i.price <= :price")
    List<Item> findItems(@Param("itemName") String itemName, @Param("price") Integer price);

    //findAll() : 코드에는 보이지 않지만 JpaRepository 공통 인터페이스가 제공하는 기능으로 모든 Item 을 조회한다.
    //            실행되는 JPQL : `select i from Item i`
}
