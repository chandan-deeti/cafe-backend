package com.cafe.service;

import com.cafe.dto.response.UserResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface AdminService {
    List<UserResponse> getAllUsers();
    UserResponse getUserById(Long id);
    void toggleUserStatus(Long id);
    Map<String, Object> getDashboardStats();
    Map<String, Object> getSalesReport(LocalDateTime from, LocalDateTime to);
}
