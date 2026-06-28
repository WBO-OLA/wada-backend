package com.wada.ola.reporting.controller;

import com.wada.ola.common.dto.ApiResponse;
import com.wada.ola.reporting.dto.DashboardReport;
import com.wada.ola.reporting.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardReport>> getDashboard(
            @RequestParam(required = false) Long commandId,
            @RequestHeader(value = "X-Auth-Role", required = false) String authRole,
            @RequestHeader(value = "X-Auth-Command", required = false) String authCommand) {
        Long scopedId = resolveCommandId(authRole, authCommand, commandId);
        return ResponseEntity.ok(ApiResponse.ok(reportService.getDashboard(scopedId)));
    }

    private static Long resolveCommandId(String role, String authCommand, Long requested) {
        boolean global = role == null || role.equals("CHIEF") || role.equals("ADMIN");
        if (global) return requested;
        if (authCommand != null && !authCommand.isBlank()) return Long.parseLong(authCommand);
        return requested;
    }
}
