package com.cafe.service;

import com.cafe.dto.request.InventoryUpdateRequest;
import com.cafe.dto.response.InventoryResponse;

import java.util.List;

public interface InventoryService {
    List<InventoryResponse> getAllInventory();
    List<InventoryResponse> getLowStockItems();
    List<InventoryResponse> getOutOfStockItems();
    InventoryResponse getInventoryByMenuItemId(Long menuItemId);
    InventoryResponse updateInventory(Long menuItemId, InventoryUpdateRequest request);
    InventoryResponse adjustStock(Long menuItemId, int adjustment);
}
