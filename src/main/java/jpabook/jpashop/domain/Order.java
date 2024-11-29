package jpabook.jpashop.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) //createOrder로 통일하려고 protected로 접근 제한함
public class Order {

  @Id @GeneratedValue
  @Column(name = "order_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private Member member;

  @OneToMany(mappedBy = "order",cascade = CascadeType.ALL)
  private List<OrderLine> orderLines = new ArrayList<>();

  @OneToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
  @JoinColumn(name = "delivery_id")
  private Delivery delivery; //배송정보

  private LocalDateTime orderDate; //주문시간

  @Enumerated(EnumType.STRING)
  private OrderStatus status; //주문상태 [ORDER, CANCEL]

  /**
   * 연관관계 메서드
   */
  public void addOrderLine(OrderLine orderLine) {
    orderLines.add(orderLine);
    orderLine.setOrder(this);
  }

  /**
   * 생성 메서드
   */
  public static Order createOrder(Member member, Delivery delivery, OrderLine... orderLines) {
    Order order = new Order();
    order.setMember(member);
    order.setDelivery(delivery);
    for (OrderLine orderLine : orderLines) {
      order.addOrderLine(orderLine);
    }

    order.setStatus(OrderStatus.ORDER);
    order.setOrderDate(LocalDateTime.now());
    return order;
  }

  /**
   * 주문 취소
   */
  public void cancel() {
    if (delivery.getStatus() == DeliveryStatus.COMP) {
      throw new IllegalStateException("이미 배송완료된 상품은 취소가 불가능합니다");
    }

    this.setStatus(OrderStatus.CANCEL);
    for (OrderLine orderLine : orderLines) {
      orderLine.cancel();
    }
  }

  /**
   * 조회 로직
   * @return 전체 주문 가격
   */
  public int getTotalPrice() {
    int totalPrice = orderLines.stream().mapToInt(OrderLine::getTotalPrice).sum();
    return totalPrice;
  }

}
