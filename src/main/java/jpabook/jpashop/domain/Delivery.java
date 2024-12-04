package jpabook.jpashop.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Delivery {
  @Id @GeneratedValue
  @Column(name = "delivery_id")
  private Long id;

  @Embedded
  private Address address;

  @Enumerated(EnumType.STRING)
  private DeliveryStatus status; //ENUM [READY(준비), COMP(배송)]

  /**
   * 생성 메서드
   */
  public static Delivery createDelivery(Address address, DeliveryStatus status) {
    Delivery delivery = new Delivery();

    delivery.setAddress(address);
    delivery.setStatus(status);

    return delivery;
  }
}
