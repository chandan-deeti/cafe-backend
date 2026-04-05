package com.cafe.controller;

import com.cafe.dto.request.CartItemRequest;
import com.cafe.dto.response.ApiResponse;
import com.cafe.dto.response.CartResponse;
import com.cafe.entity.User;
import com.cafe.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Tag(name = "Cart", description = "Shopping cart operations for customers")
public class CartController {

    private final CartService cartService;

    @GetMapping
    @Operation(summary = "Get current user's cart")
    public ResponseEntity<ApiResponse<CartResponse>> getCart(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.success(cartService.getCart(user.getId())));
    }

    @PostMapping("/items")
    @Operation(summary = "Add item to cart")
    public ResponseEntity<ApiResponse<CartResponse>> addItem(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CartItemRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                cartService.addItem(user.getId(), request), "Item added to cart"));
    }

    @PutMapping("/items/{menuItemId}")
    @Operation(summary = "Update item quantity in cart (quantity=0 removes it)")
    public ResponseEntity<ApiResponse<CartResponse>> updateItem(
            @AuthenticationPrincipal User user,
            @PathVariable Long menuItemId,
            @RequestParam Integer quantity) {
        return ResponseEntity.ok(ApiResponse.success(
                cartService.updateItem(user.getId(), menuItemId, quantity), "Cart updated"));
    }

    @DeleteMapping("/items/{menuItemId}")
    @Operation(summary = "Remove item from cart")
    public ResponseEntity<ApiResponse<CartResponse>> removeItem(
            @AuthenticationPrincipal User user,
            @PathVariable Long menuItemId) {
        return ResponseEntity.ok(ApiResponse.success(
                cartService.removeItem(user.getId(), menuItemId), "Item removed"));
    }

    @DeleteMapping
    @Operation(summary = "Clear entire cart")
    public ResponseEntity<ApiResponse<CartResponse>> clearCart(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.success(
                cartService.clearCart(user.getId()), "Cart cleared"));
    }
}
