package com.pearchInventory.ioa.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderDTO(
        @NotNull(message = "Customer ID is required")
        @Positive(message = "Customer ID must be a positive number")
        Long customerId,

        @NotNull(message = "Order items are required")
        @Size(min = 1, message = "Order must contain at least one item")
        List<@Valid OrderItemDTO> items,

        @NotNull(message = "Total amount is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Total must be greater than 0")
        BigDecimal total,

        @NotNull(message = "Order date is required")
        @PastOrPresent(message = "Order date cannot be in the future")
        LocalDateTime orderDate
) {
}
