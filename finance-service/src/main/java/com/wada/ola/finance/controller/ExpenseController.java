package com.wada.ola.finance.controller;

import com.wada.ola.common.annotation.Audited;
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
    public List<Expense> getAll(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long commandId,
            @RequestHeader(value = "X-Auth-Role", required = false) String authRole,
            @RequestHeader(value = "X-Auth-Command", required = false) String authCommand) {
        Long scopedCommandId = resolveCommandId(authRole, authCommand, commandId);
        return expenseService.findAll(status, scopedCommandId);
    }

    private static Long resolveCommandId(String role, String authCommand, Long requested) {
        boolean global = role == null || role.equals("CHIEF") || role.equals("ADMIN");
        if (global) return requested;
        if (authCommand != null && !authCommand.isBlank()) return Long.parseLong(authCommand);
        return requested;
    }

    @GetMapping("/{id}")
    public Expense getById(@PathVariable Long id) {
        return expenseService.findById(id);
    }

    @PostMapping
    @Audited(action = "EXPENSE_CREATE", targetTable = "expenses")
    public Expense create(@RequestBody ExpenseRequest request) {
        return expenseService.create(request);
    }

    @PatchMapping("/{id}/approve")
    @Audited(action = "EXPENSE_APPROVE", targetTable = "expenses")
    public Expense approve(@PathVariable Long id, @RequestBody ExpenseApprovalRequest request) {
        return expenseService.approve(id, request);
    }

    @PatchMapping("/{id}/reject")
    @Audited(action = "EXPENSE_REJECT", targetTable = "expenses")
    public Expense reject(@PathVariable Long id, @RequestBody ExpenseApprovalRequest request) {
        return expenseService.reject(id, request);
    }

    @PatchMapping("/{id}/cancel")
    @Audited(action = "EXPENSE_CANCEL", targetTable = "expenses")
    public Expense cancel(@PathVariable Long id) {
        return expenseService.cancel(id);
    }
}
