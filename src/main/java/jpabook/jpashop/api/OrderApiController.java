package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderLine;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

  private final OrderRepository orderRepository;
  private final OrderQueryRepository orderQueryRepository;

  /**
   * V1. 엔티티 직접 노출
   * - 엔티티가 변하면 API 스펙이 변한다.
   * - 트랜잭션 안에서 지연 로딩 필요
   * - 양방향 연관관계 문제
   * - Hibernate5Module 모듈 등록, LAZY=null 처리
   * - 양방향 관계 문제 발생 -> @JsonIgnore
   */
  @GetMapping("/api/v1/orders")
  public List<Order> ordersV1() {

    List<Order> all = orderRepository.findAll(new OrderSearch());

    for (Order order : all) {
      order.getMember().getName(); //Lazy 강제 초기화
      order.getDelivery().getAddress(); //Lazy 강제 초기화

      List<OrderLine> orderLines = order.getOrderLines();
      orderLines.stream().forEach(o -> o.getBook().getName()); //Lazy 강제 초기화        }
    }

    return all;
  }

  /**
   * V2. 엔티티를 조회해서 DTO로 변환(fetch join 사용X)
   * - 트랜잭션 안에서 지연 로딩 필요
   */
  @GetMapping("/api/v2/orders")
  public List<OrderDto> ordersV2() {
    List<Order> orders = orderRepository.findAll(new OrderSearch());
    List<OrderDto> result = orders.stream()
        .map(o -> new OrderDto(o))
        .collect(toList());

    return result;
  }

  /**
   * V3. 엔티티를 조회해서 DTO로 변환(fetch join 사용O)
   * - 페이징 시에는 N 부분을 포기해야함
   * (대신에 batch fetch size? 옵션 주면 N -> 1 쿼리로 변경 가능)
   */
  @GetMapping("/api/v3/orders")
  public List<OrderDto> ordersV3() {
    List<Order> orders = orderRepository.findAllWithItem();
    List<OrderDto> result = orders.stream()
        .map(o -> new OrderDto(o))
        .collect(toList());

    return result;
  }

  /**
   * V3.1 엔티티를 조회해서 DTO로 변환 페이징 고려
   * - ToOne 관계만 우선 모두 페치 조인으로 최적화
   * - 컬렉션 관계는 hibernate.default_batch_fetch_size, @BatchSize로 최적화
   */
  @GetMapping("/api/v3.1/orders")
  public List<OrderDto> ordersV3_page(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                      @RequestParam(value = "limit", defaultValue = "100") int limit) {
    List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit);
    List<OrderDto> result = orders.stream()
        .map(o -> new OrderDto(o))
        .collect(toList());

    return result;
  }

  /**
   * V4. JPA에서 DTO로 바로 조회, 컬렉션 N 조회 (1 + N Query)
   * - 페이징 가능
   */
  @GetMapping("/api/v4/orders")
  public List<OrderQueryDto> ordersV4() {
    return orderQueryRepository.findOrderQueryDtos();
  }

  /**
   * V5. JPA에서 DTO로 바로 조회, 컬렉션 1 조회 최적화 버전 (1 + 1 Query)
   * - 페이징 가능
   */
  @GetMapping("/api/v5/orders")
  public List<OrderQueryDto> ordersV5() {
    return orderQueryRepository.findAllByDto_optimization();
  }

  @Data
  static class OrderDto {
    private Long orderId;
    private String name;
    private LocalDateTime orderDate; //주문시간
    private OrderStatus orderStatus;
    private Address address;
    private List<OrderItemDto> orderItems;

    public OrderDto(Order order) {
      orderId = order.getId();
      name = order.getMember().getName();
      orderDate = order.getOrderDate();
      orderStatus = order.getStatus();
      address = order.getDelivery().getAddress();
      orderItems = order.getOrderLines().stream()
          .map(orderItem -> new OrderItemDto(orderItem))
          .collect(toList());
    }
  }

  @Data
  static class OrderItemDto {
    private String itemName;//상품 명
    private int orderPrice; //주문 가격
    private int count;      //주문 수량

    public OrderItemDto(OrderLine orderLine) {
      itemName = orderLine.getBook().getName();
      orderPrice = orderLine.getOrderPrice();
      count = orderLine.getCount();
    }
  }
}
