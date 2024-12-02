package jpabook.jpashop.repository;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.Book;
import jpabook.jpashop.domain.BookLine;
import jpabook.jpashop.domain.OrderLine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class BookRepository {

  private final EntityManager em;

  public void save(Book book) {
      em.persist(book);
  }

  public Book findOne(Long id) {
    return em.find(Book.class, id);
  }

  public List<Book> findAll() {
    return em.createQuery("select b from Book b", Book.class)
        .getResultList();
  }

  /**
   * 상품 삭제
   * 기능
   * 1. 해당 책과 관련된 주문 내역(OrderLine)이 있는지 먼저 확인합니다.
   * 2. 주문 내역이 있다면 삭제를 허용하지 않고 예외를 발생시킵니다.
   * 3. 책과 카테고리를 연결하는 BookLine 엔티티들을 먼저 삭제합니다.
   * 4. 마지막으로 Book 엔티티를 삭제합니다.
   * 
   * 이렇게 구현하면:
   * - 이미 주문된 상품은 실수로 삭제되는 것을 방지할 수 있습니다.
   * - 연관된 BookLine 데이터도 깔끔하게 정리됩니다.
   * - 데이터 정합성이 보장됩니다.
   */
  public void delete(Book book) {
    // 먼저 해당 책과 관련된 모든 주문 내역을 조회
    List<OrderLine> orderLines = em.createQuery("select ol from OrderLine ol where ol.book = :book", OrderLine.class)
        .setParameter("book", book)
        .getResultList();

    //주문 내역이 있다면 삭제 불가
    if (!orderLines.isEmpty()) {
      throw new IllegalStateException("이미 주문된 상품은 삭제할 수 없습니다.");
    }

    // BookLine(카테고리 연관) 삭제
    List<BookLine> bookLines = em.createQuery("select bl from BookLine bl where bl.book = :book", BookLine.class)
        .setParameter("book", book)
        .getResultList();

    for (BookLine bookLine : bookLines) {
      em.remove(bookLine);
    }

    em.remove(book);
  }
}
