package com.pearchInventory.ioa.services;

import com.pearchInventory.ioa.dtos.OrderDTO;
import com.pearchInventory.ioa.dtos.OrderItemDTO;
import com.pearchInventory.ioa.model.Customer;
import com.pearchInventory.ioa.model.Order;
import com.pearchInventory.ioa.model.Product;
import com.pearchInventory.ioa.repositories.CustomerRepository;
import com.pearchInventory.ioa.repositories.OrderRepository;
import com.pearchInventory.ioa.repositories.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrder_Success() {
        Customer customer = new Customer();
        customer.setId(1L);

        Product product = new Product();
        product.setSku("SKU123");
        product.setStock(10);
        product.setPrice(BigDecimal.valueOf(100));

        OrderItemDTO itemDTO = new OrderItemDTO("SKU123", 2, BigDecimal.valueOf(100));
        OrderDTO orderDTO = new OrderDTO(1L, List.of(itemDTO), null, null);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findBySkuForUpdate("SKU123")).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order order = orderService.createOrder(orderDTO);

        assertNotNull(order);
        assertEquals(1, order.getItems().size());
        assertEquals(BigDecimal.valueOf(200), order.getTotal());
        verify(productRepository).save(product);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void createOrder_CustomerNotFound() {
        OrderDTO orderDTO = new OrderDTO(1L, List.of(), null, null);

        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> orderService.createOrder(orderDTO));
        assertEquals("Customer not found", exception.getMessage());
        verify(customerRepository).findById(1L);
    }

    @Test
    void createOrder_ProductNotFound() {
        Customer customer = new Customer();
        customer.setId(1L);

        OrderItemDTO itemDTO = new OrderItemDTO("SKU123", 2, BigDecimal.valueOf(100));
        OrderDTO orderDTO = new OrderDTO(1L, List.of(itemDTO), null, null);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findBySkuForUpdate("SKU123")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> orderService.createOrder(orderDTO));
        assertEquals("Product not found: SKU123", exception.getMessage());
        verify(productRepository).findBySkuForUpdate("SKU123");
    }

    @Test
    void createOrder_InsufficientStock() {
        Customer customer = new Customer();
        customer.setId(1L);

        Product product = new Product();
        product.setSku("SKU123");
        product.setStock(1);

        OrderItemDTO itemDTO = new OrderItemDTO("SKU123", 2, BigDecimal.valueOf(100));
        OrderDTO orderDTO = new OrderDTO(1L, List.of(itemDTO), null, null);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findBySkuForUpdate("SKU123")).thenReturn(Optional.of(product));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> orderService.createOrder(orderDTO));
        assertEquals("Insufficient stock for product: SKU123", exception.getMessage());
        verify(productRepository).findBySkuForUpdate("SKU123");
    }

    @Test
    void getOrdersByCustomerId_Success() {
        Customer customer = new Customer();
        customer.setId(1L);

        Order order1 = new Order();
        order1.setId(1L);

        Order order2 = new Order();
        order2.setId(2L);

        when(orderRepository.findByCustomerId(1L)).thenReturn(List.of(order1, order2));

        List<Order> orders = orderService.getOrdersByCustomerId(1L);

        assertNotNull(orders);
        assertEquals(2, orders.size());
        verify(orderRepository).findByCustomerId(1L);
    }

    @Test
    void getOrderById_Success() {
        Order order = new Order();
        order.setId(1L);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        Order foundOrder = orderService.getOrderById(1L);

        assertNotNull(foundOrder);
        assertEquals(1L, foundOrder.getId());
        verify(orderRepository).findById(1L);
    }

    @Test
    void getOrderById_OrderNotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> orderService.getOrderById(1L));
        assertEquals("Order not found", exception.getMessage());
        verify(orderRepository).findById(1L);
    }
}