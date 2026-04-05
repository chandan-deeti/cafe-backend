package com.cafe.controller;

import com.cafe.dto.request.MenuItemRequest;
import com.cafe.dto.response.ApiResponse;
import com.cafe.dto.response.MenuItemResponse;
import com.cafe.service.MenuItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor
@Tag(name = "Menu Items", description = "Menu item management and browsing")
public class MenuItemController {

    private final MenuItemService menuItemService;

    @GetMapping
    @Operation(summary = "Get all available menu items (public)")
    public ResponseEntity<ApiResponse<List<MenuItemResponse>>> getAvailableItems() {
        return ResponseEntity.ok(ApiResponse.success(menuItemService.getAvailableMenuItems()));
    }

    @GetMapping("/all")
    @Operation(summary = "Get all menu items including unavailable (Admin/Staff)")
    public ResponseEntity<ApiResponse<List<MenuItemResponse>>> getAllItems() {
        return ResponseEntity.ok(ApiResponse.success(menuItemService.getAllMenuItems()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get menu item by ID (public)")
    public ResponseEntity<ApiResponse<MenuItemResponse>> getItemById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(menuItemService.getMenuItemById(id)));
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get menu items by category (public)")
    public ResponseEntity<ApiResponse<List<MenuItemResponse>>> getByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(ApiResponse.success(menuItemService.getMenuItemsByCategory(categoryId)));
    }

    @GetMapping("/search")
    @Operation(summary = "Search menu items by keyword (public)")
    public ResponseEntity<ApiResponse<List<MenuItemResponse>>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(ApiResponse.success(menuItemService.searchMenuItems(keyword)));
    }

    @PostMapping
    @Operation(summary = "Create a new menu item (Admin/Staff)")
    public ResponseEntity<ApiResponse<MenuItemResponse>> createItem(
            @Valid @RequestBody MenuItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(menuItemService.createMenuItem(request), "Menu item created"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a menu item (Admin/Staff)")
    public ResponseEntity<ApiResponse<MenuItemResponse>> updateItem(
            @PathVariable Long id, @Valid @RequestBody MenuItemRequest request) {
        return ResponseEntity.ok(ApiResponse.success(menuItemService.updateMenuItem(id, request), "Menu item updated"));
    }

    @PatchMapping("/{id}/toggle-availability")
    @Operation(summary = "Toggle item availability (Admin/Staff)")
    public ResponseEntity<ApiResponse<MenuItemResponse>> toggleAvailability(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(menuItemService.toggleAvailability(id), "Availability updated"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete a menu item (Admin)")
    public ResponseEntity<ApiResponse<Void>> deleteItem(@PathVariable Long id) {
        menuItemService.deleteMenuItem(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Menu item deleted"));
    }
}
