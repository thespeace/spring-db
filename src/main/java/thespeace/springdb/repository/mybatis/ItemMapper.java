package thespeace.springdb.repository.mybatis;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import thespeace.springdb.domain.Item;
import thespeace.springdb.repository.ItemSearchCond;
import thespeace.springdb.repository.ItemUpdateDto;

import java.util.List;
import java.util.Optional;

/**
 * <h1>마이바티스 매핑 XML을 호출해주는 매퍼 인터페이스</h1>
 * <ul>
 *     <li>이 인터페이스에는 @Mapper 애노테이션을 붙여주어야 한다. 그래야 MyBatis에서 인식할 수 있다.</li>
 *     <li>이 인터페이스의 메서드를 호출하면 다음에 보이는 xml 의 해당 SQL을 실행하고 결과를 돌려준다.</li>
 *     <li>이제 같은 위치에 실행할 SQL이 있는 XML 매핑 파일을 만들어주면 된다.<br>
 *         참고로 자바 코드가 아니기 때문에 `src/main/resources` 하위에 만들되, 패키지 위치는 맞추어 주어야 한다.</li>
 * </ul>
 *
 * @Reference : XML 파일 경로 수정하기 <br>XML 파일을 원하는 위치에 두고 싶으면 application.properties 에 다음과 같이 설정하면 된다.
 * <br>{@code mybatis.mapper-locations=classpath:mapper/**\*.xml}<br><br>이렇게 하면 resources/mapper 를 포함한 그 하위 폴더에 있는 XML을 XML 매핑 파일로 인식한다.<br>
 * 이 경우파일 이름은 자유롭게 설정해도 된다.<br>참고로 테스트의 application.properties 파일도 함께 수정해야 테스트를 실행할 때 인식할 수 있다
 */
@Mapper
public interface ItemMapper {

    void save(Item item);

    void update(@Param("id") Long id, @Param("updateParam")ItemUpdateDto updateParam);

    List<Item> findAll(ItemSearchCond itemSearch);

    Optional<Item> findById(Long id);
}
