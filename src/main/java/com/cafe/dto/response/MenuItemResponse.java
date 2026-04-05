package com.cafe.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @Builder
public class MenuItemResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String imageUrl;
    private boolean available;
    private boolean active;
    private Long categoryId;
    private String categoryName;
    private Integer stockQuantity;
    private boolean lowStock;
    private LocalDateTime createdAt;
}
