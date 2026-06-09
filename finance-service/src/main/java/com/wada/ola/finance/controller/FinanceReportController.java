package com.wada.ola.finance.controller;

import com.wada.ola.common.dto.ApiResponse;
import com.wada.ola.finance.dto.FinanceSummaryDTO;
import com.wada.ola.finance.service.FinanceReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/finance/reports")
public class FinanceReportController {

    private final FinanceReportService reportService;

    public FinanceReportController(FinanceReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<FinanceSummaryDTO>> getSummary() {
        return ResponseEntity.ok(ApiResponse.ok(reportService.getSummary()));
    }
}
