package com.pearchInventory.ioa.dtos;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record OrderItemDTO(
        @NotBlank(message = "Product SKU is required")
        String productSku,

        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be a positive number")
        Integer quantity,

        @NotNull(message = "Unit price is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Unit price must be greater than 0")
        BigDecimal unitPrice
) {
}