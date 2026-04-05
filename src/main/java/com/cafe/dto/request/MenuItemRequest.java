package com.cafe.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MenuItemRequest {

    @NotBlank(message = "Item name is required")
    private String name;

    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;

    private String imageUrl;

    private boolean available = true;

    private boolean active = true;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    // Inventory fields (optional at creation)
    private Integer initialStock;
    private Integer lowStockThreshold;
    private String unit;
}
