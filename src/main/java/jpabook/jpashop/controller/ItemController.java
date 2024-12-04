package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Book;
import jpabook.jpashop.service.BookService;
import jpabook.jpashop.service.UpdateBookDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {

  private final BookService bookService;

  /**
   * 상품 등록 폼
   */
  @GetMapping("/items/new")
  public String createForm(Model model) {

    model.addAttribute("form", new BookForm());
    return "items/createItemForm";
  }

  /**
   * 상품 등록
   */
  @PostMapping("/items/new")
  public String create(BookForm form) {

    Book book = Book.createBook(form.getName(), form.getPrice(), form.getStockQuantity(), form.getAuthor(), form.getIsbn());
    bookService.saveBook(book);

    return "redirect:/items";
  }

  /**
   * 상품 목록
   */
  @GetMapping("/items")
  public String list(Model model) {

    List<Book> books = bookService.findBooks();
    model.addAttribute("books", books);
    return "items/itemList";
  }

  /**
   * 상품 수정 폼
   */
  @GetMapping("/items/{bookId}/edit")
  public String updateItemForm(@PathVariable("bookId") Long bookId, Model model) {
    Book book = bookService.findOne(bookId);

    BookForm form = new BookForm();
    form.setId(book.getId());
    form.setName(book.getName());
    form.setPrice(book.getPrice());
    form.setStockQuantity(book.getStockQuantity());

    model.addAttribute("form", form);
    return "items/updateItemForm";
  }

  /**
   * 상품 수정
   */
  @PostMapping("/items/{bookId}/edit")
  public String updateItem(@PathVariable Long bookId, @ModelAttribute("form") BookForm form) {

    UpdateBookDto bookDto = new UpdateBookDto();
    bookDto.setName(form.getName());
    bookDto.setPrice(form.getPrice());
    bookDto.setStockQuantity(form.getStockQuantity());

    bookService.updateBook(bookId, bookDto);
    return "redirect:/items";
  }
}
