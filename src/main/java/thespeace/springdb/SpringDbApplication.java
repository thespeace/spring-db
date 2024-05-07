package thespeace.springdb;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import thespeace.springdb.config.JdbcTemplateV1Config;
import thespeace.springdb.config.JdbcTemplateV2Config;
import thespeace.springdb.config.JdbcTemplateV3Config;
import thespeace.springdb.config.MemoryConfig;
import thespeace.springdb.repository.ItemRepository;

import javax.sql.DataSource;

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
@Slf4j
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

	/**
	 * <h2>테스트 - 임베디드 모드 DB 직접 사용</h2>
	 * H2 데이터베이스는 자바로 개발되어 있고, JVM안에서 메모리 모드로 동작하는 특별한 기능을 제공한다.
	 * 그래서 애플리케이션을 실행할 때 H2 데이터베이스도 해당 JVM 메모리에 포함해서 함께 실행할 수 있다.
	 * DB를 애플리케이션에 내장해서 함께 실행한다고 해서 임베디드 모드(Embedded mode)라 한다.<br>
	 * 물론 애플리케이션이 종료되면 임베디드 모드로 동작하는 H2 데이터베이스도 함께 종료되고, 데이터도 모두 사라진다.
	 * 쉽게 이야기해서 애플리케이션에서 자바 메모리를 함께 사용하는 라이브러리처럼 동작하는 것이다.
	 */
	@Bean
	@Profile("test") //프로필이 test 인 경우에만 데이터소스를 스프링 빈으로 등록.
	public DataSource dataSource() {
		log.info("메모리 데이터베이스 초기화");
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("org.h2.Driver");
		dataSource.setUrl("jdbc:h2:mem:db;DB_CLOSE_DELAY=-1"); //데이터소스를 만들때 임베디드(메모리 모드)로 동작하는 h2 데이터베이스를 사용할 수 있다.
		dataSource.setUsername("sa");
		dataSource.setPassword("");
		return dataSource;
	}

}
