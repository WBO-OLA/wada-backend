package com.wada.ola.finance.service;

import com.wada.ola.common.exception.ResourceNotFoundException;
import com.wada.ola.finance.dto.BudgetActivationRequest;
import com.wada.ola.finance.dto.BudgetRequest;
import com.wada.ola.finance.entity.Budget;
import com.wada.ola.finance.entity.Budget.BudgetStatus;
import com.wada.ola.finance.entity.LedgerEntry;
import com.wada.ola.finance.entity.LedgerEntry.EntryType;
import com.wada.ola.finance.repository.BudgetRepository;
import com.wada.ola.finance.repository.LedgerEntryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final LedgerEntryRepository ledgerEntryRepository;

    public BudgetService(BudgetRepository budgetRepository, LedgerEntryRepository ledgerEntryRepository) {
        this.budgetRepository = budgetRepository;
        this.ledgerEntryRepository = ledgerEntryRepository;
    }

    public List<Budget> findAll(Integer fiscalYear) {
        if (fiscalYear != null) return budgetRepository.findByFiscalYear(fiscalYear);
        return budgetRepository.findAll();
    }

    public Budget findById(Long id) {
        return budgetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found with id: " + id));
    }

    @Transactional
    public Budget create(BudgetRequest request) {
        Budget budget = new Budget();
        budget.setName(request.getName());
        budget.setFiscalYear(request.getFiscalYear());
        budget.setTotalAmount(request.getTotalAmount());
        budget.setAllocatedAmount(BigDecimal.ZERO);
        budget.setDepartment(request.getDepartment());
        budget.setDescription(request.getDescription());
        budget.setCreatedBy(request.getCreatedBy());
        budget.setNotes(request.getNotes());
        budget.setStatus(BudgetStatus.DRAFT);
        Budget saved = budgetRepository.save(budget);
        recordLedger(EntryType.BUDGET_CREATED, saved.getTotalAmount(),
                "Budget created: " + saved.getName(), "Budget", saved.getId(), request.getCreatedBy());
        return saved;
    }

    @Transactional
    public Budget update(Long id, BudgetRequest request) {
        Budget budget = findById(id);
        if (budget.getStatus() != BudgetStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT budgets can be updated.");
        }
        budget.setName(request.getName());
        budget.setFiscalYear(request.getFiscalYear());
        budget.setTotalAmount(request.getTotalAmount());
        budget.setDepartment(request.getDepartment());
        budget.setDescription(request.getDescription());
        budget.setNotes(request.getNotes());
        return budgetRepository.save(budget);
    }

    @Transactional
    public Budget activate(Long id, BudgetActivationRequest request) {
        Budget budget = findById(id);
        if (budget.getStatus() != BudgetStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT budgets can be activated.");
        }
        budget.setStatus(BudgetStatus.ACTIVE);
        budget.setApprovedBy(request.getApprovedBy());
        budget.setApprovedAt(LocalDateTime.now());
        Budget saved = budgetRepository.save(budget);
        recordLedger(EntryType.BUDGET_ACTIVATED, saved.getTotalAmount(),
                "Budget activated: " + saved.getName(), "Budget", saved.getId(), request.getApprovedBy());
        return saved;
    }

    @Transactional
    public Budget close(Long id) {
        Budget budget = findById(id);
        if (budget.getStatus() != BudgetStatus.ACTIVE) {
            throw new IllegalStateException("Only ACTIVE budgets can be closed.");
        }
        budget.setStatus(BudgetStatus.CLOSED);
        Budget saved = budgetRepository.save(budget);
        recordLedger(EntryType.BUDGET_CLOSED, saved.getTotalAmount(),
                "Budget closed: " + saved.getName(), "Budget", saved.getId(), null);
        return saved;
    }

    public void delete(Long id) {
        Budget budget = findById(id);
        if (budget.getStatus() != BudgetStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT budgets can be deleted.");
        }
        budgetRepository.deleteById(id);
    }

    private void recordLedger(EntryType type, BigDecimal amount, String description,
                               String entityType, Long entityId, String createdBy) {
        LedgerEntry entry = new LedgerEntry();
        entry.setType(type);
        entry.setAmount(amount);
        entry.setDescription(description);
        entry.setRelatedEntityType(entityType);
        entry.setRelatedEntityId(entityId);
        entry.setCreatedBy(createdBy);
        ledgerEntryRepository.save(entry);
    }
}
