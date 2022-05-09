package com.example.kdt.customer;

import java.util.List;

public interface CustomerService {

    Customer createCustomer(String email, String name);

    void createCustomers(List<Customer> customers);

    List<Customer> getAllCustomers();



}
