package com.cafe.service.impl;

import com.cafe.dto.response.UserResponse;
import com.cafe.entity.OrderStatus;
import com.cafe.entity.User;
import com.cafe.exception.ResourceNotFoundException;
import com.cafe.repository.InventoryItemRepository;
import com.cafe.repository.MenuItemRepository;
import com.cafe.repository.OrderRepository;
import com.cafe.repository.UserRepository;
import com.cafe.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final MenuItemRepository menuItemRepository;
    private final InventoryItemRepository inventoryItemRepository;

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse getUserById(Long id) {
        return toUserResponse(findUserById(id));
    }

    @Override
    @Transactional
    public void toggleUserStatus(Long id) {
        User user = findUserById(id);
        user.setEnabled(!user.isEnabled());
        userRepository.save(user);
    }

    @Override
    public Map<String, Object> getDashboardStats() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime now = LocalDateTime.now();

        long totalUsers = userRepository.count();
        long totalMenuItems = menuItemRepository.findByActiveTrue().size();
        long lowStockCount = inventoryItemRepository.findLowStockItems().size();
        long outOfStockCount = inventoryItemRepository.findOutOfStockItems().size();

        Long todayOrderCount = orderRepository.countOrdersByDateRange(startOfDay, now);
        BigDecimal todayRevenue = orderRepository.sumRevenueByDateRange(startOfDay, now);

        long pendingOrders = orderRepository.findByStatusOrderByCreatedAtDesc(OrderStatus.PENDING).size();
        long preparingOrders = orderRepository.findByStatusOrderByCreatedAtDesc(OrderStatus.PREPARING).size();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", totalUsers);
        stats.put("totalMenuItems", totalMenuItems);
        stats.put("lowStockAlerts", lowStockCount);
        stats.put("outOfStockItems", outOfStockCount);
        stats.put("todayOrders", todayOrderCount != null ? todayOrderCount : 0);
        stats.put("todayRevenue", todayRevenue != null ? todayRevenue : BigDecimal.ZERO);
        stats.put("pendingOrders", pendingOrders);
        stats.put("preparingOrders", preparingOrders);
        return stats;
    }

    @Override
    public Map<String, Object> getSalesReport(LocalDateTime from, LocalDateTime to) {
        var orders = orderRepository.findByDateRange(from, to);
        BigDecimal totalRevenue = orderRepository.sumRevenueByDateRange(from, to);
        Long totalOrders = orderRepository.countOrdersByDateRange(from, to);

        long cancelled = orders.stream().filter(o -> o.getStatus() == OrderStatus.CANCELLED).count();
        long delivered = orders.stream().filter(o -> o.getStatus() == OrderStatus.DELIVERED).count();

        Map<String, Object> report = new HashMap<>();
        report.put("from", from);
        report.put("to", to);
        report.put("totalOrders", totalOrders != null ? totalOrders : 0);
        report.put("totalRevenue", totalRevenue != null ? totalRevenue : BigDecimal.ZERO);
        report.put("cancelledOrders", cancelled);
        report.put("deliveredOrders", delivered);
        report.put("averageOrderValue", (totalOrders != null && totalOrders > 0 && totalRevenue != null)
                ? totalRevenue.divide(BigDecimal.valueOf(totalOrders), 2, java.math.RoundingMode.HALF_UP)
                : BigDecimal.ZERO);
        return report;
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }

    private UserResponse toUserResponse(User u) {
        return UserResponse.builder()
                .id(u.getId())
                .email(u.getEmail())
                .fullName(u.getFullName())
                .phone(u.getPhone())
                .role(u.getRole())
                .enabled(u.isEnabled())
                .createdAt(u.getCreatedAt())
                .build();
    }
}
