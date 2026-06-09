package com.wada.ola.inventory.controller;

import com.wada.ola.common.dto.ApiResponse;
import com.wada.ola.inventory.dto.InventorySummaryDTO;
import com.wada.ola.inventory.service.InventoryReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inventory/reports")
public class InventoryReportController {

    private final InventoryReportService reportService;

    public InventoryReportController(InventoryReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<InventorySummaryDTO>> getSummary() {
        return ResponseEntity.ok(ApiResponse.ok(reportService.getSummary()));
    }
}
