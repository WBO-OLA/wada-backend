package com.wada.ola.inventory.controller;

import com.wada.ola.common.dto.ApiResponse;
import com.wada.ola.inventory.dto.PurchaseOrderRequest;
import com.wada.ola.inventory.entity.PurchaseOrder;
import com.wada.ola.inventory.service.PurchaseOrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory/purchase-orders")
public class PurchaseOrderController {

    private final PurchaseOrderService service;

    public PurchaseOrderController(PurchaseOrderService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PurchaseOrder>>> getAll(
            @RequestParam(required = false) PurchaseOrder.OrderStatus status) {
        return ResponseEntity.ok(ApiResponse.ok(service.findAll(status)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PurchaseOrder>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(service.findById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PurchaseOrder>> create(@RequestBody PurchaseOrderRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Purchase order created", service.create(request)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<PurchaseOrder>> updateStatus(@PathVariable Long id,
                                                                    @RequestBody Map<String, String> body) {
        PurchaseOrder.OrderStatus status = PurchaseOrder.OrderStatus.valueOf(body.get("status").toUpperCase());
        return ResponseEntity.ok(ApiResponse.ok("Status updated", service.updateStatus(id, status)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.<Void>ok("Purchase order deleted", null));
    }
}
