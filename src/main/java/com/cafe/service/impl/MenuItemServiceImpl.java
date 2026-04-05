package com.cafe.service.impl;

import com.cafe.dto.request.MenuItemRequest;
import com.cafe.dto.response.MenuItemResponse;
import com.cafe.entity.Category;
import com.cafe.entity.InventoryItem;
import com.cafe.entity.MenuItem;
import com.cafe.exception.ResourceNotFoundException;
import com.cafe.repository.CategoryRepository;
import com.cafe.repository.InventoryItemRepository;
import com.cafe.repository.MenuItemRepository;
import com.cafe.service.MenuItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuItemServiceImpl implements MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final CategoryRepository categoryRepository;
    private final InventoryItemRepository inventoryItemRepository;

    @Override
    public List<MenuItemResponse> getAllMenuItems() {
        return menuItemRepository.findByActiveTrue().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<MenuItemResponse> getAvailableMenuItems() {
        return menuItemRepository.findByActiveTrueAndAvailableTrue().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<MenuItemResponse> getMenuItemsByCategory(Long categoryId) {
        return menuItemRepository.findByCategoryIdAndActiveTrue(categoryId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<MenuItemResponse> searchMenuItems(String keyword) {
        return menuItemRepository.searchByKeyword(keyword).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public MenuItemResponse getMenuItemById(Long id) {
        return toResponse(findById(id));
    }

    @Override
    @Transactional
    public MenuItemResponse createMenuItem(MenuItemRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", request.getCategoryId()));

        MenuItem item = MenuItem.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .imageUrl(request.getImageUrl())
                .available(request.isAvailable())
                .active(request.isActive())
                .category(category)
                .build();

        item = menuItemRepository.save(item);

        // Auto-create inventory entry
        InventoryItem inventory = InventoryItem.builder()
                .menuItem(item)
                .quantity(request.getInitialStock() != null ? request.getInitialStock() : 0)
                .lowStockThreshold(request.getLowStockThreshold() != null ? request.getLowStockThreshold() : 10)
                .unit(request.getUnit())
                .build();
        inventoryItemRepository.save(inventory);

        return toResponse(menuItemRepository.findById(item.getId()).orElse(item));
    }

    @Override
    @Transactional
    public MenuItemResponse updateMenuItem(Long id, MenuItemRequest request) {
        MenuItem item = findById(id);
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", request.getCategoryId()));

        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setPrice(request.getPrice());
        item.setImageUrl(request.getImageUrl());
        item.setAvailable(request.isAvailable());
        item.setActive(request.isActive());
        item.setCategory(category);

        return toResponse(menuItemRepository.save(item));
    }

    @Override
    @Transactional
    public void deleteMenuItem(Long id) {
        MenuItem item = findById(id);
        item.setActive(false);
        menuItemRepository.save(item);
    }

    @Override
    @Transactional
    public MenuItemResponse toggleAvailability(Long id) {
        MenuItem item = findById(id);
        item.setAvailable(!item.isAvailable());
        return toResponse(menuItemRepository.save(item));
    }

    private MenuItem findById(Long id) {
        return menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MenuItem", id));
    }

    MenuItemResponse toResponse(MenuItem m) {
        InventoryItem inv = m.getInventoryItem();
        return MenuItemResponse.builder()
                .id(m.getId())
                .name(m.getName())
                .description(m.getDescription())
                .price(m.getPrice())
                .imageUrl(m.getImageUrl())
                .available(m.isAvailable())
                .active(m.isActive())
                .categoryId(m.getCategory().getId())
                .categoryName(m.getCategory().getName())
                .stockQuantity(inv != null ? inv.getQuantity() : null)
                .lowStock(inv != null && inv.isLowStock())
                .createdAt(m.getCreatedAt())
                .build();
    }
}
