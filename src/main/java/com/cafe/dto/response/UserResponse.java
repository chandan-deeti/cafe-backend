package com.cafe.dto.response;

import com.cafe.entity.Role;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data @Builder
public class UserResponse {
    private Long id;
    private String email;
    private String fullName;
    private String phone;
    private Role role;
    private boolean enabled;
    private LocalDateTime createdAt;
}
