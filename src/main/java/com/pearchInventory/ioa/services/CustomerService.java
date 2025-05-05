package com.pearchInventory.ioa.services;

import com.pearchInventory.ioa.dtos.CustomerDTO;
import com.pearchInventory.ioa.exceptions.CustomerNotFoundException;
import com.pearchInventory.ioa.model.Customer;
import com.pearchInventory.ioa.repositories.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;


    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    /**
     * Creates a new customer based on the provided CustomerDTO.
     * The method is transactional to ensure atomicity in case of database operations.
     *
     * @param customerDTO Data Transfer Object containing customer details
     * @return The saved Customer entity
     */
    @Transactional
    public Customer createCustomer(CustomerDTO customerDTO) {
        Customer customer = new Customer();
        customer.setName(customerDTO.name()); // Set the customer's name from the DTO
        customer.setEmail(customerDTO.email()); // Set the customer's email from the DTO
        return customerRepository.save(customer); // Save the customer to the database
    }

    /**
     * Retrieves all customers from the database.
     *
     * @return A list of all Customer entities
     */
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll(); // Fetch all customers from the repository
    }

    /**
     * Retrieves a customer by their ID.
     * Throws a RuntimeException if the customer is not found.
     *
     * @param id The ID of the customer to retrieve
     * @return The Customer entity with the specified ID
     */
    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found")); // Handle missing customer
    }
}