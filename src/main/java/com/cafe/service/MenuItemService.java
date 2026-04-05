package com.cafe.service;

import com.cafe.dto.request.MenuItemRequest;
import com.cafe.dto.response.MenuItemResponse;

import java.util.List;

public interface MenuItemService {
    List<MenuItemResponse> getAllMenuItems();
    List<MenuItemResponse> getAvailableMenuItems();
    List<MenuItemResponse> getMenuItemsByCategory(Long categoryId);
    List<MenuItemResponse> searchMenuItems(String keyword);
    MenuItemResponse getMenuItemById(Long id);
    MenuItemResponse createMenuItem(MenuItemRequest request);
    MenuItemResponse updateMenuItem(Long id, MenuItemRequest request);
    void deleteMenuItem(Long id);
    MenuItemResponse toggleAvailability(Long id);
}
