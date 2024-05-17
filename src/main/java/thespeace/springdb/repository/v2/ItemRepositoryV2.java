package thespeace.springdb.repository.v2;

import org.springframework.data.jpa.repository.JpaRepository;
import thespeace.springdb.domain.Item;

/**
 * <h2>스프링 데이터 JPA의 기능을 제공하는 리포지토리</h2>
 * <ul>
 *     <li>ItemRepositoryV2 는 JpaRepository 를 인터페이스 상속 받아서 스프링 데이터 JPA의 기능을 제공하는
 *         리포지토리가 된다.</li>
 *     <li>기본 CRUD는 이 기능을 사용하면 된다.</li>
 *     <li>여기에 추가로 단순한 조회 쿼리들을 추가해도 된다.</li>
 * </ul>
 */
//Spring Data JPA는 Spring이 자동으로 Bean에 등록
public interface ItemRepositoryV2 extends JpaRepository<Item, Long> {
}
