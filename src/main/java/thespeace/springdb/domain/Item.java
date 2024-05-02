package thespeace.springdb.domain;

import lombok.Data;

/**
 * <h2>상품 자체를 나타내는 객체</h2>
 */
@Data
public class Item {

    private Long id;

    private String itemName;
    private Integer price;
    private Integer quantity;

    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
