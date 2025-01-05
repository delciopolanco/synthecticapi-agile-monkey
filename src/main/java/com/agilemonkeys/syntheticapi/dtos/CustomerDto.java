package com.agilemonkeys.syntheticapi.dtos;

import org.hibernate.validator.constraints.UniqueElements;

import jakarta.validation.Valid;
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
  @Valid

  private Long id;

  @NotEmpty(message = "Name is required")
  private String name;

  @NotEmpty(message = "Surname is required")
  private String surname;

  @NotEmpty(message = "Customer ID is required")
  @UniqueElements(message = "Customer ID must be unique")
  private String customerId;

  private String photo;

  public CustomerDto(String name, String surname, String customerId, String photo) {
    this.name = name;
    this.surname = surname;
    this.customerId = customerId;
    this.photo = photo;
  }
}
