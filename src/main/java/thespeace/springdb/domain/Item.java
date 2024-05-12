package thespeace.springdb.domain;

import jakarta.persistence.*;
import lombok.Data;

/**
 * <h2>상품 자체를 나타내는 객체</h2>
 *
 * <h2>ORM 매핑</h2>
 * <ul>
 *     <li>@Entity : JPA가 사용하는 객체라는 뜻이다. 이 에노테이션이 있어야 JPA가 인식할 수 있다. 이렇게
 *                   {@code @Entity}가 붙은 객체를 JPA에서는 엔티티라 한다</li>
 *     <li>@Id : 테이블의 PK와 해당 필드를 매핑한다.</li>
 *     <li>@GeneratedValue(strategy = GenerationType.IDENTITY) : PK 생성 값을 데이터베이스에서 생성하는
 *                                                               IDENTITY 방식을 사용한다. 예) MySQL auto increment</li>
 *     <li>@Column : 객체의 필드를 테이블의 컬럼과 매핑한다.
 *         <ul>
 *             <li>name = "item_name" : 객체는 itemName 이지만 테이블의 컬럼은 item_name 이므로 이렇게 매핑.</li>
 *             <li>length = 10 : JPA의 매핑 정보로 DDL( create table )도 생성할 수 있는데, 그때 컬럼의 길이 값으로
 *                               활용된다. ( varchar 10 ) </li>
 *             <li>@Column 을 생략할 경우 필드의 이름을 테이블 컬럼 이름으로 사용한다. 참고로 지금처럼 스프링 부트와
 *                 통합해서 사용하면 필드 이름을 테이블 컬럼 명으로 변경할 때 객체 필드의 카멜 케이스를 테이블 컬럼의
 *                 언더스코어로 자동으로 변환해준다.
 *                 <ul>
 *                     <li>itemName -> item_name , 따라서 예제의 @Column(name = "item_name") 를 생략해도 된다.</li>
 *                 </ul>
 *             </li>
 *         </ul>
 *     </li>
 *     <li>JPA는 public 또는 protected 의 기본 생성자가 필수이다.(+프록시 기술 사용) {@code public Item() {} }</li>
 * </ul>
 */
@Data
@Entity
//@Table(name = "Item") //객체명이랑 같으면 생략 가능.
public class Item {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "item_name", length = 10)
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
