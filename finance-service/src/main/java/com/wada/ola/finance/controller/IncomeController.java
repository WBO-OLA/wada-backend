package com.wada.ola.finance.controller;

import com.wada.ola.common.dto.ApiResponse;
import com.wada.ola.finance.dto.IncomeRequest;
import com.wada.ola.finance.entity.Income;
import com.wada.ola.finance.service.IncomeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/finance/incomes")
public class IncomeController {

    private final IncomeService incomeService;

    public IncomeController(IncomeService incomeService) {
        this.incomeService = incomeService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Income>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(incomeService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Income>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(incomeService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Income>> create(@RequestBody IncomeRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Income recorded", incomeService.create(request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        incomeService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Income deleted", null));
    }
}
