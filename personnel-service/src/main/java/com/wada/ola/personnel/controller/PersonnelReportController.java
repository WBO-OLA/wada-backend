package com.wada.ola.personnel.controller;

import com.wada.ola.common.dto.ApiResponse;
import com.wada.ola.personnel.dto.PersonnelSummaryDTO;
import com.wada.ola.personnel.security.MemberAccessGuard;
import com.wada.ola.personnel.service.PersonnelReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/personnel/reports")
public class PersonnelReportController {

    private final PersonnelReportService reportService;
    private final MemberAccessGuard accessGuard;

    public PersonnelReportController(PersonnelReportService reportService, MemberAccessGuard accessGuard) {
        this.reportService = reportService;
        this.accessGuard = accessGuard;
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<PersonnelSummaryDTO>> getSummary(
            @RequestParam(required = false) Long commandId,
            @RequestHeader(value = "X-Auth-Role", required = false) String authRole,
            @RequestHeader(value = "X-Auth-Command", required = false) String authCommand) {
        if (accessGuard.isGlobal(authRole)) {
            return ResponseEntity.ok(ApiResponse.ok(reportService.getSummary(commandId)));
        }
        // Zone-scoped caller: the summary always covers their own command subtree,
        // ignoring any requested commandId so they cannot read another zone's totals.
        return ResponseEntity.ok(ApiResponse.ok(
                reportService.getSummary(accessGuard.scopedCommandIds(authCommand))));
    }
}
