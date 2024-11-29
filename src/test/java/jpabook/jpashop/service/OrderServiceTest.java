package jpabook.jpashop.service;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.*;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class OrderServiceTest {

  @Autowired
  EntityManager em;
  @Autowired
  OrderService orderService;
  @Autowired
  OrderRepository orderRepository;

  /**
   * 내가 테스트하고 싶은 메서드: OrderService.order()
   */
  @Test
  void 상품주문() {

    //given
    Member member = createMember();
    Book book = createBook("책1", 10000, 10);

    //when
    int orderCount = 2;
    Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

    //then
    Order getOrder = orderRepository.findOne(orderId);

    assertThat(getOrder.getStatus()).isEqualTo(OrderStatus.ORDER);
    assertThat(getOrder.getOrderLines().size()).isEqualTo(1); //주문한 상품 종류의 수
    assertThat(getOrder.getTotalPrice()).isEqualTo(book.getPrice() * orderCount); //주문 가격은 가격 * 수량이니까
    assertThat(book.getStockQuantity()).isEqualTo(8); //주문 수량만큼 재고가 줄어야 한다
  }

  @Test
  void 상품주문_재고수량초과() {

    //given
    Member member = createMember();
    Book book = createBook("책1", 10000, 10);

    //when
    int orderCount = 11;

    //then
    assertThatThrownBy(() -> orderService.order(member.getId(), book.getId(), orderCount))
        .isInstanceOf(NotEnoughStockException.class)
        .hasMessage("need more stock");
  }

  @Test
  void 주문취소() {

    //given
    Member member = createMember();
    Book book = createBook("책1", 10000, 10);
    int orderCount = 2;

    Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

    //when
    orderService.cancelOrder(orderId);

    //then
    Order order = orderRepository.findOne(orderId);

    assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCEL);
    assertThat(book.getStockQuantity()).isEqualTo(10);

  }

  private Book createBook(String name, int price, int stockQuantity) {
    Book book = new Book();
    book.setName(name);
    book.setPrice(price);
    book.setStockQuantity(stockQuantity);
    em.persist(book);
    return book;
  }

  private Member createMember() {
    Member member = new Member();
    member.setName("회원1");
    member.setAddress(new Address("서울", "강가", "123-123"));
    member.setLoginId("1234");
    em.persist(member);
    return member;
  }

}