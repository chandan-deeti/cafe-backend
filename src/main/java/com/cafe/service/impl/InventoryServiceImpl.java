package com.cafe.service.impl;

import com.cafe.dto.request.InventoryUpdateRequest;
import com.cafe.dto.response.InventoryResponse;
import com.cafe.entity.InventoryItem;
import com.cafe.entity.MenuItem;
import com.cafe.exception.BadRequestException;
import com.cafe.exception.ResourceNotFoundException;
import com.cafe.repository.InventoryItemRepository;
import com.cafe.repository.MenuItemRepository;
import com.cafe.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryItemRepository inventoryItemRepository;
    private final MenuItemRepository menuItemRepository;

    @Override
    public List<InventoryResponse> getAllInventory() {
        return inventoryItemRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<InventoryResponse> getLowStockItems() {
        return inventoryItemRepository.findLowStockItems().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<InventoryResponse> getOutOfStockItems() {
        return inventoryItemRepository.findOutOfStockItems().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public InventoryResponse getInventoryByMenuItemId(Long menuItemId) {
        InventoryItem item = inventoryItemRepository.findByMenuItemId(menuItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory for menu item " + menuItemId + " not found"));
        return toResponse(item);
    }

    @Override
    @Transactional
    public InventoryResponse updateInventory(Long menuItemId, InventoryUpdateRequest request) {
        InventoryItem item = inventoryItemRepository.findByMenuItemId(menuItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory for menu item " + menuItemId + " not found"));

        item.setQuantity(request.getQuantity());
        if (request.getLowStockThreshold() != null) {
            item.setLowStockThreshold(request.getLowStockThreshold());
        }
        if (request.getUnit() != null) {
            item.setUnit(request.getUnit());
        }
        if (request.getNotes() != null) {
            item.setNotes(request.getNotes());
        }

        // Sync menu item availability
        MenuItem menuItem = item.getMenuItem();
        menuItem.setAvailable(item.getQuantity() > 0);
        menuItemRepository.save(menuItem);

        return toResponse(inventoryItemRepository.save(item));
    }

    @Override
    @Transactional
    public InventoryResponse adjustStock(Long menuItemId, int adjustment) {
        InventoryItem item = inventoryItemRepository.findByMenuItemId(menuItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory for menu item " + menuItemId + " not found"));

        int newQty = item.getQuantity() + adjustment;
        if (newQty < 0) {
            throw new BadRequestException("Insufficient stock. Current: " + item.getQuantity());
        }
        item.setQuantity(newQty);

        MenuItem menuItem = item.getMenuItem();
        menuItem.setAvailable(newQty > 0);
        menuItemRepository.save(menuItem);

        return toResponse(inventoryItemRepository.save(item));
    }

    private InventoryResponse toResponse(InventoryItem i) {
        return InventoryResponse.builder()
                .id(i.getId())
                .menuItemId(i.getMenuItem().getId())
                .menuItemName(i.getMenuItem().getName())
                .quantity(i.getQuantity())
                .lowStockThreshold(i.getLowStockThreshold())
                .unit(i.getUnit())
                .notes(i.getNotes())
                .lowStock(i.isLowStock())
                .outOfStock(i.isOutOfStock())
                .updatedAt(i.getUpdatedAt())
                .build();
    }
}
