package com.cafe.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data @Builder
public class InventoryResponse {
    private Long id;
    private Long menuItemId;
    private String menuItemName;
    private Integer quantity;
    private Integer lowStockThreshold;
    private String unit;
    private String notes;
    private boolean lowStock;
    private boolean outOfStock;
    private LocalDateTime updatedAt;
}
