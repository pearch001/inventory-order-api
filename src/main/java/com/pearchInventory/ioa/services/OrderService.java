package com.pearchInventory.ioa.services;

import com.pearchInventory.ioa.dtos.OrderDTO;
import com.pearchInventory.ioa.dtos.OrderItemDTO;
import com.pearchInventory.ioa.exceptions.CustomerNotFoundException;
import com.pearchInventory.ioa.exceptions.InsufficientStockException;
import com.pearchInventory.ioa.exceptions.OrderNotFoundException;
import com.pearchInventory.ioa.exceptions.ProductNotFoundException;
import com.pearchInventory.ioa.model.Customer;
import com.pearchInventory.ioa.model.Order;
import com.pearchInventory.ioa.model.OrderItem;
import com.pearchInventory.ioa.model.Product;
import com.pearchInventory.ioa.repositories.CustomerRepository;
import com.pearchInventory.ioa.repositories.OrderRepository;
import com.pearchInventory.ioa.repositories.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, CustomerRepository customerRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
    }

    /**
     * Creates a new order based on the provided OrderDTO.
     * The method is transactional with SERIALIZABLE isolation to ensure data consistency.
     *
     * @param orderDTO Data Transfer Object containing order details
     * @return The saved Order entity
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Order createOrder(OrderDTO orderDTO) {
        // Fetch the customer by ID, throw an exception if not found
        Customer customer = customerRepository.findById(orderDTO.customerId())
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));

        // Initialize order items list and total amount
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        // Process each item in the order
        for (OrderItemDTO itemDTO : orderDTO.items()) {
            // Fetch the product by SKU with a lock for update, throw an exception if not found
            Product product = productRepository.findBySkuForUpdate(itemDTO.productSku())
                    .orElseThrow(() -> new ProductNotFoundException("Product not found: " + itemDTO.productSku()));

            // Check if there is sufficient stock for the product
            if (product.getStock() < itemDTO.quantity()) {
                throw new InsufficientStockException("Insufficient stock for product: " + itemDTO.productSku());
            }

            // Deduct the stock and save the updated product
            product.setStock(product.getStock() - itemDTO.quantity());
            productRepository.save(product);

            // Create a new OrderItem and set its details
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(itemDTO.quantity());
            orderItem.setUnitPrice(product.getPrice());
            orderItems.add(orderItem);

            // Update the total price of the order
            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(itemDTO.quantity())));
        }

        // Create a new Order and set its details
        Order order = new Order();
        order.setCustomer(customer);
        order.setItems(orderItems);
        order.setTotal(total);

        // Link each order item to the order
        order.getItems().forEach(item -> item.setOrder(order));

        // Save and return the order
        return orderRepository.save(order);
    }

    /**
     * Retrieves all orders for a specific customer by their ID.
     *
     * @param customerId The ID of the customer
     * @return A list of orders associated with the customer
     */
    public List<Order> getOrdersByCustomerId(Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    /**
     * Retrieves an order by its ID.
     * Throws a RuntimeException if the order is not found.
     *
     * @param id The ID of the order to retrieve
     * @return The Order entity with the specified ID
     */
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));
    }
}