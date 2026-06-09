package com.wada.ola.reporting.dto;

import java.math.BigDecimal;

public class FinanceSummary {
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
    public void setTotalBudgets(long v) { this.totalBudgets = v; }
    public long getActiveBudgets() { return activeBudgets; }
    public void setActiveBudgets(long v) { this.activeBudgets = v; }
    public BigDecimal getTotalBudgetAmount() { return totalBudgetAmount; }
    public void setTotalBudgetAmount(BigDecimal v) { this.totalBudgetAmount = v; }
    public BigDecimal getTotalAllocatedAmount() { return totalAllocatedAmount; }
    public void setTotalAllocatedAmount(BigDecimal v) { this.totalAllocatedAmount = v; }
    public long getPendingExpenses() { return pendingExpenses; }
    public void setPendingExpenses(long v) { this.pendingExpenses = v; }
    public long getApprovedExpenses() { return approvedExpenses; }
    public void setApprovedExpenses(long v) { this.approvedExpenses = v; }
    public BigDecimal getApprovedExpensesTotal() { return approvedExpensesTotal; }
    public void setApprovedExpensesTotal(BigDecimal v) { this.approvedExpensesTotal = v; }
    public long getTotalIncomes() { return totalIncomes; }
    public void setTotalIncomes(long v) { this.totalIncomes = v; }
    public BigDecimal getTotalIncomeAmount() { return totalIncomeAmount; }
    public void setTotalIncomeAmount(BigDecimal v) { this.totalIncomeAmount = v; }
}
