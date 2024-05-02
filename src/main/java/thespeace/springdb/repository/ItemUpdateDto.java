package thespeace.springdb.repository;

import lombok.Data;

/**
 * <h2>상품 수정할 때 사용하는 객체</h2>
 * 단순히 데이터를 전달하는 용도로 사용, 때문에 클래스이름에 DTO를 붙임.
 */
@Data
public class ItemUpdateDto {
    private String itemName;
    private Integer price;
    private Integer quantity;

    public ItemUpdateDto() {
    }

    public ItemUpdateDto(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
