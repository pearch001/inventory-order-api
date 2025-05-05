package com.pearchInventory.ioa.controllers;


import com.pearchInventory.ioa.dtos.CustomerDTO;
import com.pearchInventory.ioa.enums.ResponseCode;
import com.pearchInventory.ioa.model.Customer;
import com.pearchInventory.ioa.services.CustomerService;
import com.pearchInventory.ioa.utils.GenericData;
import com.pearchInventory.ioa.utils.Response;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<Response> createCustomer(@Valid @RequestBody CustomerDTO customerDTO) {
        Customer customer = customerService.createCustomer(customerDTO);
        return ResponseEntity.status(201).body(new Response(ResponseCode.SUCCESS,new GenericData<>(customer)) );
    }

    @GetMapping
    public ResponseEntity<Response> getAllCustomers() {
        return ResponseEntity.ok(new Response(ResponseCode.SUCCESS,new GenericData<>(customerService.getAllCustomers())) );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getCustomerById(@PathVariable Long id) {
        return ResponseEntity.ok(new Response(ResponseCode.SUCCESS,new GenericData<>(customerService.getCustomerById(id))) );

    }
}