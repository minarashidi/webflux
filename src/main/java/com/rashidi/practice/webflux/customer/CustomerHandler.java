package com.rashidi.practice.webflux.customer;

import org.springframework.web.reactive.function.server.RenderingResponse;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.BodyInserters.fromObject;
import static org.springframework.web.reactive.function.server.ServerResponse.created;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

public class CustomerHandler {

  private final CustomerRepository customerRepository;

  public CustomerHandler(CustomerRepository customerRepository) {
    this.customerRepository = customerRepository;
  }

  public Mono<ServerResponse> findById(ServerRequest request) {

    String id = request.pathVariable("id");
    return customerRepository.findById(id)
      .flatMap(customer -> ok().contentType(APPLICATION_JSON).body(fromObject(customer)))
      .switchIfEmpty(Mono.defer(() -> ServerResponse.notFound().build()));
//      I am not building this object until it's needed
  }

  public Mono<ServerResponse> findAll(ServerRequest request) {
    Flux<Customer> customers = customerRepository.findAll();
    return ok().contentType(APPLICATION_JSON).body(customers, Customer.class);
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

  public Mono<ServerResponse> save(ServerRequest request) {
    String id = UUID.randomUUID().toString();

    Mono<Customer> customerMono = request.bodyToMono(Customer.class).map(c -> new Customer(id, c.getName(), c.getCity()))
      .flatMap(customerRepository::save);

    return created(UriComponentsBuilder.fromPath("customer/" + id).build().toUri())
      .contentType(APPLICATION_JSON)
      .body(customerMono, Customer.class);
  }

  public Mono<ServerResponse> update(ServerRequest request) {
    String id = request.pathVariable("id");

    Mono<Customer> customerMono = request.bodyToMono(Customer.class).map(c -> new Customer(id, c.getName(), c.getCity()))
      .flatMap(customerRepository::save);

    return customerRepository
      .findById(id)
      .flatMap(existingCustomer ->
        ok().contentType(APPLICATION_JSON).body(customerMono, Customer.class))
      .switchIfEmpty(Mono.defer(() -> ServerResponse.notFound().build()));
  }

}
