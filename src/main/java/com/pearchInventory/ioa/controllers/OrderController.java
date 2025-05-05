package com.pearchInventory.ioa.controllers;


import com.pearchInventory.ioa.dtos.OrderDTO;
import com.pearchInventory.ioa.enums.ResponseCode;
import com.pearchInventory.ioa.model.Order;
import com.pearchInventory.ioa.services.OrderService;
import com.pearchInventory.ioa.utils.GenericData;
import com.pearchInventory.ioa.utils.Response;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<Response> createOrder(@Valid @RequestBody OrderDTO orderDTO) {
        Order order = orderService.createOrder(orderDTO);
        return ResponseEntity.status(201).body(new Response(ResponseCode.SUCCESS,new GenericData<>(order)) );
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<Response> getOrdersByCustomerId(@PathVariable Long customerId) {
        return ResponseEntity.ok(new Response(ResponseCode.SUCCESS,new GenericData<>(orderService.getOrdersByCustomerId(customerId))) );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(new Response(ResponseCode.SUCCESS,new GenericData<>(orderService.getOrderById(id))) );

    }
}
