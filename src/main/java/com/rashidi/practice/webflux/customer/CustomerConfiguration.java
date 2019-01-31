package com.rashidi.practice.webflux.customer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_HTML;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class CustomerConfiguration {

  @Bean
  CustomerHandler customerHandler(CustomerRepository repository) {
    return new CustomerHandler(repository);
  }

  @Bean
  public RouterFunction<ServerResponse> customerRouter(CustomerHandler customerHandler) {

    RouterFunction<ServerResponse> json = route()
      .nest(accept(APPLICATION_JSON), builder -> {
        builder
          .GET("/{id}", customerHandler::findById)
          .GET("", customerHandler::findAll);
      }).build();

    RouterFunction<ServerResponse> html = route()
      .nest(accept(TEXT_HTML), builder -> {
        builder
          .GET("/{id}", customerHandler::renderCustomer)
          .GET("", customerHandler::renderCustomers);
      }).build();

    return route()
      .path("/customers", () -> html.and(json))
      .build();
  }

}
