package com.wada.ola.finance.service;

import com.wada.ola.finance.dto.FinanceSummaryDTO;
import com.wada.ola.finance.entity.Budget.BudgetStatus;
import com.wada.ola.finance.entity.Expense.ExpenseStatus;
import com.wada.ola.finance.repository.BudgetRepository;
import com.wada.ola.finance.repository.ExpenseRepository;
import com.wada.ola.finance.repository.IncomeRepository;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
public class FinanceReportService {

    private final IncomeRepository incomeRepository;
    private final BudgetRepository budgetRepository;
    private final ExpenseRepository expenseRepository;

    public FinanceReportService(IncomeRepository incomeRepository,
                                 BudgetRepository budgetRepository,
                                 ExpenseRepository expenseRepository) {
        this.incomeRepository = incomeRepository;
        this.budgetRepository = budgetRepository;
        this.expenseRepository = expenseRepository;
    }

    public FinanceSummaryDTO getSummary() {
        var incomes = incomeRepository.findAll();
        var budgets = budgetRepository.findAll();
        var activeBudgets = budgetRepository.findByStatus(BudgetStatus.ACTIVE);
        var expenses = expenseRepository.findAll();
        var approvedExpenses = expenseRepository.findByStatus(ExpenseStatus.APPROVED);
        var pendingExpenses = expenseRepository.findByStatus(ExpenseStatus.PENDING);

        FinanceSummaryDTO dto = new FinanceSummaryDTO();

        dto.setTotalIncomes(incomes.size());
        dto.setTotalIncomeAmount(incomes.stream()
                .map(i -> i.getAmount() != null ? i.getAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        dto.setTotalBudgets(budgets.size());
        dto.setActiveBudgets(activeBudgets.size());
        dto.setTotalBudgetAmount(budgets.stream()
                .map(b -> b.getTotalAmount() != null ? b.getTotalAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        dto.setTotalAllocatedAmount(budgets.stream()
                .map(b -> b.getAllocatedAmount() != null ? b.getAllocatedAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        dto.setTotalExpenses(expenses.size());
        dto.setPendingExpenses(pendingExpenses.size());
        dto.setApprovedExpenses(approvedExpenses.size());
        dto.setTotalApprovedExpenseAmount(approvedExpenses.stream()
                .map(e -> e.getAmount() != null ? e.getAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        return dto;
    }
}
