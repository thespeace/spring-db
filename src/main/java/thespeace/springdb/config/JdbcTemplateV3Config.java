package thespeace.springdb.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import thespeace.springdb.repository.ItemRepository;
import thespeace.springdb.repository.jdbctemplate.JdbcTemplateItemRepositoryV3;
import thespeace.springdb.service.ItemService;
import thespeace.springdb.service.ItemServiceV1;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class JdbcTemplateV3Config {

    private final DataSource dataSource;

    @Bean
    public ItemService itemService() {
        return new ItemServiceV1(itemRepository());
    }

    @Bean
    public ItemRepository itemRepository() {
        return new JdbcTemplateItemRepositoryV3(dataSource);
    }

}
