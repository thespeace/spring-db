package thespeace.springdb.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import thespeace.springdb.repository.ItemRepository;
import thespeace.springdb.repository.jpa.JpaItemRepositoryV2;
import thespeace.springdb.repository.jpa.SpringDataJpaItemRepository;
import thespeace.springdb.service.ItemService;
import thespeace.springdb.service.ItemServiceV1;

@Configuration
@RequiredArgsConstructor
public class SpringDataJpaConfig {

    //SpringDataJpaItemRepository 는 스프링 데이터 JPA가 프록시 기술로 만들어주고 스프링 빈으로도 등록해준다.
    private final SpringDataJpaItemRepository springDataJpaItemRepository;

    @Bean
    public ItemService itemService() {
        return new ItemServiceV1(itemRepository());
    }

    @Bean
    public ItemRepository itemRepository() {
        return new JpaItemRepositoryV2(springDataJpaItemRepository);
    }

}
