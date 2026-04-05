package com.cafe.dto.request;

import lombok.Data;

@Data
public class OrderRequest {
    private String specialInstructions;
    private String tableNumber;
}
