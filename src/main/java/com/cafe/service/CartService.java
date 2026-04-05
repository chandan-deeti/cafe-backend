package com.cafe.service;

import com.cafe.dto.request.CartItemRequest;
import com.cafe.dto.response.CartResponse;

public interface CartService {
    CartResponse getCart(Long userId);
    CartResponse addItem(Long userId, CartItemRequest request);
    CartResponse updateItem(Long userId, Long menuItemId, Integer quantity);
    CartResponse removeItem(Long userId, Long menuItemId);
    CartResponse clearCart(Long userId);
}
