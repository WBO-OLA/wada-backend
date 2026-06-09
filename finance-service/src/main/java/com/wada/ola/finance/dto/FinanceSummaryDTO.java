package com.wada.ola.finance.dto;

import java.math.BigDecimal;

public class FinanceSummaryDTO {

    private long totalBudgets;
    private long activeBudgets;
    private BigDecimal totalBudgetAmount;
    private BigDecimal totalAllocatedAmount;
    private long pendingExpenses;
    private long approvedExpenses;
    private BigDecimal approvedExpensesTotal;
    private long totalIncomes;
    private BigDecimal totalIncomeAmount;

    public long getTotalBudgets() { return totalBudgets; }
    public void setTotalBudgets(long totalBudgets) { this.totalBudgets = totalBudgets; }

    public long getActiveBudgets() { return activeBudgets; }
    public void setActiveBudgets(long activeBudgets) { this.activeBudgets = activeBudgets; }

    public BigDecimal getTotalBudgetAmount() { return totalBudgetAmount; }
    public void setTotalBudgetAmount(BigDecimal totalBudgetAmount) { this.totalBudgetAmount = totalBudgetAmount; }

    public BigDecimal getTotalAllocatedAmount() { return totalAllocatedAmount; }
    public void setTotalAllocatedAmount(BigDecimal totalAllocatedAmount) { this.totalAllocatedAmount = totalAllocatedAmount; }

    public long getPendingExpenses() { return pendingExpenses; }
    public void setPendingExpenses(long pendingExpenses) { this.pendingExpenses = pendingExpenses; }

    public long getApprovedExpenses() { return approvedExpenses; }
    public void setApprovedExpenses(long approvedExpenses) { this.approvedExpenses = approvedExpenses; }

    public BigDecimal getApprovedExpensesTotal() { return approvedExpensesTotal; }
    public void setApprovedExpensesTotal(BigDecimal approvedExpensesTotal) { this.approvedExpensesTotal = approvedExpensesTotal; }

    public long getTotalIncomes() { return totalIncomes; }
    public void setTotalIncomes(long totalIncomes) { this.totalIncomes = totalIncomes; }

    public BigDecimal getTotalIncomeAmount() { return totalIncomeAmount; }
    public void setTotalIncomeAmount(BigDecimal totalIncomeAmount) { this.totalIncomeAmount = totalIncomeAmount; }
}
