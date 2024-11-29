package jpabook.jpashop.controller;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberForm {

  @NotEmpty(message = "회원 이름은 필수입니다")
  private String name;

  @NotEmpty(message = "회원 아이디는 필수입니다")
  private String loginId;

  private String city;
  private String street;
  private String zipcode;

}
