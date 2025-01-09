package jpabook.jpashop.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

import static jpabook.jpashop.domain.QMember.member;
import static jpabook.jpashop.domain.QOrder.order;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

  private final EntityManager em;
  private final JPAQueryFactory queryFactory;

  public void save(Order order) {
    em.persist(order);
  }

  public Order findOne(Long id) {
    return em.find(Order.class, id);
  }

  /**
   * 검색 조건에 동적으로 쿼리를 생성해서 주문 엔티티를 조회한다.
   * 주문 상태, 회원 이름을 조건으로 주문을 검색한다.
   * language=JPQL
   */
  public List<Order> findAll(OrderSearch orderSearch) {
    String jpql = "select o From Order o join o.member m";
    boolean isFirstCondition = true;

    //주문 상태 검색
    if (orderSearch.getOrderStatus() != null) {
      if (isFirstCondition) {
        jpql += " where";
        isFirstCondition = false;
      } else {
        jpql += " and";
      }
      jpql += " o.status = :status";
    }

    //회원 이름 검색
    if (StringUtils.hasText(orderSearch.getLoginId())) {
      if (isFirstCondition) {
        jpql += " where";
        isFirstCondition = false;
      } else {
        jpql += " and";
      }
      jpql += " m.loginId like :loginId";
    }

    TypedQuery<Order> query = em.createQuery(jpql, Order.class)
        .setMaxResults(1000); //최대 1000건

    if (orderSearch.getOrderStatus() != null) {
      query = query.setParameter("status", orderSearch.getOrderStatus());
    }
    if (StringUtils.hasText(orderSearch.getLoginId())) {
      query = query.setParameter("loginId", orderSearch.getLoginId());
    }
    return query.getResultList();
  }

  public List<Order> findAllByQuerydsl(OrderSearch orderSearch) {
    return queryFactory
        .selectFrom(order)
        .join(order.member, member)
        .where(
            statusEq(orderSearch.getOrderStatus()),
            loginIdEq(orderSearch.getLoginId())
        )
        .fetch();
  }

  private BooleanExpression statusEq(OrderStatus status) {
    if (status == null) {
      return null;
    }
    return order.status.eq(status);
  }

  private BooleanExpression loginIdEq(String loginId) {
    if (!StringUtils.hasText(loginId)) {
      return null;
    }
    return member.loginId.eq(loginId);
  }

  public List<Order> findAllWithMemberDelivery() {
    return em.createQuery(
            "select o from Order o" +
                " join fetch o.member m" +
                " join fetch o.delivery d", Order.class)
        .getResultList();
  }

  public List<Order> findAllWithItem() {
    return em.createQuery(
            "select distinct o from Order o" +
                " join fetch o.member m" +
                " join fetch o.delivery d" +
                " join fetch o.orderLines ol" +
                " join fetch ol.book b", Order.class)
        .getResultList();
  }

  public List<Order> findAllWithMemberDelivery(int offset, int limit) {
    return em.createQuery(
            "select o from Order o" +
                " join fetch o.member m" +
                " join fetch o.delivery d", Order.class)
        .setFirstResult(offset)
        .setMaxResults(limit)
        .getResultList();
  }
}
