package com.wada.ola.reporting.controller;

import com.wada.ola.common.dto.ApiResponse;
import com.wada.ola.reporting.dto.AuditLogPage;
import com.wada.ola.reporting.service.AuditLogAggregationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports/audit-logs")
public class AuditLogController {

    private final AuditLogAggregationService auditLogAggregationService;

    public AuditLogController(AuditLogAggregationService auditLogAggregationService) {
        this.auditLogAggregationService = auditLogAggregationService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<AuditLogPage>> search(
            HttpServletRequest httpRequest,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String targetTable,
            @RequestParam(required = false) Long commandId,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        String authRole = httpRequest.getHeader("X-Auth-Role");
        if (!"ADMIN".equals(authRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Admin role required"));
        }
        String authorization = httpRequest.getHeader("Authorization");
        return ResponseEntity.ok(ApiResponse.ok(
                auditLogAggregationService.search(action, targetTable, commandId, from, to, page, size, authorization, authRole)));
    }
}
