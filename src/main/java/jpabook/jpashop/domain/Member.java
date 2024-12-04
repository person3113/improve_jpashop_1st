package jpabook.jpashop.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Member {
  @Id @GeneratedValue
  @Column(name = "member_id")
  private Long id;

  @Column(unique = true)
  private String loginId; // 로그인할 때 쓰는 아이디
  private String name;

  @Embedded
  private Address address;

  /**
   * 생성 메서드
   */
  public static Member createMember(String loginId, String name, Address address) {
    Member member = new Member();

    member.setLoginId(loginId);
    member.setName(name);
    member.setAddress(address);

    return member;
  }
}
