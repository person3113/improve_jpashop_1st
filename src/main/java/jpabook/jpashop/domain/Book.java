package jpabook.jpashop.domain;

import jakarta.persistence.*;
import jpabook.jpashop.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Book {

  @Id @GeneratedValue
  @Column(name = "book_id")
  private Long id;

  private String name;
  private int price;
  private int stockQuantity;

  private String author;
  private String isbn;

  @OneToMany(mappedBy = "book")
  private List<BookLine> bookLines = new ArrayList<>();

  /**
   * 연관관계 메서드
   */
  public void addBookLine(BookLine bookLine) {
    bookLines.add(bookLine);
    bookLine.setBook(this);
  }

  /**
   * 생성 메서드
   */
  public static Book createBook(String name, int price, int stockQuantity, String author, String isbn,BookLine... bookLines) {

    Book book = new Book();

    book.setName(name);
    book.setPrice(price);
    book.setStockQuantity(stockQuantity);
    book.setAuthor(author);
    book.setIsbn(isbn);

    for (BookLine bookLine : bookLines) {
      book.addBookLine(bookLine);
    }

    return book;
  }

  /**
   * 재고 증가
   */
  public void addStock(int quantity){
    this.stockQuantity += quantity;
  }

  /**
   * 재고 감소
   */
  public void removeStock(int quantity){
    int restStock = this.stockQuantity - quantity;
    if (restStock < 0) {
      throw new NotEnoughStockException("need more stock");
    }
    this.stockQuantity = restStock;
  }
}
