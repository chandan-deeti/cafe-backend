package com.cafe.dto.request;

import com.cafe.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderStatusRequest {

    @NotNull(message = "Status is required")
    private OrderStatus status;
}
