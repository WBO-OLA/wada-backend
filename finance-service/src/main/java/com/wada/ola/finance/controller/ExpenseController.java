package com.wada.ola.finance.controller;

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
    public List<Expense> getAll(@RequestParam(required = false) String status) {
        return expenseService.findAll(status);
    }

    @GetMapping("/{id}")
    public Expense getById(@PathVariable Long id) {
        return expenseService.findById(id);
    }

    @PostMapping
    public Expense create(@RequestBody ExpenseRequest request) {
        return expenseService.create(request);
    }

    @PatchMapping("/{id}/approve")
    public Expense approve(@PathVariable Long id, @RequestBody ExpenseApprovalRequest request) {
        return expenseService.approve(id, request);
    }

    @PatchMapping("/{id}/reject")
    public Expense reject(@PathVariable Long id, @RequestBody ExpenseApprovalRequest request) {
        return expenseService.reject(id, request);
    }

    @PatchMapping("/{id}/cancel")
    public Expense cancel(@PathVariable Long id) {
        return expenseService.cancel(id);
    }
}
