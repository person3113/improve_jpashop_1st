package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberServiceTest {

  @Autowired
  MemberService memberService;
  @Autowired
  MemberRepository memberRepository;

  @Test
  void 회원가입(){

    //given
    Member member = new Member();
    member.setName("test");

    //when
    Long saveId = memberService.join(member);

    //then
    assertThat(member).isEqualTo(memberRepository.findOne(saveId));

  }

  @Test
  void 중복_회원_예외(){

    //given
    Member member1 = new Member();
    member1.setLoginId("1234");

    Member member2 = new Member();
    member2.setLoginId("1234");

    //when
    memberService.join(member1);

    //then
    Assertions.assertThrows(
        IllegalStateException.class,
        () -> memberService.join(member2));
  }

  /**
   * findMembers(), findOne() 검증
   */
  @Test
  void 회원조회(){

    //given
    Member member1 = new Member();
    member1.setLoginId("1234");

    Member member2 = new Member();
    member2.setLoginId("2345");

    //when
    memberService.join(member1);
    memberService.join(member2);

    //then
    assertThat(memberService.findMembers().size()).isEqualTo(2);
    assertThat((memberService.findOne(member2.getId()))).isEqualTo(member2);
  }

  @Test
  void 회원삭제(){

    //given
    Member member = new Member();
    member.setLoginId("1234");

    memberService.join(member);

    //when
    memberService.delete(member);

    //then
    assertThat(memberService.findOne(member.getId())).isEqualTo(null);
  }
}