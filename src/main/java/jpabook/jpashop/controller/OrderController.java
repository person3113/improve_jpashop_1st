package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Book;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.service.BookService;
import jpabook.jpashop.service.MemberService;
import jpabook.jpashop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController {

  private final OrderService orderService;
  private final MemberService memberService;
  private final BookService bookService;

  @GetMapping("/order")
  public String createForm(Model model) {

    List<Member> members = memberService.findMembers();
    List<Book> books = bookService.findBooks();

    model.addAttribute("members", members);
    model.addAttribute("books", books);

    return "order/orderForm";
  }

  @PostMapping("/order")
  public String order(@RequestParam("memberId") Long memberId,
                      @RequestParam("bookId") Long bookId,
                      @RequestParam("count") int count) {
    orderService.order(memberId, bookId, count);

    return "redirect:/orders";
  }

  @GetMapping("/orders")
  public String orderList(@ModelAttribute("orderSearch") OrderSearch orderSearch, Model model) {

    List<Order> orders = orderService.findOrders(orderSearch);
    model.addAttribute("orders", orders);

    return "order/orderList";
  }

  @PostMapping("/orders/{orderId}/cancel")
  public String cancelOrder(@PathVariable("orderId") Long orderId) {

    orderService.cancelOrder(orderId);
    return "redirect:/order/{orderId}/specific";
  }

  @GetMapping("/order/{orderId}/specific")
  public String order_specific(@PathVariable("orderId") Long orderId, Model model) {
    Order order = orderService.findOne(orderId);
    model.addAttribute("order", order);

    return "order/orderView";
  }
}
