package com.agilemonkeys.syntheticapi.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

  @Valid

  private Long id;

  @NotEmpty(message = "Name is required")
  private String name;

  @NotEmpty(message = "Email is required")
  @Email(message = "Email should be valid")
  private String email;

}