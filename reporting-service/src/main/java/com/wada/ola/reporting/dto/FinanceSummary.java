package com.wada.ola.reporting.dto;

import java.math.BigDecimal;

public class FinanceSummary {
    private long totalIncomes;
    private BigDecimal totalIncomeAmount;
    private long totalBudgets;
    private long activeBudgets;
    private BigDecimal totalBudgetAmount;
    private BigDecimal totalAllocatedAmount;
    private long totalExpenses;
    private long pendingExpenses;
    private long approvedExpenses;
    private BigDecimal totalApprovedExpenseAmount;

    public long getTotalIncomes() { return totalIncomes; }
    public void setTotalIncomes(long v) { this.totalIncomes = v; }
    public BigDecimal getTotalIncomeAmount() { return totalIncomeAmount; }
    public void setTotalIncomeAmount(BigDecimal v) { this.totalIncomeAmount = v; }
    public long getTotalBudgets() { return totalBudgets; }
    public void setTotalBudgets(long v) { this.totalBudgets = v; }
    public long getActiveBudgets() { return activeBudgets; }
    public void setActiveBudgets(long v) { this.activeBudgets = v; }
    public BigDecimal getTotalBudgetAmount() { return totalBudgetAmount; }
    public void setTotalBudgetAmount(BigDecimal v) { this.totalBudgetAmount = v; }
    public BigDecimal getTotalAllocatedAmount() { return totalAllocatedAmount; }
    public void setTotalAllocatedAmount(BigDecimal v) { this.totalAllocatedAmount = v; }
    public long getTotalExpenses() { return totalExpenses; }
    public void setTotalExpenses(long v) { this.totalExpenses = v; }
    public long getPendingExpenses() { return pendingExpenses; }
    public void setPendingExpenses(long v) { this.pendingExpenses = v; }
    public long getApprovedExpenses() { return approvedExpenses; }
    public void setApprovedExpenses(long v) { this.approvedExpenses = v; }
    public BigDecimal getTotalApprovedExpenseAmount() { return totalApprovedExpenseAmount; }
    public void setTotalApprovedExpenseAmount(BigDecimal v) { this.totalApprovedExpenseAmount = v; }
}
