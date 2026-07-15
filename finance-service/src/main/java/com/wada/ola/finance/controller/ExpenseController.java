package com.wada.ola.finance.controller;

import com.wada.ola.common.annotation.Audited;
import com.wada.ola.finance.dto.ExpenseApprovalRequest;
import com.wada.ola.finance.dto.ExpenseRequest;
import com.wada.ola.finance.entity.Expense;
import com.wada.ola.finance.security.FinanceAccessGuard;
import com.wada.ola.finance.service.ExpenseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/finance/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;
    private final FinanceAccessGuard accessGuard;

    public ExpenseController(ExpenseService expenseService, FinanceAccessGuard accessGuard) {
        this.expenseService = expenseService;
        this.accessGuard = accessGuard;
    }

    @GetMapping
    public List<Expense> getAll(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long commandId,
            @RequestHeader(value = "X-Auth-Role", required = false) String authRole,
            @RequestHeader(value = "X-Auth-Command", required = false) String authCommand) {
        Long scopedCommandId = accessGuard.resolveScopedCommandId(authRole, authCommand, commandId);
        return expenseService.findAll(status, scopedCommandId);
    }

    @GetMapping("/{id}")
    public Expense getById(
            @PathVariable Long id,
            @RequestHeader(value = "X-Auth-Role", required = false) String authRole,
            @RequestHeader(value = "X-Auth-Command", required = false) String authCommand) {
        Expense expense = expenseService.findById(id);
        accessGuard.assertCommandInScope(authRole, authCommand, expense.getCommandId());
        return expense;
    }

    @PostMapping
    @Audited(action = "EXPENSE_CREATE", targetTable = "expenses")
    public Expense create(
            @RequestBody ExpenseRequest request,
            @RequestHeader(value = "X-Auth-Role", required = false) String authRole,
            @RequestHeader(value = "X-Auth-Command", required = false) String authCommand) {
        accessGuard.assertCommandInScope(authRole, authCommand, request.getCommandId());
        return expenseService.create(request);
    }

    @PatchMapping("/{id}/approve")
    @Audited(action = "EXPENSE_APPROVE", targetTable = "expenses")
    public Expense approve(
            @PathVariable Long id,
            @RequestBody ExpenseApprovalRequest request,
            @RequestHeader(value = "X-Auth-Role", required = false) String authRole,
            @RequestHeader(value = "X-Auth-Command", required = false) String authCommand) {
        accessGuard.assertCommandInScope(authRole, authCommand, expenseService.findById(id).getCommandId());
        return expenseService.approve(id, request);
    }

    @PatchMapping("/{id}/reject")
    @Audited(action = "EXPENSE_REJECT", targetTable = "expenses")
    public Expense reject(
            @PathVariable Long id,
            @RequestBody ExpenseApprovalRequest request,
            @RequestHeader(value = "X-Auth-Role", required = false) String authRole,
            @RequestHeader(value = "X-Auth-Command", required = false) String authCommand) {
        accessGuard.assertCommandInScope(authRole, authCommand, expenseService.findById(id).getCommandId());
        return expenseService.reject(id, request);
    }

    @PatchMapping("/{id}/cancel")
    @Audited(action = "EXPENSE_CANCEL", targetTable = "expenses")
    public Expense cancel(
            @PathVariable Long id,
            @RequestHeader(value = "X-Auth-Role", required = false) String authRole,
            @RequestHeader(value = "X-Auth-Command", required = false) String authCommand) {
        accessGuard.assertCommandInScope(authRole, authCommand, expenseService.findById(id).getCommandId());
        return expenseService.cancel(id);
    }
}
