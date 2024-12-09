package jpabook.jpashop.controller;

import jakarta.validation.Valid;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {

  private final MemberService memberService;

  /**
   *회원 등록 폼
   */
  @GetMapping("/members/new")
  public String createForm(Model model) {
    model.addAttribute("memberForm", new CreateMemberForm());
    return "members/createMemberForm";
  }

  /**
   *회원 등록
   */
  @PostMapping("/members/new")
  public String create(@Valid CreateMemberForm form, BindingResult result) {

    if (result.hasErrors()) {
      return "members/createMemberForm";
    }

    Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());
    Member member = Member.createMember(form.getLoginId(), form.getName(), address);

    memberService.join(member);
    return "redirect:/";
  }

  /**
   *회원 목록
   */
  @GetMapping("/members")
  public String list(Model model) {
    List<Member> members = memberService.findMembers();
    model.addAttribute("members", members);
    return "/members/memberList";
  }

  /**
   * 상품 수정 폼
   * : 회원명은 수정할 수 없고, 그 외의 것만 바뀌도록
   */
  @GetMapping("members/{memberId}/edit")
  public String updateMemberForm(@PathVariable("memberId") Long memberId, Model model) {

    Member member = memberService.findOne(memberId);

    UpdateMemberForm form = new UpdateMemberForm();
    form.setLoginId(member.getLoginId());
    form.setCity(member.getAddress().getCity());
    form.setStreet(member.getAddress().getStreet());
    form.setZipcode(member.getAddress().getZipcode());

    model.addAttribute("form", form);
    return "members/updateMemberForm";
  }

  /**
   * 회원 수정
   */
  @PostMapping("members/{memberId}/edit")
  public String updateMember(@PathVariable Long memberId, @ModelAttribute("form") UpdateMemberForm form) {

    memberService.updateMember(memberId, form);
    return "redirect:/members";
  }

  /**
   * 회원 삭제
   */
  @PostMapping("/members/{memberId}/delete")
  public String deleteMember(@PathVariable Long memberId){

    Member member = memberService.findOne(memberId);
    memberService.delete(member);

    return "redirect:/members";
  }

}
