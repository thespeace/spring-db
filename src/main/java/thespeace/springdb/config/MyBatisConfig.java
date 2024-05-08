package thespeace.springdb.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import thespeace.springdb.repository.ItemRepository;
import thespeace.springdb.repository.mybatis.ItemMapper;
import thespeace.springdb.repository.mybatis.MyBatisItemRepository;
import thespeace.springdb.service.ItemService;
import thespeace.springdb.service.ItemServiceV1;

@Configuration
@RequiredArgsConstructor
public class MyBatisConfig {

    /**
     * MyBatis 모듈이 데이터 소스나 트랜잭션 매니저 같은 걸 다 읽어서 해당 매퍼와 연결시켜준다.
     */
    private final ItemMapper itemMapper;

    @Bean
    public ItemService itemService() {
        return new ItemServiceV1(itemRepository());
    }

    @Bean
    public ItemRepository itemRepository() {
        return new MyBatisItemRepository(itemMapper);
    }

}
