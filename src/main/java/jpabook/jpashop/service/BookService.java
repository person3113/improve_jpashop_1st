package jpabook.jpashop.service;

import jpabook.jpashop.domain.Book;
import jpabook.jpashop.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookService {

  private final BookRepository bookRepository;

  @Transactional
  public void saveBook(Book book) {
    bookRepository.save(book);
  }

  /**
   * 책(아이템) 수정
   * merge가 아닌 변경 감지 활용
   */
  @Transactional
  public void updateBook(Long bookId, UpdateBookDto bookDto) {
    Book findBook = bookRepository.findOne(bookId);

    findBook.setName(bookDto.getName());
    findBook.setPrice(bookDto.getPrice());
    findBook.setStockQuantity(bookDto.getStockQuantity());
  }

  public List<Book> findBooks() {
    return bookRepository.findAll();
  }

  public Book findOne(Long bookId) {
    return bookRepository.findOne(bookId);
  }

  @Transactional
  public void deleteBook(Book book) {
    bookRepository.delete(book);
  }
}
