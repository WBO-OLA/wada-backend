package com.wada.ola.finance.repository;

import com.wada.ola.finance.entity.Expense;
import com.wada.ola.finance.entity.Expense.ExpenseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByStatus(ExpenseStatus status);
    List<Expense> findByBudgetId(Long budgetId);
    List<Expense> findBySubmittedBy(String submittedBy);
    List<Expense> findByCategory(String category);
    List<Expense> findByCommandId(Long commandId);
    List<Expense> findByCommandIdAndStatus(Long commandId, ExpenseStatus status);
    List<Expense> findByCommandIdIn(List<Long> commandIds);
}
