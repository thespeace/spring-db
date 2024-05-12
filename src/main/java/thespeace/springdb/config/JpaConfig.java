package thespeace.springdb.config;

import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import thespeace.springdb.repository.ItemRepository;
import thespeace.springdb.repository.jpa.JpaItemRepository;
import thespeace.springdb.service.ItemService;
import thespeace.springdb.service.ItemServiceV1;

@Configuration
public class JpaConfig {

    private final EntityManager em;

    public JpaConfig(EntityManager em) {
        this.em = em;
    }

    @Bean
    public ItemService itemService() {
        return new ItemServiceV1(itemRepository());
    }

    @Bean
    public ItemRepository itemRepository() {
        return new JpaItemRepository(em);
    }

}
