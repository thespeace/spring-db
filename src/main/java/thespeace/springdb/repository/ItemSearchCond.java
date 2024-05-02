package thespeace.springdb.repository;

import lombok.Data;

/**
 * <h2>검색 조건</h2>
 * <ul>
 *     <li>검색 조건으로 사용. 상품명, 최대 가격이 있고 상품명의 일부만 포함되어도 검색이 가능해야 한다.(like 검색)</li>
 *     <li>cond condition 을 줄여서 사용했다. 검색 조건은 뒤에 Cond 를 붙이도록 규칙을 정함.</li>
 * </ul>
 */
@Data
public class ItemSearchCond {

    private String itemName;
    private Integer maxPrice;

    public ItemSearchCond() {
    }

    public ItemSearchCond(String itemName, Integer maxPrice) {
        this.itemName = itemName;
        this.maxPrice = maxPrice;
    }
}
