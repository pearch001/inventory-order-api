package com.pearchInventory.ioa.dtos;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record ProductDTO(
        @NotBlank(message = "Product name is required")
        @Size(min = 2, max = 100, message = "Product name must be between 2 and 100 characters")
        String name,

        @NotBlank(message = "Product SKU is required")
        @Size(min = 2, max = 50, message = "Product SKU must be between 2 and 50 characters")
        String sku,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
        BigDecimal price,

        @NotNull(message = "Stock is required")
        @PositiveOrZero(message = "Stock must be zero or a positive number")
        Integer stock
) {
}