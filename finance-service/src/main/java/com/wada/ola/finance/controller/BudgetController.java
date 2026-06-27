package com.wada.ola.finance.controller;

import com.wada.ola.common.annotation.Audited;
import com.wada.ola.finance.dto.BudgetActivationRequest;
import com.wada.ola.finance.dto.BudgetRequest;
import com.wada.ola.finance.entity.Budget;
import com.wada.ola.finance.service.BudgetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/finance/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @GetMapping
    public List<Budget> getAll(@RequestParam(required = false) Integer fiscalYear) {
        return budgetService.findAll(fiscalYear);
    }

    @GetMapping("/{id}")
    public Budget getById(@PathVariable Long id) {
        return budgetService.findById(id);
    }

    @PostMapping
    @Audited(action = "BUDGET_CREATE", targetTable = "budgets")
    public Budget create(@RequestBody BudgetRequest request) {
        return budgetService.create(request);
    }

    @PutMapping("/{id}")
    @Audited(action = "BUDGET_UPDATE", targetTable = "budgets")
    public Budget update(@PathVariable Long id, @RequestBody BudgetRequest request) {
        return budgetService.update(id, request);
    }

    @PatchMapping("/{id}/activate")
    @Audited(action = "BUDGET_ACTIVATE", targetTable = "budgets")
    public Budget activate(@PathVariable Long id, @RequestBody BudgetActivationRequest request) {
        return budgetService.activate(id, request);
    }

    @PatchMapping("/{id}/close")
    @Audited(action = "BUDGET_CLOSE", targetTable = "budgets")
    public Budget close(@PathVariable Long id, @RequestBody(required = false) Map<String, String> body) {
        String closedBy = body != null ? body.getOrDefault("closedBy", "system") : "system";
        return budgetService.close(id, closedBy);
    }

    @DeleteMapping("/{id}")
    @Audited(action = "BUDGET_DELETE", targetTable = "budgets")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        budgetService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
