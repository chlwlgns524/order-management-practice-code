package com.example.kdt.customer;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Customer createCustomer(String email, String name) {
        return customerRepository.insert(new Customer(UUID.randomUUID(), name, email, LocalDateTime.now()));
    }

    @Override
    public void createCustomers(List<Customer> customers) {
        customers.forEach(customerRepository::insert);
    }

    @Override
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

}
