package thespeace.springdb.config;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import thespeace.springdb.repository.ItemRepository;
import thespeace.springdb.repository.jpa.JpaItemRepositoryV2;
import thespeace.springdb.repository.jpa.JpaItemRepositoryV3;
import thespeace.springdb.repository.jpa.SpringDataJpaItemRepository;
import thespeace.springdb.repository.v2.ItemQueryRepositoryV2;
import thespeace.springdb.repository.v2.ItemRepositoryV2;
import thespeace.springdb.service.ItemService;
import thespeace.springdb.service.ItemServiceV1;
import thespeace.springdb.service.ItemServiceV2;

/**
 * <h2>Spring Data JPA + Querydsl(실용적인 구조)</h2>
 * 스프링 데이터 JPA의 기능은 최대한 살리면서, Querydsl도 편리하게 사용할 수 있는 구조이다.<p><p>
 *
 * 이렇게 둘을 분리하면 기본 CRUD와 단순 조회는 스프링 데이터 JPA가 담당하고, 복잡한 조회 쿼리는 Querydsl이
 * 담당하게 된다.<br>
 * 물론 ItemService 는 기존 ItemRepository 를 사용할 수 없기 때문에 코드를 변경해야 한다.<p><p>
 * 
 * 이렇게 구조를 만들면 구조적인 유연성은 조금 떨어진다.<br>
 * 나중에 리포지토리를 바꾸거나 스프링 데이터 JPA를 다른 기술로 바꾸는 것은 유연성이 떨어지지만 실용적으로
 * 빠르게 개발할때는 효율적이다.<br>
 * 프로젝트가 점진적으로 커지면 그때 추상화에 대해 고민해도 늦지않다.
 */
@Configuration
@RequiredArgsConstructor
public class V2Config {

    private final EntityManager em;
    private final ItemRepositoryV2 itemRepositoryV2; //SpringDataJPA 제공

    @Bean
    public ItemService itemService() {
        return new ItemServiceV2(itemRepositoryV2, itemQueryRepositoryV2());
    }

    @Bean
    public ItemQueryRepositoryV2 itemQueryRepositoryV2() {
        return new ItemQueryRepositoryV2(em);
    }

    //테스트 데이터 초기화.(TestDataInit)
    @Bean
    public ItemRepository itemRepository() {
        return new JpaItemRepositoryV3(em);
    }

}
