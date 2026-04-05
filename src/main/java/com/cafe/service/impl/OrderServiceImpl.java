package com.cafe.service.impl;

import com.cafe.dto.request.OrderRequest;
import com.cafe.dto.request.OrderStatusRequest;
import com.cafe.dto.response.OrderItemResponse;
import com.cafe.dto.response.OrderResponse;
import com.cafe.entity.*;
import com.cafe.exception.BadRequestException;
import com.cafe.exception.ResourceNotFoundException;
import com.cafe.repository.*;
import com.cafe.service.CartService;
import com.cafe.service.InventoryService;
import com.cafe.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final InventoryService inventoryService;
    private final CartService cartService;

    @Override
    @Transactional
    public OrderResponse placeOrder(Long userId, OrderRequest request) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException("Cart is empty"));

        if (cart.getCartItems().isEmpty()) {
            throw new BadRequestException("Cannot place order with empty cart");
        }

        User customer = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        // Build order items and calculate total
        BigDecimal total = BigDecimal.ZERO;
        Order order = Order.builder()
                .orderNumber(generateOrderNumber())
                .customer(customer)
                .status(OrderStatus.PENDING)
                .specialInstructions(request.getSpecialInstructions())
                .tableNumber(request.getTableNumber())
                .totalAmount(BigDecimal.ZERO)
                .build();

        order = orderRepository.save(order);

        List<OrderItem> orderItems = new java.util.ArrayList<>();
        for (CartItem cartItem : cart.getCartItems()) {
            MenuItem menuItem = cartItem.getMenuItem();
            if (!menuItem.isAvailable()) {
                throw new BadRequestException("Item unavailable: " + menuItem.getName());
            }

            BigDecimal subtotal = menuItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            total = total.add(subtotal);

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .menuItem(menuItem)
                    .quantity(cartItem.getQuantity())
                    .unitPrice(menuItem.getPrice())
                    .subtotal(subtotal)
                    .customization(cartItem.getCustomization())
                    .build();
            orderItems.add(orderItem);

            // Deduct from inventory
            inventoryService.adjustStock(menuItem.getId(), -cartItem.getQuantity());
        }

        order.setOrderItems(orderItems);
        order.setTotalAmount(total);
        order = orderRepository.save(order);

        // Clear cart
        cartService.clearCart(userId);

        return toResponse(order);
    }

    @Override
    public OrderResponse getOrderById(Long id) {
        return toResponse(findById(id));
    }

    @Override
    public OrderResponse getOrderByNumber(String orderNumber) {
        return toResponse(orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderNumber)));
    }

    @Override
    public List<OrderResponse> getMyOrders(Long userId) {
        return orderRepository.findByCustomerIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderResponse> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatusOrderByCreatedAtDesc(status).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Long id, OrderStatusRequest request) {
        Order order = findById(id);
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new BadRequestException("Cannot update a cancelled order");
        }
        order.setStatus(request.getStatus());
        return toResponse(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(Long id, Long userId) {
        Order order = findById(id);
        if (!order.getCustomer().getId().equals(userId)) {
            throw new BadRequestException("You can only cancel your own orders");
        }
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BadRequestException("Only PENDING orders can be cancelled");
        }

        // Restore inventory
        for (OrderItem item : order.getOrderItems()) {
            inventoryService.adjustStock(item.getMenuItem().getId(), item.getQuantity());
        }

        order.setStatus(OrderStatus.CANCELLED);
        return toResponse(orderRepository.save(order));
    }

    private Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
    }

    private String generateOrderNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = (int) (Math.random() * 9000) + 1000;
        return "ORD-" + timestamp + "-" + random;
    }

    private OrderResponse toResponse(Order o) {
        List<OrderItemResponse> items = o.getOrderItems() == null ? List.of() :
                o.getOrderItems().stream().map(this::toItemResponse).collect(Collectors.toList());

        return OrderResponse.builder()
                .id(o.getId())
                .orderNumber(o.getOrderNumber())
                .customerId(o.getCustomer().getId())
                .customerName(o.getCustomer().getFullName())
                .customerEmail(o.getCustomer().getEmail())
                .orderItems(items)
                .status(o.getStatus())
                .totalAmount(o.getTotalAmount())
                .specialInstructions(o.getSpecialInstructions())
                .tableNumber(o.getTableNumber())
                .createdAt(o.getCreatedAt())
                .updatedAt(o.getUpdatedAt())
                .build();
    }

    private OrderItemResponse toItemResponse(OrderItem oi) {
        return OrderItemResponse.builder()
                .id(oi.getId())
                .menuItemId(oi.getMenuItem().getId())
                .menuItemName(oi.getMenuItem().getName())
                .menuItemImageUrl(oi.getMenuItem().getImageUrl())
                .quantity(oi.getQuantity())
                .unitPrice(oi.getUnitPrice())
                .subtotal(oi.getSubtotal())
                .customization(oi.getCustomization())
                .build();
    }
}
