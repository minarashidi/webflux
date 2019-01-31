package com.rashidi.practice.webflux.customer;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RenderingResponse;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;

public class CustomerHandler {

  private final CustomerRepository customerRepository;

  public CustomerHandler(CustomerRepository customerRepository) {
    this.customerRepository = customerRepository;
  }

  public Mono<ServerResponse> findById(ServerRequest request) {

    String id = request.pathVariable("id");
    return customerRepository.findById(id)
      .flatMap(customer -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(fromObject(customer)))
      .switchIfEmpty(Mono.defer(() -> ServerResponse.notFound().build()));
//      I am not building this object until it's needed
  }

  public Mono<ServerResponse> findAll(ServerRequest request) {
    Flux<Customer> customers = customerRepository.findAll();
    return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(customers, Customer.class);
  }

  public Mono<ServerResponse> renderCustomer(ServerRequest request) {
    String id = request.pathVariable("id");
    return customerRepository.findById(id).flatMap(
      customer -> RenderingResponse.create("customer").modelAttribute("customer", customer).build());
  }

  public Mono<ServerResponse> renderCustomers(ServerRequest request) {
    Flux<Customer> customers = customerRepository.findAll();
    return RenderingResponse.create("customers").modelAttribute("customers", customers).build().map(r -> r);
  }

}
