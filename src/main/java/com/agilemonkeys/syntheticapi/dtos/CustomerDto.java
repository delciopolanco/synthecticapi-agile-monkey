package com.agilemonkeys.syntheticapi.dtos;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDto {

  private Long id;

  @NotEmpty
  private String name;

  @NotEmpty
  private String surname;

  @NotEmpty
  private String customerId;

  private String photo;

  public CustomerDto(String name, String surname, String customerId, String photo) {
    this.name = name;
    this.surname = surname;
    this.customerId = customerId;
    this.photo = photo;
  }
}
