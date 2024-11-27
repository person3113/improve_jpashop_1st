package jpabook.jpashop.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "order_line")
@Getter @Setter
public class OrderLine {

  @Id @GeneratedValue
  @Column(name = "order_line_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "book_id")
  private Book book;      //주문 상품

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id")
  private Order order;    //주문

  private int orderPrice; //주문 가격
  private int count;      //주문 수량
}
