package com.wbo.finance.controller;

import com.wbo.common.dto.ApiResponse;
import com.wbo.finance.entity.Transaction;
import com.wbo.finance.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/finance/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Transaction>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(transactionService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Transaction>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(transactionService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Transaction>> create(@RequestBody Transaction transaction) {
        return ResponseEntity.ok(ApiResponse.ok("Transaction created", transactionService.create(transaction)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Transaction>> updateStatus(
            @PathVariable Long id,
            @RequestParam Transaction.TransactionStatus status) {
        return ResponseEntity.ok(ApiResponse.ok("Status updated", transactionService.updateStatus(id, status)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        transactionService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Transaction deleted", null));
    }
}
