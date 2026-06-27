package com.wada.ola.inventory.controller;

import com.wada.ola.common.annotation.Audited;
import com.wada.ola.common.dto.ApiResponse;
import com.wada.ola.inventory.dto.WarehouseRequest;
import com.wada.ola.inventory.entity.Item;
import com.wada.ola.inventory.entity.Warehouse;
import com.wada.ola.inventory.service.ItemService;
import com.wada.ola.inventory.service.WarehouseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory/warehouses")
public class WarehouseController {

    private final WarehouseService warehouseService;
    private final ItemService itemService;

    public WarehouseController(WarehouseService warehouseService, ItemService itemService) {
        this.warehouseService = warehouseService;
        this.itemService = itemService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Warehouse>>> getAll(
            @RequestParam(required = false) Boolean active) {
        List<Warehouse> warehouses = (active != null && active)
                ? warehouseService.findActive()
                : warehouseService.findAll();
        return ResponseEntity.ok(ApiResponse.ok(warehouses));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Warehouse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(warehouseService.findById(id)));
    }

    @GetMapping("/{id}/items")
    public ResponseEntity<ApiResponse<List<Item>>> getItems(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(itemService.findByWarehouse(id)));
    }

    @PostMapping
    @Audited(action = "WAREHOUSE_CREATE", targetTable = "warehouses")
    public ResponseEntity<ApiResponse<Warehouse>> create(@RequestBody WarehouseRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Warehouse created", warehouseService.create(request)));
    }

    @PutMapping("/{id}")
    @Audited(action = "WAREHOUSE_UPDATE", targetTable = "warehouses")
    public ResponseEntity<ApiResponse<Warehouse>> update(@PathVariable Long id,
                                                          @RequestBody WarehouseRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Warehouse updated", warehouseService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @Audited(action = "WAREHOUSE_DEACTIVATE", targetTable = "warehouses")
    public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable Long id) {
        warehouseService.deactivate(id);
        return ResponseEntity.ok(ApiResponse.ok("Warehouse deactivated", null));
    }
}
