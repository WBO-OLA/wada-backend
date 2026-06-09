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

    private final BudgetRepository budgetRepository;
    private final ExpenseRepository expenseRepository;
    private final IncomeRepository incomeRepository;

    public FinanceReportService(BudgetRepository budgetRepository,
                                 ExpenseRepository expenseRepository,
                                 IncomeRepository incomeRepository) {
        this.budgetRepository = budgetRepository;
        this.expenseRepository = expenseRepository;
        this.incomeRepository = incomeRepository;
    }

    public FinanceSummaryDTO getSummary() {
        var budgets = budgetRepository.findAll();
        var expenses = expenseRepository.findAll();
        var incomes = incomeRepository.findAll();

        FinanceSummaryDTO dto = new FinanceSummaryDTO();
        dto.setTotalBudgets(budgets.size());
        dto.setActiveBudgets(budgets.stream().filter(b -> b.getStatus() == BudgetStatus.ACTIVE).count());
        dto.setTotalBudgetAmount(budgets.stream()
                .map(b -> b.getTotalAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        dto.setTotalAllocatedAmount(budgets.stream()
                .map(b -> b.getAllocatedAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        dto.setPendingExpenses(expenses.stream().filter(e -> e.getStatus() == ExpenseStatus.PENDING).count());
        dto.setApprovedExpenses(expenses.stream().filter(e -> e.getStatus() == ExpenseStatus.APPROVED).count());
        dto.setApprovedExpensesTotal(expenses.stream()
                .filter(e -> e.getStatus() == ExpenseStatus.APPROVED)
                .map(e -> e.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        dto.setTotalIncomes(incomes.size());
        dto.setTotalIncomeAmount(incomes.stream()
                .map(i -> i.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        return dto;
    }
}
