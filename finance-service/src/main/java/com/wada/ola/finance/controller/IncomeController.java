package com.wada.ola.finance.controller;

import com.wada.ola.common.annotation.Audited;
import com.wada.ola.common.dto.ApiResponse;
import com.wada.ola.finance.dto.IncomeRequest;
import com.wada.ola.finance.entity.Income;
import com.wada.ola.finance.security.FinanceAccessGuard;
import com.wada.ola.finance.service.IncomeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/finance/incomes")
public class IncomeController {

    private final IncomeService incomeService;
    private final FinanceAccessGuard accessGuard;

    public IncomeController(IncomeService incomeService, FinanceAccessGuard accessGuard) {
        this.incomeService = incomeService;
        this.accessGuard = accessGuard;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Income>>> getAll(
            @RequestParam(required = false) Long commandId,
            @RequestHeader(value = "X-Auth-Role", required = false) String authRole,
            @RequestHeader(value = "X-Auth-Command", required = false) String authCommand) {
        Long scopedCommandId = accessGuard.resolveScopedCommandId(authRole, authCommand, commandId);
        return ResponseEntity.ok(ApiResponse.ok(incomeService.findAll(scopedCommandId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Income>> getById(
            @PathVariable Long id,
            @RequestHeader(value = "X-Auth-Role", required = false) String authRole,
            @RequestHeader(value = "X-Auth-Command", required = false) String authCommand) {
        Income income = incomeService.findById(id);
        accessGuard.assertCommandInScope(authRole, authCommand, income.getCommandId());
        return ResponseEntity.ok(ApiResponse.ok(income));
    }

    @PostMapping
    @Audited(action = "INCOME_CREATE", targetTable = "incomes")
    public ResponseEntity<ApiResponse<Income>> create(
            @RequestBody IncomeRequest request,
            @RequestHeader(value = "X-Auth-Role", required = false) String authRole,
            @RequestHeader(value = "X-Auth-Command", required = false) String authCommand) {
        accessGuard.assertCommandInScope(authRole, authCommand, request.getCommandId());
        return ResponseEntity.ok(ApiResponse.ok("Income recorded", incomeService.create(request)));
    }

    @DeleteMapping("/{id}")
    @Audited(action = "INCOME_DELETE", targetTable = "incomes")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id,
            @RequestHeader(value = "X-Auth-Role", required = false) String authRole,
            @RequestHeader(value = "X-Auth-Command", required = false) String authCommand) {
        accessGuard.assertCommandInScope(authRole, authCommand, incomeService.findById(id).getCommandId());
        incomeService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Income deleted", null));
    }

    @GetMapping("/aggregate")
    public ResponseEntity<Map<String, Object>> aggregate() {
        Map<String, BigDecimal> byGroup = incomeService.aggregateByGroup();
        Map<String, BigDecimal> byCountry = incomeService.aggregateByCountry();
        BigDecimal globalTotal = incomeService.globalTotal();
        return ResponseEntity.ok(Map.of(
                "byGroup", byGroup,
                "byCountry", byCountry,
                "globalTotal", globalTotal
        ));
    }
}
