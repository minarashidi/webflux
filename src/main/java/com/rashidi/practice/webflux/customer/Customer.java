package com.rashidi.practice.webflux.customer;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class Customer {

  @Id
  private final String id;
  private final String name;
  private final String city;

  public Customer(String id, String name, String city) {
    this.id = id;
    this.name = name;
    this.city = city;
  }
}
