package jpabook.jpashop.service;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.repository.BookRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

  private final OrderRepository orderRepository;
  private final MemberRepository memberRepository;
  private final BookRepository bookRepository;

  /**
   * 상품 주문
   */
  @Transactional
  public Long order(Long memberId, Long bookId, int count) {

    //엔티티 조회
    Member member = memberRepository.findOne(memberId);
    Book book = bookRepository.findOne(bookId);

    //배송정보 생성
    Delivery delivery = Delivery.createDelivery(member.getAddress(), DeliveryStatus.READY);

    //주문상품 생성
    OrderLine orderLine = OrderLine.createOrderLine(book, book.getPrice(), count);

    //주문 생성
    Order order = Order.createOrder(member, delivery, orderLine);

    //주문 저장
    orderRepository.save(order);
    return order.getId();
  }

  /**
   * 주문 취소
   */
  @Transactional
  public void cancelOrder(Long orderId) {

    //주문 엔티티 조회
    Order order = orderRepository.findOne(orderId);

    //주문 취소
    order.cancel();
  }

  /**
   * 주문 검색
   */
  public List<Order> findOrders(OrderSearch orderSearch) {
    return orderRepository.findAllByQuerydsl(orderSearch);
  }

  public Order findOne(Long orderId) {
    return orderRepository.findOne(orderId);
  }

}
