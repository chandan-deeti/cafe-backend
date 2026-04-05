package com.cafe.dto.response;

import com.cafe.entity.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data @Builder
public class OrderResponse {
    private Long id;
    private String orderNumber;
    private Long customerId;
    private String customerName;
    private String customerEmail;
    private List<OrderItemResponse> orderItems;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private String specialInstructions;
    private String tableNumber;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
