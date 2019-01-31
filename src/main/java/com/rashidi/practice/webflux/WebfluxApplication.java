package com.rashidi.practice.webflux;

import com.rashidi.practice.webflux.customer.Customer;
import com.rashidi.practice.webflux.customer.CustomerConfiguration;
import com.rashidi.practice.webflux.customer.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

import static java.util.Arrays.asList;

@SpringBootApplication
@Import(CustomerConfiguration.class)
public class WebfluxApplication {

  public static void main(String[] args) {
    SpringApplication.run(WebfluxApplication.class, args);
  }

  @Component
  private static class DataInserter implements CommandLineRunner {


    private final CustomerRepository customerRepository;

    public DataInserter(CustomerRepository customerRepository) {
      this.customerRepository = customerRepository;
    }

    @Override
    public void run(String... args) throws Exception {
      Customer first = new Customer("1", "nice customer", "Stockholm");
      Customer second = new Customer("2", "cool customer", "Paris");
      this.customerRepository.insert(asList(first, second)).subscribe();
    }
  }

}

