package thespeace.springdb.config;

import thespeace.springdb.repository.ItemRepository;
import thespeace.springdb.repository.memory.MemoryItemRepository;
import thespeace.springdb.service.ItemService;
import thespeace.springdb.service.ItemServiceV1;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MemoryConfig {

    @Bean
    public ItemService itemService() {
        return new ItemServiceV1(itemRepository());
    }

    @Bean
    public ItemRepository itemRepository() {
        return new MemoryItemRepository();
    }

}
