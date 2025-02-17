package jpabook.jpashop.service;

import jpabook.jpashop.controller.UpdateMemberForm;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

  private final MemberRepository memberRepository;

  /**
   * 회원가입
   */
  @Transactional
  public Long join(Member member) {

    validateDuplicateMember(member);
    memberRepository.save(member);
    return member.getId();
  }

  private void validateDuplicateMember(Member member) {

    List<Member> findMembers = memberRepository.findByLoginId(member.getLoginId());
    if (!findMembers.isEmpty()) {
      throw new IllegalStateException("이미 존재하는 아이디입니다.");
    }
  }

  /**
   * 전체 회원 조회
   */
  public List<Member> findMembers() {
    return memberRepository.findAll();
  }

  /**
   * 회원 단건 조회
   */
  public Member findOne(Long memberId) {
    return memberRepository.findOne(memberId);
  }

  /**
   * 회원 삭제
   */
  @Transactional
  public void delete(Member member) {
    memberRepository.delete(member);
  }

  /**
   * 회원 수정
   */
  @Transactional
  public void update(Long memberId, UpdateMemberForm form) {

    Member member = memberRepository.findOne(memberId);
    Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());

    member.setName(form.getName());
    member.setAddress(address);
  }

  /**
   * 회원 수정 - MemberApiController에서 씀. spring jpa 활용 2편 내용
   * 이름만 수정할 때 씀
   */
  @Transactional
  public void update(Long memberId, String name) {

    Member member = memberRepository.findOne(memberId);
    member.setName(name);
  }
}
