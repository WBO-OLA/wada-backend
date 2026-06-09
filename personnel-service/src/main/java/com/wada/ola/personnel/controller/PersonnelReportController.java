package com.wada.ola.personnel.controller;

import com.wada.ola.common.dto.ApiResponse;
import com.wada.ola.personnel.dto.PersonnelSummaryDTO;
import com.wada.ola.personnel.service.PersonnelReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/personnel/reports")
public class PersonnelReportController {

    private final PersonnelReportService reportService;

    public PersonnelReportController(PersonnelReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<PersonnelSummaryDTO>> getSummary() {
        return ResponseEntity.ok(ApiResponse.ok(reportService.getSummary()));
    }
}
