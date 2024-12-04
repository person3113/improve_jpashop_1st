package jpabook.jpashop.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
//@Table(name = "order_line")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)  //createOrderLine으로 통일하려고 protected로 접근 제한함
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

  /**
   * 생성 메서드
   */
  public static OrderLine createOrderLine(Book book, int orderPrice, int count) {
    OrderLine orderLine = new OrderLine();
    orderLine.setBook(book); //단방향이라서 OrderLine에만 여기서 연간관계 넣어주면 되구나
    orderLine.setOrderPrice(orderPrice);
    orderLine.setCount(count);

    book.removeStock(count);
    return orderLine;
  }

  /**
   * 주문 상품 취소
   * > 취소된 재고만큼 늘리는 게 목적임
   */
  public void cancel() {
    getBook().addStock(count);
  }

  /**
   * 총 가격: 주문 수량 * 가격
   * @return 주문 상품의 총 가격
   */
  public int getTotalPrice() {
    return getOrderPrice() * getCount();
  }
}
