package com.wada.ola.finance.controller;

import com.wada.ola.common.dto.ApiResponse;
import com.wada.ola.finance.entity.LedgerEntry;
import com.wada.ola.finance.entity.LedgerEntry.EntryType;
import com.wada.ola.finance.service.LedgerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/finance/ledger")
public class LedgerController {

    private final LedgerService ledgerService;

    public LedgerController(LedgerService ledgerService) {
        this.ledgerService = ledgerService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<LedgerEntry>>> getAll(
            @RequestParam(required = false) EntryType type) {
        return ResponseEntity.ok(ApiResponse.ok(ledgerService.findAll(type)));
    }

    @GetMapping("/entity/{type}/{id}")
    public ResponseEntity<ApiResponse<List<LedgerEntry>>> getByEntity(
            @PathVariable("type") String entityType,
            @PathVariable("id") Long entityId) {
        return ResponseEntity.ok(ApiResponse.ok(ledgerService.findByEntity(entityType, entityId)));
    }
}
