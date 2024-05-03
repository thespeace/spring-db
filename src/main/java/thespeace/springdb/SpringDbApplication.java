package thespeace.springdb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import thespeace.springdb.config.JdbcTemplateV1Config;
import thespeace.springdb.config.JdbcTemplateV2Config;
import thespeace.springdb.config.JdbcTemplateV3Config;
import thespeace.springdb.config.MemoryConfig;
import thespeace.springdb.repository.ItemRepository;

/**
 * <ul>
 *     <li>@Import(MemoryConfig.class) : 앞서 설정한 MemoryConfig 를 설정 파일로 사용한다.</li>
 *     <li>scanBasePackages = "thespeace.springdb.web" : 여기서는 컨트롤러만 컴포넌트 스캔을 사용하고,
 * 		   나머지는 직접 수동 등록한다. 그래서 컴포넌트 스캔 경로를 thespeace.springdb.web 하위로 지정했다.</li>
 *     <li>@Profile("local") : 특정 프로필의 경우에만 해당 스프링 빈을 등록한다. 여기서는 local 이라는 이름의
 *     	   프로필이 사용되는 경우에만 testDataInit 이라는 스프링 빈을 등록한다. 이 빈은 앞서 본 것인데, 편의상
 *     	   초기 데이터를 만들어서 저장하는 빈이다.</li>
 * </ul>
 */
//@Import(MemoryConfig.class)
//@Import(JdbcTemplateV1Config.class)
//@Import(JdbcTemplateV2Config.class)
@Import(JdbcTemplateV3Config.class)
@SpringBootApplication(scanBasePackages = "thespeace.springdb.web")
public class SpringDbApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringDbApplication.class, args);
	}

	@Bean
	@Profile("local")
	public TestDataInit testDataInit(ItemRepository itemRepository) {
		return new TestDataInit(itemRepository);
	}

}
