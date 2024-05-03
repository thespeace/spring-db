package thespeace.springdb.repository.jdbctemplate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
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
import java.util.Optional;

/**
 * <h1>JdbcTemplate</h1>
 */
@Slf4j
public class JdbcTemplateItemRepositoryV1 implements ItemRepository {

    //dataSource 를 의존 관계 주입 받고 생성자 내부에서 JdbcTemplate 을 생성(관례상 이 방법을 많이 사용, 물론 스프링 빈으로 등록하고 주입받아도 된다.)
    private final JdbcTemplate template;

    public JdbcTemplateItemRepositoryV1(DataSource dataSource) {
        this.template = new JdbcTemplate(dataSource);
    }

    /**
     * <h2>데이터 저장</h2>
     * <ul>
     *     <li>{@code KeyHolder}와 {@code connection.prepareStatement(sql, new String[]{"id"})}를 사용해서 id 를
     *         지정해주면 INSERT 쿼리 실행 이후에 데이터베이스에서 생성된 ID 값을 조회할 수 있다.</li>
     * </ul>
     */
    @Override
    public Item save(Item item) {
        String sql  = "insert into item(item_name, price, quantity) values (?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(connection -> {
            //자동 증가 키
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, item.getItemName());
            ps.setInt(2, item.getPrice());
            ps.setInt(3, item.getQuantity());
            return ps;
        }, keyHolder);

        long key = keyHolder.getKey().longValue();
        item.setId(key);
        return item;
    }

    /**
     * <h2>데이터 업데이트</h2>
     */
    @Override
    public void update(Long itemId, ItemUpdateDto updateParam) {
        String sql = "update item set item_name=?, price=?, quantity=?, where id =?";
        template.update(sql,
                updateParam.getItemName(),
                updateParam.getPrice(),
                updateParam.getQuantity(),
                itemId);
    }

    /**
     * <h2>데이터 조회</h2>
     * <ul>
     *     <li>{@code template.queryForObject()}
     *         <ul>
     *             <li>결과 로우가 하나일 때 사용한다.</li>
     *             <li>RowMapper 는 데이터베이스의 반환 결과인 ResultSet 을 객체로 변환한다.</li>
     *             <li>결과가 없으면 EmptyResultDataAccessException 예외가 발생한다.</li>
     *             <li>결과가 둘 이상이면 IncorrectResultSizeDataAccessException 예외가 발생한다.</li>
     *             <li>queryForObject() 인터페이스 정의 : {@code <T> T queryForObject(String sql,
     *                 RowMapper<T> rowMapper, Object... args) throws DataAccessException;}</li>
     *         </ul>
     *     </li>
     * </ul>
     */
    @Override
    public Optional<Item> findById(Long id) {
        String sql = "select id, item_name, price, quantity from item where id = ?";
        try {
            Item item = template.queryForObject(sql, itemRowMapper(), id);
            return Optional.of(item);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty(); //예외를 잡아서 Optional.empty 를 대신 반환.
        }
    }

    /**
     * <h2>데이터 리스트로 조회</h2>
     * <ul>
     *     <li>template.query()
     *         <ul>
     *             <li>결과가 하나 이상일 때 사용한다.</li>
     *             <li>RowMapper 는 데이터베이스의 반환 결과인 ResultSet 을 객체로 변환한다.</li>
     *             <li>결과가 없으면 빈 컬렉션을 반환한다.</li>
     *             <li>동적 쿼리에 대한 부분은 바로 다음에 다룬다.</li>
     *             <li>query() 인터페이스 정의 : {@code <T> List<T> query(String sql, RowMapper<T> rowMapper,
     *                 Object... args) throws DataAccessException;}</li>
     *         </ul>
     *     </li>
     * </ul>
     */
    @Override
    public List<Item> findAll(ItemSearchCond cond) {
        String itemName = cond.getItemName();
        Integer maxPrice = cond.getMaxPrice();

        String sql = "select id, item_name, price, quantity from item";

        //동적 쿼리
        if (StringUtils.hasText(itemName) || maxPrice != null) {
            sql += " where";
        }

        boolean andFlag = false;
        List<Object> param = new ArrayList<>();
        if (StringUtils.hasText(itemName)) {
            sql += " item_name like concat('%',?,'%')";
            param.add(itemName);
            andFlag = true;
        }

        if (maxPrice != null) {
            if (andFlag) {
                sql += " and";
            }
            sql += " price <= ?";
            param.add(maxPrice);
        }

        log.info("sql={}", sql);
        return template.query(sql, itemRowMapper());
    }

    /**
     * <h2>DB 조회 결과를 객체로 변환</h2>
     * 데이터베이스의 조회 결과를 객체로 변환할 때 사용한다.<br>
     * JDBC를 직접 사용할 때 ResultSet 를 사용했던 부분을 떠올리면 된다.<br>
     * 차이가 있다면 다음과 같이 JdbcTemplate이 다음과 같은 루프를 돌려주고, 개발자는 RowMapper 를 구현해서 그 내
     * 부 코드만 채운다고 이해하면 된다.<br>
     * <blockquote><pre>
     * while(resultSet 이 끝날 때 까지) {
     *     rowMapper(rs, rowNum)
     * }
     * </pre></blockquote>
     */
    private RowMapper<Item> itemRowMapper() {
        return (rs, rowNum) -> {
            Item item = new Item();
            item.setId(rs.getLong("id"));
            item.setItemName(rs.getString("item_name"));
            item.setPrice(rs.getInt("price"));
            item.setQuantity(rs.getInt("quantity"));
            return item;
        };
    }
}
