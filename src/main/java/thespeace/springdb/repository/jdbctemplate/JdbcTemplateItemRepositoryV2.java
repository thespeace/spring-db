package thespeace.springdb.repository.jdbctemplate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.util.StringUtils;
import thespeace.springdb.domain.Item;
import thespeace.springdb.repository.ItemRepository;
import thespeace.springdb.repository.ItemSearchCond;
import thespeace.springdb.repository.ItemUpdateDto;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * <h1>NamedParameterJdbcTemplate</h1>
 * JdbcTemplate의 파라미터 바인딩 순서로 인한 버그를 보완하기 위해 이름을 지정해서 파라미터를 바인딩하는 기능을 제공한다.
 *
 */
@Slf4j
public class JdbcTemplateItemRepositoryV2 implements ItemRepository {

    private final NamedParameterJdbcTemplate template;

    public JdbcTemplateItemRepositoryV2(DataSource dataSource) {
        this.template = new NamedParameterJdbcTemplate(dataSource);
    }

    /**
     * <h2>데이터 저장</h2>
     * <ul>
     *     <li>SQL에서 다음과 같이 `?` 대신에 `:파라미터이름` 을 받는 것을 확인할 수 있다.</li>
     *     <li>추가로 NamedParameterJdbcTemplate 은 데이터베이스가 생성해주는 키를 매우 쉽게 조회하는 기능도 제공해준다.</li>
     * </ul>
     */
    @Override
    public Item save(Item item) {
        String sql  = "insert into item(item_name, price, quantity)" +
                "values (:itemName, :price, :quantity)";

        SqlParameterSource param = new BeanPropertySqlParameterSource(item);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(sql, param, keyHolder);

        long key = keyHolder.getKey().longValue();
        item.setId(key);
        return item;
    }

    /**
     * <h2>데이터 업데이트</h2>
     * 파라미터를 전달하려면 Map 처럼 key , value 데이터 구조를 만들어서 전달해야 한다.<br>
     * 여기서 `key` 는 `:파리이터이름` 으로 지정한, 파라미터의 이름이고 , `value`는 해당 파라미터의 값이 된다.<br><p>
     *
     * <h3>이름 지정 바인딩에서 자주 사용하는 파라미터의 종류는 크게 3가지가 있다.</h3>
     * <ul>
     *     <li>Map</li>
     *     <li>SqlParameterSource
     *         <ul>
     *             <li>MapSqlParameterSource</li>
     *             <li>BeanPropertySqlParameterSource</li>
     *         </ul>
     *     </li>
     * </ul>
     */
    @Override
    public void update(Long itemId, ItemUpdateDto updateParam) {
        String sql = "update item " +
                "set item_name=:itemName, price=:price, quantity=:quantity " +
                "where id =:id";

        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("itemName", updateParam.getItemName())
                .addValue("price", updateParam.getPrice())
                .addValue("quantity", updateParam.getQuantity())
                .addValue("id", itemId); //MapSqlParameterSource : Map 과 유사한데, SQL 타입을 지정할 수 있는 등 SQL에 좀 더 특화된 기능을 제공.

        template.update(sql, param);
    }

    /**
     * <h2>데이터 조회</h2>
     */
    @Override
    public Optional<Item> findById(Long id) {
        String sql = "select id, item_name, price, quantity from item where id = :id";
        try {
            Map<String, Object> param = Map.of("id", id); //단순히 Map 을 사용하여 바인딩.
            Item item = template.queryForObject(sql, param, itemRowMapper());
            return Optional.of(item);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty(); //예외를 잡아서 Optional.empty 를 대신 반환.
        }
    }

    /**
     * <h2>데이터 리스트로 조회</h2>
     * <ul>
     *     <li></li>
     * </ul>
     */
    @Override
    public List<Item> findAll(ItemSearchCond cond) {
        String itemName = cond.getItemName();
        Integer maxPrice = cond.getMaxPrice();

        SqlParameterSource param = new BeanPropertySqlParameterSource(cond); //자바빈 프로퍼티 규약을 통해서 자동으로 파라미터 객체를 생성.

        String sql = "select id, item_name, price, quantity from item";
        //동적 쿼리
        if (StringUtils.hasText(itemName) || maxPrice != null) {
            sql += " where";
        }

        boolean andFlag = false;
        if (StringUtils.hasText(itemName)) {
            sql += " item_name like concat('%',:itemName,'%')";
            andFlag = true;
        }

        if (maxPrice != null) {
            if (andFlag) {
                sql += " and";
            }
            sql += " price <= :maxPrice";
        }

        log.info("sql={}", sql);
        return template.query(sql, param, itemRowMapper());
    }

    /**
     * <h2>DB 조회 결과를 객체로 변환</h2>
     * `BeanPropertyRowMapper`는 `ResultSet`의 결과를 받아서 자바빈 규약에 맞추어 데이터를 변환한다.<br>
     * 예를 들어서 데이터베이스에서 조회한 결과가 select id, price 라고 하면 다음과 같은 코드를 작성해준다.
     * (실제로는 리플렉션 같은 기능을 사용한다.)
     *
     * <blockquote><pre>
     * Item item = new Item();
     * item.setId(rs.getLong("id"));
     * item.setPrice(rs.getInt("price"));
     * </pre></blockquote>
     */
    private RowMapper<Item> itemRowMapper() {
        return BeanPropertyRowMapper.newInstance(Item.class); //camel 변환 지원.
    }
}

/**
 *  -별칭
 *   그런데 `select item_name` 의 경우 `setItem_name()` 이라는 메서드가 없기 때문에 골치가 아프다.
 *   이런 경우 개발자가 조회 SQL을 다음과 같이 고치면 된다. `select item_name as itemName`
 *
 *   이렇게 데이터베이스 컬럼 이름과 객체의 이름이 다를 때 별칭( as )을 사용해서 문제를 많이 해결한다.
 *   `JdbcTemplate`은 물론이고, `MyBatis`같은 기술에서도 자주 사용된다.
 *
 *
 *  -관례의 불일치
 *   자바 객체는 `카멜( camelCase ) 표기법을 사용`한다. itemName 처럼 중간에 낙타 봉이 올라와 있는 표기법이다.
 *   반면에 관계형 `데이터베이스에서는 주로 언더스코어를 사용하는 snake_case 표기법을 사용`한다.
 *   item_name 처럼 중간에 언더스코어를 사용하는 표기법이다.
 *
 *   이 부분을 `관례로 많이 사용`하다 보니 BeanPropertyRowMapper 는 언더스코어 표기법을 카멜로 자동 변환해준다.
 *   따라서 select item_name 으로 조회해도 setItemName() 에 문제 없이 값이 들어간다.
 *   정리하면 snake_case 는 자동으로 해결되니 그냥 두면 되고, 컬럼 이름과 객체 이름이 완전히 다른 경우에는 조회
 *   SQL에서 별칭을 사용하면 된다.
 */