package com.wada.ola.finance.controller;

import com.wada.ola.common.annotation.Audited;
import com.wada.ola.finance.dto.BudgetActivationRequest;
import com.wada.ola.finance.dto.BudgetRequest;
import com.wada.ola.finance.entity.Budget;
import com.wada.ola.finance.security.FinanceAccessGuard;
import com.wada.ola.finance.service.BudgetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/finance/budgets")
public class BudgetController {

    private final BudgetService budgetService;
    private final FinanceAccessGuard accessGuard;

    public BudgetController(BudgetService budgetService, FinanceAccessGuard accessGuard) {
        this.budgetService = budgetService;
        this.accessGuard = accessGuard;
    }

    @GetMapping
    public List<Budget> getAll(
            @RequestParam(required = false) Integer fiscalYear,
            @RequestParam(required = false) Long commandId,
            @RequestHeader(value = "X-Auth-Role", required = false) String authRole,
            @RequestHeader(value = "X-Auth-Command", required = false) String authCommand) {
        Long scopedCommandId = accessGuard.resolveScopedCommandId(authRole, authCommand, commandId);
        return budgetService.findAll(fiscalYear, scopedCommandId);
    }

    @GetMapping("/{id}")
    public Budget getById(
            @PathVariable Long id,
            @RequestHeader(value = "X-Auth-Role", required = false) String authRole,
            @RequestHeader(value = "X-Auth-Command", required = false) String authCommand) {
        Budget budget = budgetService.findById(id);
        accessGuard.assertCommandInScope(authRole, authCommand, budget.getCommandId());
        return budget;
    }

    @PostMapping
    @Audited(action = "BUDGET_CREATE", targetTable = "budgets")
    public Budget create(
            @RequestBody BudgetRequest request,
            @RequestHeader(value = "X-Auth-Role", required = false) String authRole,
            @RequestHeader(value = "X-Auth-Command", required = false) String authCommand) {
        accessGuard.assertCommandInScope(authRole, authCommand, request.getCommandId());
        return budgetService.create(request);
    }

    @PutMapping("/{id}")
    @Audited(action = "BUDGET_UPDATE", targetTable = "budgets")
    public Budget update(
            @PathVariable Long id,
            @RequestBody BudgetRequest request,
            @RequestHeader(value = "X-Auth-Role", required = false) String authRole,
            @RequestHeader(value = "X-Auth-Command", required = false) String authCommand) {
        accessGuard.assertCommandInScope(authRole, authCommand, budgetService.findById(id).getCommandId());
        return budgetService.update(id, request);
    }

    @PatchMapping("/{id}/activate")
    @Audited(action = "BUDGET_ACTIVATE", targetTable = "budgets")
    public Budget activate(
            @PathVariable Long id,
            @RequestBody BudgetActivationRequest request,
            @RequestHeader(value = "X-Auth-Role", required = false) String authRole,
            @RequestHeader(value = "X-Auth-Command", required = false) String authCommand) {
        accessGuard.assertCommandInScope(authRole, authCommand, budgetService.findById(id).getCommandId());
        return budgetService.activate(id, request);
    }

    @PatchMapping("/{id}/close")
    @Audited(action = "BUDGET_CLOSE", targetTable = "budgets")
    public Budget close(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body,
            @RequestHeader(value = "X-Auth-Role", required = false) String authRole,
            @RequestHeader(value = "X-Auth-Command", required = false) String authCommand) {
        accessGuard.assertCommandInScope(authRole, authCommand, budgetService.findById(id).getCommandId());
        String closedBy = body != null ? body.getOrDefault("closedBy", "system") : "system";
        return budgetService.close(id, closedBy);
    }

    @DeleteMapping("/{id}")
    @Audited(action = "BUDGET_DELETE", targetTable = "budgets")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @RequestHeader(value = "X-Auth-Role", required = false) String authRole,
            @RequestHeader(value = "X-Auth-Command", required = false) String authCommand) {
        accessGuard.assertCommandInScope(authRole, authCommand, budgetService.findById(id).getCommandId());
        budgetService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
