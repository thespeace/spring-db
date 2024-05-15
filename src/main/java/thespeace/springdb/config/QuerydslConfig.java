package thespeace.springdb.config;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import thespeace.springdb.repository.ItemRepository;
import thespeace.springdb.repository.jpa.JpaItemRepositoryV3;
import thespeace.springdb.service.ItemService;
import thespeace.springdb.service.ItemServiceV1;

@Configuration
@RequiredArgsConstructor
public class QuerydslConfig {

    private final EntityManager em;


    @Bean
    public ItemService itemService() {
        return new ItemServiceV1(itemRepository());
    }

    @Bean
    public ItemRepository itemRepository() {
        return new JpaItemRepositoryV3(em);
    }

}
