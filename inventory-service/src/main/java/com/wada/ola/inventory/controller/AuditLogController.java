package com.wada.ola.inventory.controller;

import com.wada.ola.common.dto.ApiResponse;
import com.wada.ola.common.entity.AuditLog;
import com.wada.ola.common.service.AuditLogQueryService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/inventory/audit-logs")
public class AuditLogController {

    private final AuditLogQueryService auditLogQueryService;

    public AuditLogController(AuditLogQueryService auditLogQueryService) {
        this.auditLogQueryService = auditLogQueryService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<AuditLog>>> search(
            HttpServletRequest httpRequest,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String targetTable,
            @RequestParam(required = false) Long commandId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        if (!"ADMIN".equals(httpRequest.getHeader("X-Auth-Role"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Admin role required"));
        }
        return ResponseEntity.ok(ApiResponse.ok(
                auditLogQueryService.search(action, targetTable, commandId, from, to, page, size)));
    }
}
