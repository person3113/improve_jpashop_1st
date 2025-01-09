package jpabook.jpashop;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Profile("local")
@Component
@RequiredArgsConstructor
public class InitDb {

  private final InitService initService;

//  @PostConstruct
  @EventListener(ApplicationReadyEvent.class)
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
      Member member = createMember("loginId1","userA", "seoul", "1", "1111");
      em.persist(member);

      Book book1 = createBook("jpa1", 10000, 100, "kim", "1234");
      em.persist(book1);

      Book book2 = createBook("jpa2", 20000, 100, "jeong", "5678");
      em.persist(book2);

      OrderLine orderLine1 = OrderLine.createOrderLine(book1, 10000, 1);
      OrderLine orderLine2 = OrderLine.createOrderLine(book2, 20000, 2);

      Delivery delivery = createDelivery(member);
      Order order = Order.createOrder(member, delivery, orderLine1, orderLine2);
      em.persist(order);
    }

    public void dbInit2() {
      Member member = createMember("loginId2","userB", "busan", "2", "2222");
      em.persist(member);

      Book book1 = createBook("spring1", 20000, 200, "lee", "4321");
      em.persist(book1);

      Book book2 = createBook("spring2", 40000, 300, "park", "8765");
      em.persist(book2);

      OrderLine orderLine1 = OrderLine.createOrderLine(book1, 20000, 3);
      OrderLine orderLine2 = OrderLine.createOrderLine(book2, 40000, 4);

      Delivery delivery = createDelivery(member);
      Order order = Order.createOrder(member, delivery, orderLine1, orderLine2);
      em.persist(order);
    }

    private Member createMember(String loginId, String name, String city, String street, String zipcode) {
      Member member = new Member();
      member.setLoginId(loginId);
      member.setName(name);
      member.setAddress(new Address(city, street, zipcode));
      return member;
    }

    private Book createBook(String name, int price, int stockQuantity, String author, String isbn) {
      Book book = new Book();

      book.setName(name);
      book.setPrice(price);
      book.setStockQuantity(stockQuantity);
      book.setAuthor(author);
      book.setIsbn(isbn);

      return book;
    }

    private Delivery createDelivery(Member member) {
      Delivery delivery = new Delivery();
      delivery.setAddress(member.getAddress());
      return delivery;
    }
  }
}
