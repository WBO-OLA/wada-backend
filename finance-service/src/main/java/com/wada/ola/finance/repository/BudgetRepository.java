package com.wada.ola.finance.repository;

import com.wada.ola.finance.entity.Budget;
import com.wada.ola.finance.entity.Budget.BudgetStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    List<Budget> findByFiscalYear(Integer fiscalYear);
    List<Budget> findByStatus(BudgetStatus status);
    List<Budget> findByDepartment(String department);
    List<Budget> findByCommandId(Long commandId);
    List<Budget> findByCommandIdIn(List<Long> commandIds);
}
