package com.wada.ola.reporting.controller;

import com.wada.ola.common.dto.ApiResponse;
import com.wada.ola.reporting.dto.DashboardReport;
import com.wada.ola.reporting.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardReport>> getDashboard() {
        return ResponseEntity.ok(ApiResponse.ok(reportService.getDashboard()));
    }
}
