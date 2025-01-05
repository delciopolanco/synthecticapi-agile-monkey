package com.agilemonkeys.syntheticapi.converters;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.agilemonkeys.syntheticapi.dtos.CustomerDto;
import com.agilemonkeys.syntheticapi.entities.Customer;

@Component
public class CustomerDtoConverter {

  @Autowired
  private ModelMapper modelMapper;

  public CustomerDto convertToDto(Customer customer) {
    return modelMapper.map(customer, CustomerDto.class);
  }

  public Customer convertToEntity(CustomerDto customerDto) {
    return modelMapper.map(customerDto, Customer.class);

  }
}
