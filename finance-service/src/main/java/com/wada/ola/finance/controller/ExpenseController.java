package com.wada.ola.finance.controller;

import com.wada.ola.common.dto.ApiResponse;
import com.wada.ola.finance.dto.ExpenseApprovalRequest;
import com.wada.ola.finance.dto.ExpenseRequest;
import com.wada.ola.finance.entity.Expense;
import com.wada.ola.finance.service.ExpenseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/finance/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Expense>>> getAll(
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(ApiResponse.ok(expenseService.findAll(status)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Expense>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(expenseService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Expense>> create(@RequestBody ExpenseRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Expense submitted", expenseService.create(request)));
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<Expense>> approve(@PathVariable Long id,
                                                         @RequestBody ExpenseApprovalRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Expense approved", expenseService.approve(id, request)));
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<Expense>> reject(@PathVariable Long id,
                                                        @RequestBody ExpenseApprovalRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Expense rejected", expenseService.reject(id, request)));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<Expense>> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Expense cancelled", expenseService.cancel(id)));
    }
}
