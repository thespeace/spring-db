package thespeace.springdb.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import thespeace.springdb.repository.ItemRepository;
import thespeace.springdb.repository.jdbctemplate.JdbcTemplateItemRepositoryV1;
import thespeace.springdb.repository.memory.MemoryItemRepository;
import thespeace.springdb.service.ItemService;
import thespeace.springdb.service.ItemServiceV1;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class JdbcTemplateV1Config {

    private final DataSource dataSource;

    @Bean
    public ItemService itemService() {
        return new ItemServiceV1(itemRepository());
    }

    @Bean
    public ItemRepository itemRepository() {
        return new JdbcTemplateItemRepositoryV1(dataSource);
    }

}
