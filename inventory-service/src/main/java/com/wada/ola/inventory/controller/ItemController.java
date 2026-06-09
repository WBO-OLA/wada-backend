package com.wada.ola.inventory.controller;

import com.wada.ola.common.dto.ApiResponse;
import com.wada.ola.inventory.dto.ItemRequest;
import com.wada.ola.inventory.entity.Item;
import com.wada.ola.inventory.service.ItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Item>>> getAll(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Long warehouseId) {
        List<Item> items;
        if (category != null) items = itemService.findByCategory(category);
        else if (warehouseId != null) items = itemService.findByWarehouse(warehouseId);
        else items = itemService.findAll();
        return ResponseEntity.ok(ApiResponse.ok(items));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Item>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(itemService.findById(id)));
    }

    @GetMapping("/low-stock")
    public ResponseEntity<ApiResponse<List<Item>>> getLowStock(
            @RequestParam(defaultValue = "10") int threshold) {
        return ResponseEntity.ok(ApiResponse.ok(itemService.findLowStock(threshold)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Item>> create(@RequestBody ItemRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Item created", itemService.create(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Item>> update(@PathVariable Long id,
                                                     @RequestBody ItemRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Item updated", itemService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        itemService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Item deleted", null));
    }
}
