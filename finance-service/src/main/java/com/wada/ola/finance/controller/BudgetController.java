package com.wada.ola.finance.controller;

import com.wada.ola.common.dto.ApiResponse;
import com.wada.ola.finance.dto.BudgetActivationRequest;
import com.wada.ola.finance.dto.BudgetRequest;
import com.wada.ola.finance.entity.Budget;
import com.wada.ola.finance.service.BudgetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/finance/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Budget>>> getAll(
            @RequestParam(required = false) Integer fiscalYear) {
        return ResponseEntity.ok(ApiResponse.ok(budgetService.findAll(fiscalYear)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Budget>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(budgetService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Budget>> create(@RequestBody BudgetRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Budget created", budgetService.create(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Budget>> update(@PathVariable Long id,
                                                       @RequestBody BudgetRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Budget updated", budgetService.update(id, request)));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<Budget>> activate(@PathVariable Long id,
                                                         @RequestBody BudgetActivationRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Budget activated", budgetService.activate(id, request)));
    }

    @PatchMapping("/{id}/close")
    public ResponseEntity<ApiResponse<Budget>> close(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Budget closed", budgetService.close(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        budgetService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Budget deleted", null));
    }
}
