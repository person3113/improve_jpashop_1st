package jpabook.jpashop.repository;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

  private final EntityManager em;

  public void save(Member member) {
    em.persist(member);
  }

  public Member findOne(Long id) {
    return em.find(Member.class, id);
  }

  public List<Member> findAll() {
    return em.createQuery("select m from Member m", Member.class)
        .getResultList();
  }

  public List<Member> findByLoginId(String loginId) {
    return em.createQuery("select m from Member m where m.loginId = :loginId", Member.class)
        .setParameter("loginId",loginId)
        .getResultList();
  }

  /**
   * 회원 수정 메서드: 웹 계층 개발 때 추가
   */

  /**
   * name: 회원 삭제
   * feture
   * 1. 먼저 삭제할 회원과 관련된 모든 주문을 조회합니다.
   * 2. 조회된 각 주문을 삭제합니다.
   *    (Order 엔티티는 OrderLine, Delivery와 cascade=ALL 관계이므로 연관된 데이터도 함께 삭제됩니다)
   * 3.마지막으로 회원을 삭제합니다.
   */
  public void delete(Member member) {
    List<Order> orders = em.createQuery("select o from Order o where o.member=:member", Order.class)
        .setParameter("member", member)
        .getResultList();

    for (Order order : orders) {
      em.remove(order);
    }

    em.remove(member);
  }

}
