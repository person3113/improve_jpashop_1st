package jpabook.jpashop;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class InitDb {

  private final InitService initService;

  @PostConstruct
  public void init() {
    initService.dbInit1();
    initService.dbInit2();
  }

  @Component
  @Transactional
  @RequiredArgsConstructor
  static class InitService {

    private final EntityManager em;

    public void dbInit1() {
      Member member = createMember("userA", "seoul", "1", "1111");
      em.persist(member);

      Book book1 = createBook("jpa1", 10000, 100);
      em.persist(book1);

      Book book2 = createBook("jpa2", 20000, 100);
      em.persist(book2);

      OrderLine orderLine1 = OrderLine.createOrderLine(book1, 10000, 1);
      OrderLine orderLine2 = OrderLine.createOrderLine(book2, 20000, 2);

      Delivery delivery = createDelivery(member);
      Order order = Order.createOrder(member, delivery, orderLine1, orderLine2);
      em.persist(order);
    }

    public void dbInit2() {
      Member member = createMember("userB", "busan", "2", "2222");
      em.persist(member);

      Book book1 = createBook("spring1", 20000, 200);
      em.persist(book1);

      Book book2 = createBook("spring2", 40000, 300);
      em.persist(book2);

      OrderLine orderLine1 = OrderLine.createOrderLine(book1, 20000, 3);
      OrderLine orderLine2 = OrderLine.createOrderLine(book2, 40000, 4);

      Delivery delivery = createDelivery(member);
      Order order = Order.createOrder(member, delivery, orderLine1, orderLine2);
      em.persist(order);
    }

    private Member createMember(String name, String city, String street, String zipcode) {
      Member member = new Member();
      member.setName(name);
      member.setAddress(new Address(city, street, zipcode));
      return member;
    }

    private Book createBook(String name, int price, int stockQuantity) {
      Book book1 = new Book();
      book1.setName(name);
      book1.setPrice(price);
      book1.setStockQuantity(stockQuantity);
      return book1;
    }

    private Delivery createDelivery(Member member) {
      Delivery delivery = new Delivery();
      delivery.setAddress(member.getAddress());
      return delivery;
    }
  }
}
