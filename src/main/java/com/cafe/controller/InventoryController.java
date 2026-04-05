package com.cafe.controller;

import com.cafe.dto.request.InventoryUpdateRequest;
import com.cafe.dto.response.ApiResponse;
import com.cafe.dto.response.InventoryResponse;
import com.cafe.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory", description = "Inventory management (Admin/Staff only)")
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    @Operation(summary = "Get full inventory list")
    public ResponseEntity<ApiResponse<List<InventoryResponse>>> getAllInventory() {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.getAllInventory()));
    }

    @GetMapping("/low-stock")
    @Operation(summary = "Get low stock items")
    public ResponseEntity<ApiResponse<List<InventoryResponse>>> getLowStock() {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.getLowStockItems()));
    }

    @GetMapping("/out-of-stock")
    @Operation(summary = "Get out-of-stock items")
    public ResponseEntity<ApiResponse<List<InventoryResponse>>> getOutOfStock() {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.getOutOfStockItems()));
    }

    @GetMapping("/menu-item/{menuItemId}")
    @Operation(summary = "Get inventory for a specific menu item")
    public ResponseEntity<ApiResponse<InventoryResponse>> getByMenuItem(@PathVariable Long menuItemId) {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.getInventoryByMenuItemId(menuItemId)));
    }

    @PutMapping("/menu-item/{menuItemId}")
    @Operation(summary = "Update inventory for a menu item (set quantity)")
    public ResponseEntity<ApiResponse<InventoryResponse>> updateInventory(
            @PathVariable Long menuItemId,
            @Valid @RequestBody InventoryUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                inventoryService.updateInventory(menuItemId, request), "Inventory updated"));
    }

    @PatchMapping("/menu-item/{menuItemId}/adjust")
    @Operation(summary = "Adjust stock by delta (positive=add, negative=subtract)")
    public ResponseEntity<ApiResponse<InventoryResponse>> adjustStock(
            @PathVariable Long menuItemId,
            @RequestParam int delta) {
        return ResponseEntity.ok(ApiResponse.success(
                inventoryService.adjustStock(menuItemId, delta), "Stock adjusted"));
    }
}
