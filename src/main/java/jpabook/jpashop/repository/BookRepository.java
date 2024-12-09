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
   * 1. 책과 카테고리를 연결하는 BookLine 엔티티들을 먼저 삭제합니다.
   * 2. 마지막으로 Book 엔티티를 삭제합니다.
   * 
   * 이렇게 구현하면:
   * - 연관된 BookLine 데이터도 깔끔하게 정리됩니다.
   * - 데이터 정합성이 보장됩니다.
   */
  public void delete(Book book) {

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
