package com.wada.ola.inventory.controller;

import com.wada.ola.common.dto.ApiResponse;
import com.wada.ola.inventory.dto.AssetAssignmentRequest;
import com.wada.ola.inventory.entity.AssetAssignment;
import com.wada.ola.inventory.service.AssetAssignmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/inventory/assignments")
public class AssetAssignmentController {

    private final AssetAssignmentService service;

    public AssetAssignmentController(AssetAssignmentService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AssetAssignment>>> getAll(
            @RequestParam(required = false) Long itemId) {
        List<AssetAssignment> results = itemId != null
                ? service.findByItem(itemId)
                : service.findAll();
        return ResponseEntity.ok(ApiResponse.ok(results));
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<ApiResponse<List<AssetAssignment>>> getActiveByMember(@PathVariable Long memberId) {
        return ResponseEntity.ok(ApiResponse.ok(service.findActiveByMember(memberId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AssetAssignment>> assign(@RequestBody AssetAssignmentRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Asset assigned", service.assign(request)));
    }

    @PatchMapping("/{id}/return")
    public ResponseEntity<ApiResponse<AssetAssignment>> returnItem(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Asset returned", service.returnItem(id)));
    }
}
