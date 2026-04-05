package com.cafe.service;

import com.cafe.dto.request.OrderRequest;
import com.cafe.dto.request.OrderStatusRequest;
import com.cafe.dto.response.OrderResponse;
import com.cafe.entity.OrderStatus;

import java.util.List;

public interface OrderService {
    OrderResponse placeOrder(Long userId, OrderRequest request);
    OrderResponse getOrderById(Long id);
    OrderResponse getOrderByNumber(String orderNumber);
    List<OrderResponse> getMyOrders(Long userId);
    List<OrderResponse> getAllOrders();
    List<OrderResponse> getOrdersByStatus(OrderStatus status);
    OrderResponse updateOrderStatus(Long id, OrderStatusRequest request);
    OrderResponse cancelOrder(Long id, Long userId);
}
