package jpabook.jpashop.controller;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateMemberForm {
  private String name;

  private String city;
  private String street;
  private String zipcode;
}
