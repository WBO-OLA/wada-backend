package com.wada.ola.finance.controller;

import com.wada.ola.common.dto.ApiResponse;
import com.wada.ola.finance.dto.FinanceSummaryDTO;
import com.wada.ola.finance.security.FinanceAccessGuard;
import com.wada.ola.finance.service.FinanceReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/finance/reports")
public class FinanceReportController {

    private final FinanceReportService reportService;
    private final FinanceAccessGuard accessGuard;

    public FinanceReportController(FinanceReportService reportService, FinanceAccessGuard accessGuard) {
        this.reportService = reportService;
        this.accessGuard = accessGuard;
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<FinanceSummaryDTO>> getSummary(
            @RequestParam(required = false) Long commandId,
            @RequestHeader(value = "X-Auth-Role", required = false) String authRole,
            @RequestHeader(value = "X-Auth-Command", required = false) String authCommand) {
        // A zone-scoped caller is pinned to their own command; global callers see whatever they request.
        Long scopedCommandId = accessGuard.resolveScopedCommandId(authRole, authCommand, commandId);
        return ResponseEntity.ok(ApiResponse.ok(reportService.getSummary(scopedCommandId)));
    }
}
