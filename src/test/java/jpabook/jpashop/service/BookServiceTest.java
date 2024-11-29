package jpabook.jpashop.service;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.*;
import jpabook.jpashop.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class BookServiceTest {

  @Autowired
  EntityManager em;
  @Autowired
  BookRepository bookRepository;
  @Autowired
  BookService bookService;

  @Test
  void saveBook_persist() {

    //given
    Book book = new Book();

    //when
    bookService.saveBook(book);

    //then
    assertThat(book).isEqualTo(bookRepository.findOne(book.getId()));
  }

  @Test
  void saveBook_merge() {

    //given
    Book book = new Book();
    book.setName("test");
    bookService.saveBook(book);

    //when
    book.setName("changed");
    bookService.saveBook(book);

    //then
    String findName = bookRepository.findOne(book.getId()).getName();
    assertThat(findName).isEqualTo("changed");
  }

  @Test
  void findBooks() {

    //given
    Book book1 = new Book();
    Book book2 = new Book();
    bookService.saveBook(book1);
    bookService.saveBook(book2);

    //when
    List<Book> findBooks = bookService.findBooks();

    //then
    assertThat(findBooks.size()).isEqualTo(2);
  }

  @Test
  void findOne() {

    //given
    Book book = new Book();
    bookService.saveBook(book);

    //when
    Book findBook = bookService.findOne(book.getId());

    //then
    assertThat(findBook).isEqualTo(bookRepository.findOne(book.getId()));
  }

  /**
   * 테스트 요점
   * : 파라미터로 넘어온 book을 삭제해서 repository에서도 제거되었는가
   * : BookLine(카테고리 연관) 삭제되었는가
   *
   * deleteBook_BookLine()
   * : Book과 Category를 연결하는 BookLine을 생성하고 저장
   * : Book을 삭제했을 때 연관된 BookLine도 함께 삭제되는지 확인
   */
  @Test
  void deleteBook_BookLine() {

    //given
    Book book = new Book();
    Category category = new Category();
    BookLine bookLine = new BookLine();

    bookLine.setBook(book);
    bookLine.setCategory(category);
    book.getBookLines().add(bookLine);

    em.persist(category);
    em.persist(bookLine);
    bookService.saveBook(book);

    //when
    bookService.deleteBook(book);

    //then
    assertThat(bookRepository.findOne(book.getId())).isNull();
    assertThat(em.find(BookLine.class, bookLine.getId())).isNull();
  }

  /**
   * 테스트 요점
   * : 해당 책과 관련된 주문 내역이 있을 시 => 주문된 상품은 삭제할 수 없다는 예외를 터뜨리는지
   *
   * deleteBook_exception()
   * : Book과 연관된 주문(Order, OrderLine)을 생성하고 저장
   * : Book 삭제 시도 시 IllegalStateException이 발생하는지 확인
   * : 예외 메시지가 정확한지 확인
   */
  @Test
  void deleteBook_exception() {

    //given
    Book book = new Book();
    book.setStockQuantity(10);
    bookService.saveBook(book);

    Member member = new Member();
    em.persist(member);

    Delivery delivery = new Delivery();
    OrderLine orderLine = OrderLine.createOrderLine(book, book.getPrice(), 1);
    Order order = Order.createOrder(member, delivery, orderLine);

    em.persist(order);

    //when&then
    assertThatThrownBy(() -> bookService.deleteBook(book))
        .isInstanceOfAny(IllegalStateException.class, 
                        InvalidDataAccessApiUsageException.class)
        .hasMessage("이미 주문된 상품은 삭제할 수 없습니다.");

  }
}